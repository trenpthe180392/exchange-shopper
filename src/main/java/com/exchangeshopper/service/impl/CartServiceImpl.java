package com.exchangeshopper.service.impl;

import com.exchangeshopper.entity.*;
import com.exchangeshopper.repository.*;
import com.exchangeshopper.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired private CartRepository cartRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private UsersRepository userRepo;
    @Autowired private UsersRepository usersRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Override
    public Cart getOrCreateCart(Users user) {
        return cartRepo.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepo.save(cart);
        });
    }


    @Override
    public void addItem(Users user, int productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId() == productId)
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            Product product = productRepo.findById(productId).orElse(null);
            if (product == null) return;

            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setCart(cart);
            cart.getItems().add(item);
        }
        cartRepo.save(cart);
    }

    @Override
    public void updateItem(Users user, int productId, int quantity) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().forEach(item -> {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(quantity);
            }
        });
        cartRepo.save(cart);
    }

    @Override
    public void removeItem(Users user, int productId) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().removeIf(item -> item.getProduct().getId() == productId);
        cartRepo.save(cart);
    }

    @Override
    public void clearCart(Users user) {
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepo.save(cart);
    }
    @Override
    public int getItemCount(Users user) {
        Cart cart = getOrCreateCart(user);
        return cart.getItems().size();
    }
    @Override
    public void addToCart(String username, Integer productId, int quantity) {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));

        Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepo.save(newCart);
        });

        // Kiểm tra xem user đã có sản phẩm trong giỏ chưa
        Optional<CartItem> optional = cartItemRepository.findByUserAndProduct(user, product);

        CartItem cartItem;
        if (optional.isPresent()) {
            cartItem = optional.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
        }

        cartItemRepository.save(cartItem);
    }
    @Override
    public void addToCart(Users user, Integer productId, int quantity) {
        // 🔍 Lấy sản phẩm cần thêm vào giỏ
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // 🛒 Lấy giỏ hàng hiện tại của user, nếu chưa có thì tạo mới
        Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepo.save(newCart);
        });
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepo.save(cart); // cần gán lại vì có ID sinh ra
        }

        // 🔄 Kiểm tra xem sản phẩm đã có trong giỏ chưa
        Optional<CartItem> optional = cartItemRepository.findByUserAndProduct(user, product);

        CartItem cartItem;
        if (optional.isPresent()) {
            // Nếu đã có thì cập nhật số lượng
            cartItem = optional.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Nếu chưa có thì tạo mới CartItem
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setCart(cart); // ✅ Gán giỏ hàng để cart_id KHÔNG NULL
            cartItem.setQuantity(quantity);
        }

        // 💾 Lưu lại cart item
        cartItemRepository.save(cartItem);
    }


}
