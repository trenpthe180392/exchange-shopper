package com.exchangeshopper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders") // "order" là từ khóa SQL
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime orderDate;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String fullName;    // 👈 Tên người nhận hàng
    @Column(columnDefinition = "NVARCHAR(255)")
    private String address;     // Địa chỉ giao hàng
    private String phone;       // Số điện thoại
    @Column(columnDefinition = "NVARCHAR(255)")
    private String note;        // Ghi chú từ người dùng
    private double totalAmount;
    @Column(columnDefinition = "NVARCHAR(255)")
    private String username;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
