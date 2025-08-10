package com.exchangeshopper.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Users user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    // === Convenience methods ===
    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    // === Getter & Setter ===
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}
