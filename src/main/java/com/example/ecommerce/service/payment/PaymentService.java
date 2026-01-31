package com.example.ecommerce.service.payment;

import com.example.ecommerce.dto.response.payment.PaymentResponse;
import com.example.ecommerce.exception.custom.BadRequestException;
import com.example.ecommerce.exception.custom.ResourceNotFoundException;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final String razorpayKeyId;
    private final String razorpaySecret;

    public PaymentService(RazorpayClient razorpayClient,
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            @Value("${razorpay.key-id}") String razorpayKeyId,
            @Value("${razorpay.secret}") String razorpaySecret) {
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.razorpayKeyId = razorpayKeyId;
        this.razorpaySecret = razorpaySecret;
    }

    public String createPaymentIntent(String orderId, double amount) {
        JSONObject options = new JSONObject();
        options.put("amount", (int) (amount * 100)); // paise
        options.put("currency", "INR");
        options.put("receipt", orderId);
        try {
            com.razorpay.Order providerOrder = razorpayClient.orders.create(options);
            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setProvider("RAZORPAY");
            payment.setProviderOrderId(providerOrder.get("id"));
            payment.setProviderPaymentId(providerOrder.get("id")); // set for backward compatibility; overwritten after capture
            payment.setStatus("CREATED");
            payment.setAmount(amount);
            paymentRepository.save(payment);
            return providerOrder.get("id");
        } catch (RazorpayException e) {
            throw new BadRequestException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    public PaymentResponse verifyRazorpayPayment(String razorpayOrderId,
                                                 String razorpayPaymentId,
                                                 String razorpaySignature) {
        JSONObject payload = new JSONObject();
        payload.put("razorpay_order_id", razorpayOrderId);
        payload.put("razorpay_payment_id", razorpayPaymentId);
        payload.put("razorpay_signature", razorpaySignature);

        try {
            boolean isValid = Utils.verifyPaymentSignature(payload, razorpaySecret);
            if (!isValid) {
                throw new BadRequestException("Invalid Razorpay signature");
            }
        } catch (RazorpayException e) {
            throw new BadRequestException("Failed to verify Razorpay signature: " + e.getMessage());
        }

        Payment payment = paymentRepository.findByProviderOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order"));
        payment.setProviderPaymentId(razorpayPaymentId);
        payment.setProviderSignature(razorpaySignature);
        payment.setStatus("PAID");
        paymentRepository.save(payment);

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.PAID);
        order.setPaymentId(payment.getProviderPaymentId());
        orderRepository.save(order);

        PaymentResponse resp = new PaymentResponse();
        resp.setOrderId(order.getId());
        resp.setProviderOrderId(payment.getProviderOrderId());
        resp.setStatus(payment.getStatus());
        resp.setAmount(payment.getAmount());
        return resp;
    }

    public PaymentResponse mockWebhook(String providerPaymentId, String status) {
        Payment payment = paymentRepository.findByProviderPaymentId(providerPaymentId)
            .or(() -> paymentRepository.findByProviderOrderId(providerPaymentId))
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        payment.setStatus(status);
        paymentRepository.save(payment);
        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if ("PAID".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.PAID);
        } else if ("FAILED".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.CANCELLED);
        }
        orderRepository.save(order);
        PaymentResponse resp = new PaymentResponse();
        resp.setOrderId(order.getId());
        resp.setProviderOrderId(payment.getProviderOrderId());
        resp.setStatus(status);
        resp.setAmount(payment.getAmount());
        return resp;
    }

    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }
}
