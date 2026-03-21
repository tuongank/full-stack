package com.project.ecommerce.service;

import com.project.ecommerce.dto.request.OrderRequest;
import com.project.ecommerce.dto.response.OrderResponse;
import com.project.ecommerce.entity.*;
import com.project.ecommerce.enums.*;
import com.project.ecommerce.exception.IllegalArgumentException;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.mapper.AddressMapper;
import com.project.ecommerce.mapper.OrderItemMapper;
import com.project.ecommerce.mapper.OrderMapper;
import com.project.ecommerce.mapper.PaymentMapper;
import com.project.ecommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final AddressMapper addressMapper;
    private final UserService userService;
    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final UserItemInteractionService userItemInteractionService;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        User user = userService.getLoggedInUser();

        // Validation
        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order Items cannot be empty");
        }

        if (orderRequest.getOrderItems().stream().anyMatch(i -> i.getQuantity() <= 0)) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Validate address belongs to user
        Address address = addressRepository.findByIdAndUserId(orderRequest.getAddressId(), user.getId())
                .orElseThrow(() -> new NotFoundException("Address not found"));

        // Create order first (don't reassign to keep it effectively final)
        Order order = Order.builder()
                .user(user)
                .address(address)
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();
        orderRepository.save(order);
        PaymentMethod paymentMethod = parsePaymentMethod(orderRequest.getPaymentMethod());

        // Process order items and update stock
        List<OrderItem> orderItems = orderRequest.getOrderItems().stream().map(itemRequest -> {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + itemRequest.getProductId()));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
            userItemInteractionService.logInteraction(user, product, InteractionType.PURCHASE);

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .build();
        }).toList();

        orderItemRepository.saveAll(orderItems);
        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(total);

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentMethod == PaymentMethod.COD ? PaymentStatus.PENDING : PaymentStatus.COMPLETED)
                .build();
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created: {}", savedOrder.getId());

        return OrderResponse.builder()
                .id(savedOrder.getId())
                .totalPrice(savedOrder.getTotalPrice())
                .orderStatus(savedOrder.getOrderStatus().name())
                .createdAt(LocalDateTime.now())
                .paymentInfo(paymentMapper.toPaymentResponse(savedOrder.getPayment()))
                .deliveryAddress(addressMapper.toAddressResponse(address))
                .orderItems(orderItems.stream().map(orderItemMapper::toOrderItemResponse).collect(Collectors.toList()))
                .build();
    }

    private PaymentMethod parsePaymentMethod(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new java.lang.IllegalArgumentException("paymentMethod is required");
        }
        try {
            return PaymentMethod.valueOf(raw.trim().toUpperCase());
        } catch (java.lang.IllegalArgumentException e) {
            throw new java.lang.IllegalArgumentException("Invalid paymentMethod: " + raw);
        }
    }

    public Page<OrderResponse> getUserOrders(int page, int size) {
        User user = userService.getLoggedInUser();
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));
        return orders.map(orderMapper::toOrderResponse);
    }

    public OrderResponse getOrderById(Long orderId) {
        User user = userService.getLoggedInUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("You do not have permission to view this order");
        }
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (orderStatus == null) {
            throw new IllegalArgumentException("orderStatus is required");
        }
        OrderStatus statusEnum = OrderStatus.valueOf(orderStatus.toUpperCase());
        order.setOrderStatus(statusEnum);

        if (statusEnum == OrderStatus.DELIVERED && order.getPayment().getPaymentMethod() == PaymentMethod.COD) {
            order.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, orderStatus);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    public OrderResponse cancelOrder(Long orderId) {
        User user = userService.getLoggedInUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("You do not have permission to cancel this order");
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new IllegalArgumentException("Cannot cancel an order that has already been shipped or delivered");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        if (order.getPayment().getPaymentStatus() == PaymentStatus.COMPLETED) {
            order.getPayment().setPaymentStatus(PaymentStatus.REFUNDED);
        }

        for (OrderItem item : order.getOrderItemList()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        Order cancelledOrder = orderRepository.save(order);
        log.info("Order {} cancelled", orderId);
        return orderMapper.toOrderResponse(cancelledOrder);
    }
}
