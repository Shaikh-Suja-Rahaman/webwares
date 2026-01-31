package com.example.ecommerce.dto.mapper;

import com.example.ecommerce.dto.response.order.OrderItemResponse;
import com.example.ecommerce.dto.response.order.OrderResponse;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.embedded.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus());
        response.setPaymentId(order.getPaymentId());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()));
        return response;
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        OrderItemResponse resp = new OrderItemResponse();
        resp.setProductId(item.getProductId());
        resp.setName(item.getName());
        resp.setQuantity(item.getQuantity());
        resp.setPrice(item.getPrice());
        return resp;
    }
}

