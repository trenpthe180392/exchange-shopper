package com.exchangeshopper.service.impl;

import com.exchangeshopper.entity.Product;
import com.exchangeshopper.repository.ProductRepository;
import com.exchangeshopper.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    @Override
    public Product findById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));
    }
    @Override
    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id); // repository cần trả về Optional<Product>
    }
    @Override
    public void save(Product product) {
        productRepository.save(product);
    }


}