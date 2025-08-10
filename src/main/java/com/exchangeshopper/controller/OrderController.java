package com.exchangeshopper.controller;

import com.exchangeshopper.entity.*;
import com.exchangeshopper.repository.OrderRepository;
import com.exchangeshopper.security.CustomUserDetails;
import com.exchangeshopper.service.CartService;
import com.exchangeshopper.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UsersRepository usersRepository;

    /**
     * Trang checkout – nhập thông tin giao hàng
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/login";
        }

        Users user = userDetails.getUser();
        Cart cart = cartService.getOrCreateCart(user);

        model.addAttribute("cart", cart);
        model.addAttribute("user", user);
        return "checkout";
    }

    /**
     * Xử lý đặt hàng thủ công (không dùng VNPAY)
     */
    @PostMapping("/submit")
    public String submitOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) String note,
            RedirectAttributes ra) {

        Users user = userDetails.getUser();

        if (user == null) {
            ra.addFlashAttribute("error", "Bạn cần đăng nhập để đặt hàng.");
            return "redirect:/login";
        }

        if (!phone.matches("0[0-9]{9}")) {
            ra.addFlashAttribute("error", "Số điện thoại không hợp lệ (10 số, bắt đầu bằng 0)");
            return "redirect:/order/checkout";
        }

        Cart cart = cartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống. Không thể đặt hàng.");
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setFullName(fullName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setNote(note);
        order.setUser(user);
        order.setUsername(user.getUsername());
        order.setTotalAmount(cart.getTotalPrice());

        for (CartItem item : cart.getItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setProductId(item.getProduct().getId());
            detail.setProductName(item.getProduct().getName());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getProduct().getPrice());
            detail.setOrder(order);
            order.getItems().add(detail);
        }

        orderRepository.save(order);
        cartService.clearCart(user);

        ra.addFlashAttribute("success", "Đặt hàng thành công!");
        return "redirect:/";
    }

    /**
     * Xem danh sách đơn hàng của người dùng
     */
    @GetMapping("/list")
    public String viewOrders(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Users user = userDetails.getUser();
        List<Order> orders = orderRepository.findByUser(user);
        model.addAttribute("orders", orders);
        return "order_list"; // Thymeleaf
    }

    /**
     * Xem chi tiết 1 đơn hàng – có kiểm tra quyền sở hữu
     */
    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable("id") Integer id,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  RedirectAttributes ra,
                                  Model model) {

        Optional<Order> optOrder = orderRepository.findById(id);
        if (optOrder.isEmpty()) {
            ra.addFlashAttribute("error", "Đơn hàng không tồn tại.");
            return "redirect:/order/list";
        }

        Order order = optOrder.get();

        // Kiểm tra user hiện tại có sở hữu đơn hàng này không
        if (!order.getUser().getId().equals(userDetails.getUser().getId())) {
            ra.addFlashAttribute("error", "Bạn không có quyền xem đơn hàng này.");
            return "redirect:/order/list";
        }

        model.addAttribute("order", order);
        return "order_detail";
    }
}
