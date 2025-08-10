package com.exchangeshopper.repository;

import com.exchangeshopper.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}