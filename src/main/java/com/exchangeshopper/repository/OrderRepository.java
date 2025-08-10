package com.exchangeshopper.repository;

import com.exchangeshopper.entity.Order;
import com.exchangeshopper.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(Users user);
}
