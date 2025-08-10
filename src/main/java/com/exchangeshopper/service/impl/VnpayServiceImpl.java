package com.exchangeshopper.service.impl;

import com.exchangeshopper.entity.Users;
import com.exchangeshopper.service.VnpayService;
import com.exchangeshopper.util.HMACUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VnpayServiceImpl implements VnpayService {

    private final String vnp_TmnCode = "F9YJVOF0";
    private final String vnp_HashSecret = "X5CA379JBCDVUYW1JRJIZ96QI3PLTW87";
    private final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String vnp_ReturnUrl = "https://44eacf0642a8.ngrok-free.app/exchange2025/payment/vnpay-return";

    @Override
    public String createPaymentUrl(Users user, String amount, HttpServletRequest req) {
        long vnpAmount;
        try {
            double raw = Double.parseDouble(amount);
            vnpAmount = (long) (raw * 100); // VNPAY expects amount x100
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amount);
        }

        String orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 12); // Short and clean

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnp_TmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", orderId);

        // ❗ Gỡ dấu tiếng Việt để tránh lỗi encoding
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho user " + user.getUsername());

        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl);
        String ipAddress = req.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = "127.0.0.1"; // fallback IP
        }
        vnpParams.put("vnp_IpAddr", ipAddress);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        vnpParams.put("vnp_CreateDate", LocalDateTime.now().format(formatter));

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String field : fieldNames) {
            String value = vnpParams.get(field);
            if (value != null && !value.isEmpty()) {
                hashData.append(field).append('=').append(value).append('&');
                query.append(URLEncoder.encode(field, StandardCharsets.US_ASCII))
                        .append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII))
                        .append('&');
            }
        }

        if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);
        if (query.length() > 0) query.setLength(query.length() - 1);

        String secureHash = HMACUtil.hmacSHA512(vnp_HashSecret, hashData.toString());
        String fullUrl = vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + secureHash;

        // 🔍 Debug full thông tin
        System.out.println("========== VNPAY DEBUG ==========");
        System.out.println("🔢 OrderID: " + orderId);
        System.out.println("📦 Amount: " + vnpAmount);
        System.out.println("🔐 Hash Data: " + hashData);
        System.out.println("🌐 Query: " + query);
        System.out.println("✅ Full URL: " + fullUrl);
        System.out.println("==================================");

        return fullUrl;
    }

    @Override
    public boolean handleVnpayReturn(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        return "00".equals(responseCode);
    }
}
