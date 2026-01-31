package com.example.ecommerce.config.app;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Bean
    public RazorpayClient razorpayClient(@Value("${razorpay.key-id}") String keyId,
                                         @Value("${razorpay.secret}") String secret) throws Exception {
        return new RazorpayClient(keyId, secret);
    }
}

