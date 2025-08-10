package com.exchangeshopper.repository;

import com.exchangeshopper.entity.Cart;
import com.exchangeshopper.entity.CartItem;
import com.exchangeshopper.entity.Product;
import com.exchangeshopper.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByUserAndProduct(Users user, Product product); // nếu dùng theo user
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);  // nếu dùng theo cart ✅
}


