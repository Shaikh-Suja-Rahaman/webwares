package com.example.ecommerce.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LOGIN_LIMIT = 10; // per minute
    private static final int ORDER_LIMIT = 5;  // per minute
    private static final long WINDOW_MILLIS = 60_000;

    private final Map<String, WindowCounter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login")) {
            if (isRateLimited(key(request), LOGIN_LIMIT)) {
                reject(response, "Too many login attempts");
                return;
            }
        } else if (path.startsWith("/api/orders")) {
            if (isRateLimited(key(request), ORDER_LIMIT)) {
                reject(response, "Too many order requests");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String key(HttpServletRequest request) {
        return request.getRemoteAddr() + ":" + request.getRequestURI();
    }

    private boolean isRateLimited(String key, int limit) {
        long now = Instant.now().toEpochMilli();
        WindowCounter counter = counters.computeIfAbsent(key, k -> new WindowCounter(now));
        synchronized (counter) {
            if (now - counter.windowStart >= WINDOW_MILLIS) {
                counter.windowStart = now;
                counter.count.set(0);
            }
            int current = counter.count.incrementAndGet();
            return current > limit;
        }
    }

    private void reject(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"RATE_LIMIT\",\"message\":\"" + message + "\"}");
    }

    private static class WindowCounter {
        long windowStart;
        AtomicInteger count = new AtomicInteger();
        WindowCounter(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}

