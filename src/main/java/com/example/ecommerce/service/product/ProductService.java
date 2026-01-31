package com.example.ecommerce.service.product;

import com.example.ecommerce.dto.mapper.ProductMapper;
import com.example.ecommerce.dto.request.product.ProductRequest;
import com.example.ecommerce.dto.response.product.ProductResponse;
import com.example.ecommerce.exception.custom.ResourceNotFoundException;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.util.storage.FileStorageService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileStorageService fileStorageService;

    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper,
                          FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    @CacheEvict(value = {"product-list"}, allEntries = true)
    public ProductResponse create(ProductRequest request) {
        String imageUrl = uploadIfPresent(request);
        Product product = productMapper.toEntity(request, imageUrl);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = {"product-by-id", "product-list"}, allEntries = true)
    public ProductResponse update(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        String imageUrl = uploadIfPresent(request);
        productMapper.updateEntity(product, request, imageUrl);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = {"product-by-id", "product-list"}, allEntries = true)
    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Cacheable(value = "product-by-id", key = "#id")
    public ProductResponse getById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toResponse(product);
    }

    @Cacheable(value = "product-list", key = "#page + '-' + #size + '-' + #sort + '-' + #category + '-' + #minPrice + '-' + #maxPrice + '-' + #available")
    public List<ProductResponse> list(int page, int size, String sort, String category, Double minPrice, Double maxPrice, Boolean available) {
        String[] sortParts = sort.split(",");
        Sort.Direction dir = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(dir, sortParts[0]));
        double min = minPrice != null ? minPrice : 0;
        double max = maxPrice != null ? maxPrice : Double.MAX_VALUE;
        String cat = category != null ? category : "";
        // Simplified filter: category contains and price range
        return productRepository.findByCategoryContainingIgnoreCaseAndPriceBetween(cat, min, max, pageable)
                .stream()
                .filter(p -> available == null || (available ? p.getStock() > 0 : p.getStock() == 0))
                .map(productMapper::toResponse)
                .toList();
    }

    private String uploadIfPresent(ProductRequest request) {
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            try {
                return fileStorageService.store(request.getImage());
            } catch (IOException e) {
                throw new RuntimeException("Failed to store image", e);
            }
        }
        return null;
    }
}
