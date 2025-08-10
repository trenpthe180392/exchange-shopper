package com.exchangeshopper.controller;

import com.exchangeshopper.entity.*;
import com.exchangeshopper.repository.OrderRepository;
import com.exchangeshopper.security.CustomUserDetails;
import com.exchangeshopper.service.CartService;
import com.exchangeshopper.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private VnpayService vnpayService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Người dùng bấm nút Thanh toán => Redirect sang VNPAY
     */
    @PostMapping("/vnpay")
    public String redirectToVnpay(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) String note,
            HttpServletRequest request,
            RedirectAttributes ra
    ) {
        Users user = userDetails.getUser();
        Cart cart = cartService.getOrCreateCart(user);

        if (cart.getItems().isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng trống. Không thể thanh toán.");
            return "redirect:/cart";
        }

        // Tạm lưu thông tin đặt hàng vào session để dùng lại sau khi thanh toán
        request.getSession().setAttribute("fullName", fullName.trim());
        request.getSession().setAttribute("phone", phone.trim());
        request.getSession().setAttribute("address", address.trim());
        request.getSession().setAttribute("note", note != null ? note.trim() : "");

        // Tạo URL thanh toán VNPAY
        double total = cart.getTotalPrice();
        String amount = String.valueOf((long) total); // đảm bảo không phải 2790000.0
        String paymentUrl = vnpayService.createPaymentUrl(user, amount, request);

        return "redirect:" + paymentUrl.trim();
    }

    /**
     * Sau khi thanh toán xong, VNPAY redirect về đây
     */
    @GetMapping("/vnpay-return")
    public String handleVnpayReturn(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request,
            RedirectAttributes ra
    ) {
        Users user = userDetails.getUser();

        boolean isSuccess = vnpayService.handleVnpayReturn(request);
        if (!isSuccess) {
            ra.addFlashAttribute("error", "Thanh toán thất bại hoặc bị huỷ.");
            return "redirect:/cart";
        }

        // Lấy thông tin từ session
        String fullName = (String) request.getSession().getAttribute("fullName");
        String phone = (String) request.getSession().getAttribute("phone");
        String address = (String) request.getSession().getAttribute("address");
        String note = (String) request.getSession().getAttribute("note");

        // Tạo đơn hàng
        Cart cart = cartService.getOrCreateCart(user);
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

        // Xoá dữ liệu tạm khỏi session
        request.getSession().removeAttribute("fullName");
        request.getSession().removeAttribute("phone");
        request.getSession().removeAttribute("address");
        request.getSession().removeAttribute("note");

        ra.addFlashAttribute("success", "Thanh toán và đặt hàng thành công!");
        return "redirect:/";
    }
}
