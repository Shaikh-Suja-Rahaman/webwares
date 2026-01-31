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
import com.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse addItem(String userEmail, AddCartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (request.getQuantity() > product.getStock()) {
            throw new BadRequestException("Insufficient stock");
        }
        String userId = resolveUserId(userEmail);
        Cart cart = findOrMigrateCart(userId, userEmail);
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
    public CartResponse updateQuantity(String userEmail, String productId, int quantity) {
        String userId = resolveUserId(userEmail);
        Cart cart = findOrMigrateCart(userId, userEmail);
        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(item -> item.setQuantity(quantity),
                        () -> { throw new ResourceNotFoundException("Item not found in cart"); });
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String userEmail, String productId) {
        String userId = resolveUserId(userEmail);
        Cart cart = findOrMigrateCart(userId, userEmail);
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse getCart(String userEmail) {
        String userId = resolveUserId(userEmail);
        Cart cart = findOrCreateCart(userId, userEmail);
        return toResponse(cart);
    }

    private String resolveUserId(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();
    }

    private Cart findOrCreateCart(String userId, String userEmail) {
        return cartRepository.findByUserId(userId)
                .or(() -> cartRepository.findByUserId(userEmail)) // migrate old carts stored by email
                .map(cart -> {
                    if (!cart.getUserId().equals(userId)) {
                        cart.setUserId(userId);
                        cartRepository.save(cart);
                    }
                    return cart;
                })
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    return c;
                });
    }

    private Cart findOrMigrateCart(String userId, String userEmail) {
        Cart cart = findOrCreateCart(userId, userEmail);
        if (cart.getUserId().equals(userEmail)) {
            cart.setUserId(userId);
            cart = cartRepository.save(cart);
        }
        return cart;
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
