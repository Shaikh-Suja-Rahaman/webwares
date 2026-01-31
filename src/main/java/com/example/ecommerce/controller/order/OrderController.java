package com.example.ecommerce.controller.order;

import com.example.ecommerce.dto.request.order.PlaceOrderRequest;
import com.example.ecommerce.dto.response.order.OrderResponse;
import com.example.ecommerce.service.order.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse placeOrder(@AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(user.getUsername(), request);
    }

    @GetMapping
    public List<OrderResponse> myOrders(@AuthenticationPrincipal UserDetails user) {
        return orderService.userOrders(user.getUsername());
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable String id) {
        return orderService.getById(id);
    }
}
