package com.example.ecommerce.controller.analytics;

import com.example.ecommerce.service.analytics.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return analyticsService.summary();
    }

    @GetMapping("/orders/last7days")
    public Map<String, Object> ordersLast7() {
        return analyticsService.ordersPerDayLast7();
    }
}

