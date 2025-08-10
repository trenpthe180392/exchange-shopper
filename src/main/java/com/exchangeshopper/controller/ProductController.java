package com.exchangeshopper.controller;

import com.exchangeshopper.entity.Product;
import com.exchangeshopper.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("/product/{id}")
    public String showDetail(@PathVariable Integer id, Model model) {
        Product product = productService.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/products"; // fallback nếu không tìm thấy
        }
        model.addAttribute("product", product);
        return "product-detail"; // templates/product-detail.html
    }
    @GetMapping("/products")
    public String showAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // trỏ tới file templates/products.html
    }


}
