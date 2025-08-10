package com.exchangeshopper.dto;

import com.exchangeshopper.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Integer id;
    private String name;
    private String imageUrl;
    private double price;

    public static ProductDto fromEntity(Product p) {
        return new ProductDto(
                p.getId(),
                p.getName(),
                p.getImageUrl(),
                p.getPrice()
        );
    }
}
