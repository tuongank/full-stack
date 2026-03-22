package com.project.ecommerce.controller;

import com.project.ecommerce.dto.request.OrderRequest;
import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.dto.response.OrderResponse;
import com.project.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status(201)
                .message("Order created successfully")
                .data(orderResponse)
                .build();
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(@RequestParam int page, @RequestParam int size) {
        Page<OrderResponse> orderResponse = orderService.getUserOrders(page, size);
        ApiResponse<List<OrderResponse>> response = ApiResponse.<List<OrderResponse>>builder()
                .status(200)
                .message("Order updated successfully")
                .data(orderResponse.getContent())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status(200)
                .message("Order retrieved successfully")
                .data(orderResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        OrderResponse orderResponse = orderService.updateOrderStatus(orderId, status);
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status(200)
                .message("Order updated successfully")
                .data(orderResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long orderId) {
        OrderResponse orderResponse = orderService.cancelOrder(orderId);
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .status(200)
                .message("Order cancelled successfully")
                .data(orderResponse)
                .build();
        return ResponseEntity.ok(response);
    }
}
