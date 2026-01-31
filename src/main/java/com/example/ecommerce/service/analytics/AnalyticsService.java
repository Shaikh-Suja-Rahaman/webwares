package com.example.ecommerce.service.analytics;

import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public AnalyticsService(UserRepository userRepository,
                            OrderRepository orderRepository,
                            ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Map<String, Object> summary() {
        long users = userRepository.count();
        long orders = orderRepository.count();
        double revenue = orderRepository.findAll().stream().mapToDouble(o -> o.getTotal()).sum();
        return Map.of("totalUsers", users, "totalOrders", orders, "totalRevenue", revenue);
    }

    public Map<String, Object> ordersPerDayLast7() {
        Instant now = Instant.now();
        Instant start = now.minus(7, ChronoUnit.DAYS);
        long count = orderRepository.countByCreatedAtBetween(start, now);
        return Map.of("range", "last7days", "orders", count);
    }
}

