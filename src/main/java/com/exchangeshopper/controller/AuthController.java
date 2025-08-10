package com.exchangeshopper.controller;

import com.exchangeshopper.dto.RegisterRequest;
import com.exchangeshopper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterRequest());
        return "register"; // Hiển thị form đăng ký
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute("form") RegisterRequest form, Model model) {
        try {
            userService.registerUser(form);
            model.addAttribute("success", "Đăng ký thành công! Vui lòng kiểm tra email.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "register";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam String token, Model model) {
        try {
            userService.confirmUser(token);
            model.addAttribute("success", "Xác nhận tài khoản thành công. Bạn có thể đăng nhập.");
        } catch (RuntimeException e) {
            model.addAttribute("error", "Xác nhận thất bại: " + e.getMessage());
        }
        return "login"; // hoặc một trang xác nhận thành công
    }
}
