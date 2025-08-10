package com.exchangeshopper.service.impl;

import com.exchangeshopper.dto.RegisterRequest;
import com.exchangeshopper.entity.Users;
import com.exchangeshopper.repository.UsersRepository;
import com.exchangeshopper.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void registerUser(RegisterRequest request) {
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng.");
        }

        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên người dùng đã tồn tại.");
        }

        String token = UUID.randomUUID().toString();

        Users user = Users.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role("CUSTOMER")
                .enabled(true)
                .emailVerified(false)
                .confirmationToken(token)
                .build();

        usersRepository.save(user);
        sendConfirmationEmail(user.getEmail(), token);
    }


    @Override
    @Transactional
    public void confirmUser(String token) {
        Optional<Users> optionalUser = usersRepository.findByConfirmationToken(token);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn.");
        }

        Users user = optionalUser.get();
        user.setEmailVerified(true);              // ✅ Đánh dấu đã xác minh
        user.setConfirmationToken(null);          // 🧹 Xóa token sau khi dùng
        usersRepository.save(user);
    }

    private void sendConfirmationEmail(String toEmail, String token) {
        String confirmUrl = "http://localhost:8080/exchange2025/auth/confirm?token=" + token;
        String subject = "Xác nhận tài khoản - ExchangeShopper";
        String text = """
                Xin chào,

                Vui lòng nhấn vào liên kết dưới đây để xác nhận địa chỉ email của bạn:

                %s

                Cảm ơn bạn đã sử dụng ExchangeShopper!
                """.formatted(confirmUrl);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(toEmail);
        mail.setSubject(subject);
        mail.setText(text);

        mailSender.send(mail);
    }
}
