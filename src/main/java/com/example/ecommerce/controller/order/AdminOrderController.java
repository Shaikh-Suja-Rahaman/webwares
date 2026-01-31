package com.example.ecommerce.controller.order;

import com.example.ecommerce.dto.response.order.OrderResponse;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.service.order.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> allOrders() {
        return orderService.allOrders();
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateStatus(@PathVariable String orderId,
                                      @RequestParam("status") OrderStatus status) {
        return orderService.updateStatus(orderId, status);
    }
}
