package com.example.ecommerce.controller.payment;

import com.example.ecommerce.dto.response.payment.PaymentResponse;
import com.example.ecommerce.service.payment.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/razorpay/key")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Map<String, String> getRazorpayKey() {
        return Map.of("keyId", paymentService.getRazorpayKeyId());
    }

    @PostMapping("/razorpay/verify")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public PaymentResponse verifyPayment(@RequestParam("razorpay_order_id") String razorpayOrderId,
                                         @RequestParam("razorpay_payment_id") String razorpayPaymentId,
                                         @RequestParam("razorpay_signature") String razorpaySignature) {
        return paymentService.verifyRazorpayPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);
    }

    @PostMapping("/webhook/mock")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse mockWebhook(@RequestParam String providerPaymentId,
                                       @RequestParam String status) {
        return paymentService.mockWebhook(providerPaymentId, status);
    }
}
