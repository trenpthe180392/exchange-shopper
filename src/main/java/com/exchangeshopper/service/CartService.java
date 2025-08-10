package com.exchangeshopper.service;

import com.exchangeshopper.entity.*;

public interface CartService {
    Cart getOrCreateCart(Users user);
    void addItem(Users user, int productId, int quantity);
    void updateItem(Users user, int productId, int quantity);
    void removeItem(Users user, int productId);
    void clearCart(Users user);
    int getItemCount(Users user);
    void addToCart(Users user, Integer productId, int quantity);
    void addToCart(String username, Integer productId, int quantity);

}
