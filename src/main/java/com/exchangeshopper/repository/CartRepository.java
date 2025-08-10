package com.exchangeshopper.repository;

import com.exchangeshopper.entity.Cart;
import com.exchangeshopper.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(Users user); // sửa lại
}

