package com.example.ecommerce.dto.request.order;

import jakarta.validation.constraints.NotBlank;

public class PlaceOrderRequest {
    @NotBlank
    private String paymentMethod; // e.g., RAZORPAY

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

