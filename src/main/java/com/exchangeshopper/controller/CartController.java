package com.exchangeshopper.controller;

import com.exchangeshopper.entity.Cart;
import com.exchangeshopper.entity.Product;
import com.exchangeshopper.entity.Users;
import com.exchangeshopper.security.CustomUserDetails;
import com.exchangeshopper.service.CartService;
import com.exchangeshopper.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    // ✅ Hiển thị giỏ hàng
    @GetMapping("/cart")
    public String showCart(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        Users user = userDetails.getUser();

        Cart cart = cartService.getOrCreateCart(user);
        model.addAttribute("cart", cart.getItems());

        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
        model.addAttribute("total", total);

        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam("productId") Integer productId,
                            @RequestParam("quantity") int quantity,
                            RedirectAttributes redirectAttributes) {

        if (userDetails == null) return "redirect:/login";

        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));

        if (quantity > product.getQuantity()) {
            redirectAttributes.addFlashAttribute("error", "Số lượng mua vượt quá tồn kho!");
            return "redirect:/product/" + productId;
        }

        Users user = userDetails.getUser();
        cartService.addToCart(user, productId, quantity);

        redirectAttributes.addFlashAttribute("success", "Đã thêm vào giỏ hàng!");
        return "redirect:/product/" + productId;
    }

    // ✅ Cập nhật số lượng trong giỏ
    @PostMapping("/cart/update")
    public String updateCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam("productId") int productId,
                             @RequestParam("quantity") int quantity) {
        if (userDetails == null) return "redirect:/login";
        Users user = userDetails.getUser();

        cartService.updateItem(user, productId, quantity);
        return "redirect:/cart";
    }

    // ✅ Xóa 1 sản phẩm khỏi giỏ
    @GetMapping("/cart/remove")
    public String removeItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam("productId") int productId) {
        if (userDetails == null) return "redirect:/login";
        Users user = userDetails.getUser();

        cartService.removeItem(user, productId);
        return "redirect:/cart";
    }

    // ✅ Xóa toàn bộ giỏ hàng
    @PostMapping("/cart/clear")
    public String clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";
        Users user = userDetails.getUser();

        cartService.clearCart(user);
        return "redirect:/cart";
    }
}
