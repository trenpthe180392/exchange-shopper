package com.exchangeshopper.config;

import com.exchangeshopper.security.CustomUserDetails;
import com.exchangeshopper.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalCartCountAdvice {

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void addCartCountToModel(Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            int cartCount = cartService.getItemCount(userDetails.getUser());
            model.addAttribute("cartCount", cartCount);
        } else {
            model.addAttribute("cartCount", 0);
        }
    }
}
