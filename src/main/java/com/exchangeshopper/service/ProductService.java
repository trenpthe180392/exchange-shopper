package com.exchangeshopper.service;

import com.exchangeshopper.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Product findById(int id);
    Optional<Product> findById(Integer id);
    void save(Product product);

}