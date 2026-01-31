package com.example.ecommerce.controller.cart;

import com.example.ecommerce.dto.request.cart.AddCartItemRequest;
import com.example.ecommerce.dto.response.cart.CartResponse;
import com.example.ecommerce.service.cart.CartService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public CartResponse addItem(@AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(user.getUsername(), request);
    }

    @PutMapping("/items/{productId}")
    public CartResponse updateQuantity(@AuthenticationPrincipal UserDetails user,
            @PathVariable String productId,
            @RequestParam int quantity) {
        return cartService.updateQuantity(user.getUsername(), productId, quantity);
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse removeItem(@AuthenticationPrincipal UserDetails user,
            @PathVariable String productId) {
        return cartService.removeItem(user.getUsername(), productId);
    }

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal UserDetails user) {
        return cartService.getCart(user.getUsername());
    }
}
