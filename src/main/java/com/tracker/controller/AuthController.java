package com.tracker.controller;

import com.tracker.model.CurrentUserSession;  // ✅ ADD THIS LINE
import com.tracker.model.User;
import com.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // 🟢 Show Login Page
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    // 🟢 Handle Login
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            // ✅ Store logged-in username globally (temporary)
            CurrentUserSession.username = existingUser.getUsername();
            return "redirect:/items";
        } else {
            model.addAttribute("error", "Invalid username or password!");
            return "login";
        }
    }

    // 🟢 Show Register Page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // 🟢 Handle Register
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }
        userRepository.save(user);
        model.addAttribute("success", "Registration successful! Please login.");
        return "login";
    }

    // 🟡 Logout
    @GetMapping("/logout")
    public String logout() {
        CurrentUserSession.username = null;
        return "redirect:/login";
    }
}
