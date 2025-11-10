package com.tracker.controller;

import com.tracker.FoodItem;
import com.tracker.TwilioNotifier;
import com.tracker.model.CurrentUserSession;
import com.tracker.model.User;
import com.tracker.repository.FoodItemRepository;
import com.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/items")
public class FoodItemController {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TwilioNotifier twilioNotifier;

    // 🟢 Show all food items for the logged-in user
    @GetMapping
    public String viewItems(Model model) {
        String currentUsername = CurrentUserSession.username;
        if (currentUsername == null) return "redirect:/login";

        User user = userRepository.findByUsername(currentUsername);
        List<FoodItem> items = foodItemRepository.findByUser(user);

        model.addAttribute("items", items);
        model.addAttribute("newItem", new FoodItem());
        model.addAttribute("username", currentUsername);
        return "items";
    }

    // 🟢 Add new food item
    @PostMapping("/add")
    public String addItem(@ModelAttribute FoodItem newItem) {
        String currentUsername = CurrentUserSession.username;
        if (currentUsername == null) return "redirect:/login";

        User user = userRepository.findByUsername(currentUsername);
        newItem.setUser(user);

        LocalDate today = LocalDate.now();
        if (newItem.getExpiryDate().isBefore(today)) {
            newItem.setStatus("Expired");
            newItem.setEmoji("⚠️");
        } else if (newItem.getExpiryDate().isBefore(today.plusDays(2))) {
            newItem.setStatus("Near Expiry");
            newItem.setEmoji("⏰");
            twilioNotifier.sendNotification(newItem.getName(), newItem.getExpiryDate().toString(), user);
        } else {
            newItem.setStatus("Fresh");
            newItem.setEmoji("✅");
        }

        foodItemRepository.save(newItem);
        return "redirect:/items";
    }

    // 🟡 Delete item by ID
    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        foodItemRepository.deleteById(id);
        return "redirect:/items";
    }

    // 🟠 Manual Notify (button click)
    @PostMapping("/notify/{id}")
    public String notifyUser(@PathVariable Long id) {
        String currentUsername = CurrentUserSession.username;
        if (currentUsername == null) return "redirect:/login";

        User user = userRepository.findByUsername(currentUsername);
        FoodItem item = foodItemRepository.findById(id).orElse(null);
        if (item != null) {
            twilioNotifier.sendNotification(item.getName(), item.getExpiryDate().toString(), user);
        }
        return "redirect:/items";
    }

    // 🧾 NEW FEATURE: Download all user items as CSV
    @GetMapping("/download")
    public void downloadCSV(HttpServletResponse response) throws IOException {
        String currentUsername = CurrentUserSession.username;
        if (currentUsername == null) {
            response.sendRedirect("/login");
            return;
        }

        User user = userRepository.findByUsername(currentUsername);
        List<FoodItem> items = foodItemRepository.findByUser(user);

        // Set CSV response headers
        response.setContentType("text/csv");
        String fileName = "food_items_" + currentUsername + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Write CSV content
        PrintWriter writer = response.getWriter();
        writer.println("Food Name,Expiry Date,Status,Emoji");
        for (FoodItem item : items) {
            writer.println(item.getName() + "," +
                    item.getExpiryDate() + "," +
                    item.getStatus() + "," +
                    item.getEmoji());
        }
        writer.flush();
        writer.close();
    }
}
