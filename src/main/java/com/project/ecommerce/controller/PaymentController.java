package com.project.ecommerce.controller;

import com.project.ecommerce.dto.response.ApiResponse;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.Payment;
import com.project.ecommerce.enums.PaymentStatus;
import com.project.ecommerce.exception.NotFoundException;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.PaymentRepository;
import com.project.ecommerce.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final VNPayService vnPayService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Create a VNPay payment URL for an already-placed order.
     * Frontend calls this right after creating the order with paymentMethod=VN_PAY.
     */
    @PostMapping("/vnpay/create-url")
    public ResponseEntity<ApiResponse<String>> createVNPayUrl(
            @RequestParam Long orderId,
            HttpServletRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        String clientIp = getClientIp(request);
        String orderInfo = "Thanh toan don hang #" + orderId;

        String paymentUrl = vnPayService.createPaymentUrl(
                orderId,
                order.getTotalPrice(),
                orderInfo,
                clientIp
        );

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(200)
                .message("VNPay URL created successfully")
                .data(paymentUrl)
                .build());
    }

    /**
     * VNPay redirects the browser here after payment completes (success or fail).
     * Verifies the signature then updates the payment status.
     *
     * This is a GET because VNPay uses a browser redirect (GET) with query params.
     */
    @GetMapping("/vnpay/return")
    public ResponseEntity<ApiResponse<Map<String, Object>>> vnPayReturn(HttpServletRequest request) {
        // Collect all vnp_* query params into a map
        Map<String, String> params = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name, request.getParameter(name));
        }

        log.info("VNPay return params: {}", params);

        // Verify signature
        if (!vnPayService.verifyReturnSignature(params)) {
            log.warn("VNPay return: invalid signature");
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Chữ ký không hợp lệ");
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                    .status(400).message("Invalid signature").data(result).build());
        }

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef       = params.get("vnp_TxnRef"); // orderId_timestamp
        Long orderId = Long.parseLong(txnRef.split("_")[0]);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("responseCode", responseCode);

        if (vnPayService.isPaymentSuccessful(responseCode)) {
            // Update payment status
            Payment payment = order.getPayment();
            if (payment != null && payment.getPaymentStatus() == PaymentStatus.PENDING) {
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(payment);
            }
            result.put("success", true);
            result.put("message", "Thanh toán thành công");
            log.info("VNPay payment completed for order {}", orderId);
        } else {
            // Payment failed / cancelled — mark as FAILED
            Payment payment = order.getPayment();
            if (payment != null) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
            result.put("success", false);
            result.put("message", "Thanh toán thất bại hoặc bị huỷ (mã: " + responseCode + ")");
            log.warn("VNPay payment failed for order {}, code {}", orderId, responseCode);
        }

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .status(200)
                .message("VNPay return processed")
                .data(result)
                .build());
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Take first IP if comma-separated
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
