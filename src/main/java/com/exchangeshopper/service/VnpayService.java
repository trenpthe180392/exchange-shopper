package com.exchangeshopper.service;

import com.exchangeshopper.entity.Users;

import jakarta.servlet.http.HttpServletRequest;

public interface VnpayService {

    String createPaymentUrl(Users user, String amount, HttpServletRequest request);

    boolean handleVnpayReturn(HttpServletRequest request);
}
