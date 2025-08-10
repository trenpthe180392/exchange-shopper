package com.exchangeshopper.entity;

import jakarta.persistence.*;
import lombok.*;
@Setter
@Getter
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    // === Convenience ===
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    // === Getter & Setter ===
    public Integer getId() { return id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

}
