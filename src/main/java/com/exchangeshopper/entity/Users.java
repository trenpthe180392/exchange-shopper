package com.exchangeshopper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String role;  // ví dụ: ADMIN, CUSTOMER

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @Column(name = "confirmation_token", length = 100)
    private String confirmationToken;
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

}

