package com.example.ecommerce.service.order;

import com.example.ecommerce.dto.mapper.OrderMapper;
import com.example.ecommerce.dto.request.order.PlaceOrderRequest;
import com.example.ecommerce.dto.response.order.OrderResponse;
import com.example.ecommerce.exception.custom.BadRequestException;
import com.example.ecommerce.exception.custom.ResourceNotFoundException;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.embedded.OrderItem;
import com.example.ecommerce.model.enums.OrderStatus;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.app.EmailService;
import com.example.ecommerce.service.payment.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    private final EmailService emailService;

    public OrderService(CartRepository cartRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            PaymentService paymentService,
            OrderMapper orderMapper,
            EmailService emailService) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.orderMapper = orderMapper;
        this.emailService = emailService;
    }

    @Transactional
    public OrderResponse placeOrder(String userId, PlaceOrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }
        // Build order items and compute total with stock check
        List<OrderItem> items = cart.getItems().stream().map(ci -> {
            Product p = productRepository.findById(ci.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            if (ci.getQuantity() > p.getStock()) {
                throw new BadRequestException("Insufficient stock for product: " + p.getName());
            }
            OrderItem oi = new OrderItem();
            oi.setProductId(p.getId());
            oi.setName(p.getName());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            // deduct stock
            p.setStock(p.getStock() - ci.getQuantity());
            productRepository.save(p);
            return oi;
        }).collect(Collectors.toList());

        double total = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(items);
        order.setTotal(total);
        order.setStatus(OrderStatus.CREATED);
        order = orderRepository.save(order);

        // Create payment intent (mock Razorpay integration)
        String providerOrderId = paymentService.createPaymentIntent(order.getId(), total);
        order.setPaymentId(providerOrderId);
        orderRepository.save(order);

        // Clear cart
        cart.setItems(List.of());
        cartRepository.save(cart);

        try {
            emailService.send(userId, "Order Placed",
                    "Your order " + order.getId() + " is created with total " + total);
        } catch (Exception e) {
            System.err.println("Failed to send order email: " + e.getMessage());
        }
        return orderMapper.toResponse(order);
    }

    public List<OrderResponse> userOrders(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> allOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse updateStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(newStatus);
        orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    public OrderResponse getById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return orderMapper.toResponse(order);
    }
}
