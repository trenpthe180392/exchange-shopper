package com.exchangeshopper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.text.NumberFormat;
import java.util.Locale;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;

    private double price;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String imageUrl;

    @Column(nullable = false)
    @org.hibernate.annotations.ColumnDefault("0")
    private Integer quantity;

    public String getFormattedPrice() {
        // Format tiền tệ với đơn vị VNĐ (ví dụ)
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(price);
    }
}
