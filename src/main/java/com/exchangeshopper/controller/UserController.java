package com.exchangeshopper.controller;

import com.exchangeshopper.entity.Users;
import com.exchangeshopper.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UsersRepository usersRepository;

    @GetMapping("/users")
    public String showUserList(Model model) {
        List<Users> users = usersRepository.findAll(); // lấy toàn bộ user
        model.addAttribute("users", users);
        return "user-list";
    }
    @PostMapping("/users/ban/{id}")
    public String banUser(@PathVariable Long id) {
        usersRepository.findById(id).ifPresent(user -> {
            user.setEnabled(false);
            usersRepository.save(user);
        });
        return "redirect:/users";
    }

}
