package com.tracker.controller;

import com.tracker.model.CurrentUserSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 🏠 Root URL redirect
    @GetMapping("/")
    public String home() {
        // If no user logged in → go to login
        if (CurrentUserSession.username == null) {
            return "redirect:/login";
        }
        // If user already logged in → go to items dashboard
        return "redirect:/items";
    }
}
