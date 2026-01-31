package com.example.ecommerce.service.cart;

import com.example.ecommerce.dto.request.cart.AddCartItemRequest;
import com.example.ecommerce.dto.response.cart.CartItemResponse;
import com.example.ecommerce.dto.response.cart.CartResponse;
import com.example.ecommerce.exception.custom.BadRequestException;
import com.example.ecommerce.exception.custom.ResourceNotFoundException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.embedded.CartItem;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (request.getQuantity() > product.getStock()) {
            throw new BadRequestException("Insufficient stock");
        }
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUserId(userId);
            return c;
        });
        List<CartItem> items = new ArrayList<>(cart.getItems());
        CartItem existing = items.stream().filter(i -> i.getProductId().equals(product.getId())).findFirst().orElse(null);
        if (existing == null) {
            CartItem item = new CartItem();
            item.setProductId(product.getId());
            item.setQuantity(request.getQuantity());
            items.add(item);
        } else {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
        }
        cart.setItems(items);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateQuantity(String userId, String productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(quantity),
                        () -> { throw new ResourceNotFoundException("Item not found in cart"); });
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart c = new Cart();
            c.setUserId(userId);
            return c;
        });
        return toResponse(cart);
    }

    private CartResponse toResponse(Cart cart) {
        CartResponse resp = new CartResponse();
        resp.setId(cart.getId());
        List<CartItemResponse> items = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            Product p = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            CartItemResponse cir = new CartItemResponse();
            cir.setProductId(item.getProductId());
            cir.setQuantity(item.getQuantity());
            cir.setName(p.getName());
            cir.setPrice(p.getPrice());
            items.add(cir);
        }
        resp.setItems(items);
        return resp;
    }
}

