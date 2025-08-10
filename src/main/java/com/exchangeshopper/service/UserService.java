package com.exchangeshopper.service;

import com.exchangeshopper.dto.RegisterRequest;

public interface UserService {
    void registerUser(RegisterRequest request);
    void confirmUser(String token);
}
