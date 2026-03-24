package com.project.ecommerce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * VNPay Sandbox Integration Service
 * Spec: https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.md
 */
@Slf4j
@Service
public class VNPayService {

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.pay-url}")
    private String payUrl;

    @Value("${vnpay.return-url}")
    private String returnUrl;

    /**
     * Build the VNPay payment redirect URL for a given order.
     *
     * @param orderId   internal order ID (used as part of txnRef)
     * @param amount    order amount in VND
     * @param orderInfo short description shown on VNPay page
     * @param clientIp  requestor IP address
     * @return full redirect URL to send the browser to
     */
    public String createPaymentUrl(Long orderId, BigDecimal amount, String orderInfo, String clientIp) {
        String txnRef = orderId + "_" + System.currentTimeMillis();
        String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Amount in VNPay = original VND * 100 (integer "đồng" scaled by 100)
        long vnpAmount = amount.multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, String> params = new TreeMap<>(); // TreeMap sorts keys A-Z automatically
        params.put("vnp_Version",    "2.1.0");
        params.put("vnp_Command",    "pay");
        params.put("vnp_TmnCode",    tmnCode);
        params.put("vnp_Amount",     String.valueOf(vnpAmount));
        params.put("vnp_CurrCode",   "VND");
        params.put("vnp_TxnRef",     txnRef);
        params.put("vnp_OrderInfo",  orderInfo);
        params.put("vnp_OrderType",  "other");
        params.put("vnp_Locale",     "vn");
        params.put("vnp_ReturnUrl",  returnUrl);
        params.put("vnp_IpAddr",     clientIp != null ? clientIp : "127.0.0.1");
        params.put("vnp_CreateDate", createDate);

        /*
         * Per VNPay official Java sample:
         *   hashData = "key=URLEncoded(value)&key=URLEncoded(value)..."  ← KEY is NOT encoded
         *   query    = "URLEncoded(key)=URLEncoded(value)&..."            ← both encoded
         * The HMAC-SHA512 is computed on hashData (URL-encoded VALUES, raw KEYS).
         */
        StringBuilder hashData = new StringBuilder();
        StringBuilder query    = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String fieldName  = entry.getKey();
            String fieldValue = entry.getValue();
            if (fieldValue != null && !fieldValue.isEmpty()) {
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII);
                // hashData: raw key, URL-encoded value
                if (!first) hashData.append('&');
                hashData.append(fieldName).append('=').append(encodedValue);
                // query: both URL-encoded
                if (!first) query.append('&');
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                     .append('=').append(encodedValue);
                first = false;
            }
        }

        String secureHash = hmacSHA512(hashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return payUrl + "?" + query;
    }

    /**
     * Verify the secure hash returned by VNPay on the return URL.
     * Removes vnp_SecureHash (and vnp_SecureHashType) before recomputing.
     *
     * @param params all query params from VNPay return redirect
     * @return true if signature is valid
     */
    public boolean verifyReturnSignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null) return false;

        // Build sorted param map without the hash fields
        Map<String, String> sorted = new TreeMap<>(params);
        sorted.remove("vnp_SecureHash");
        sorted.remove("vnp_SecureHashType");

        // hashData must use the same format: raw key + '=' + URLEncoded(value)
        StringBuilder hashData = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            String fieldValue = entry.getValue();
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (!first) hashData.append('&');
                hashData.append(entry.getKey())
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                first = false;
            }
        }

        String expectedHash = hmacSHA512(hashSecret, hashData.toString());
        return expectedHash.equalsIgnoreCase(receivedHash);
    }

    /** Returns true if the VNPay response code indicates a successful payment. */
    public boolean isPaymentSuccessful(String responseCode) {
        return "00".equals(responseCode);
    }

    // ─── Private helper ──────────────────────────────────────────────────────

    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC-SHA512", e);
        }
    }
}
