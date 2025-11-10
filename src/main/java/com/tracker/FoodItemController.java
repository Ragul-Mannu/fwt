package com.tracker.controller;

import com.tracker.FoodItem;
import com.tracker.model.User;
import com.tracker.repository.FoodItemRepository;
import com.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/items")
public class FoodItemController {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    // Temporary: assume a fixed user (we’ll replace this with logged-in user later)
    private String currentUsername = "ragul";

    // 🟢 Show all food items for the current user
    @GetMapping
    public String viewItems(Model model) {
        User user = userRepository.findByUsername(currentUsername);
        List<FoodItem> items = foodItemRepository.findByUser(user);
        model.addAttribute("items", items);
        model.addAttribute("newItem", new FoodItem());
        return "items"; // This maps to templates/items.html
    }

    // 🟢 Add new food item
    @PostMapping("/add")
    public String addItem(@ModelAttribute FoodItem newItem) {
        User user = userRepository.findByUsername(currentUsername);
        newItem.setUser(user);

        // Automatically set status based on expiry
        LocalDate today = LocalDate.now();
        if (newItem.getExpiryDate().isBefore(today)) {
            newItem.setStatus("Expired");
            newItem.setEmoji("⚠️");
        } else if (newItem.getExpiryDate().isBefore(today.plusDays(2))) {
            newItem.setStatus("Near Expiry");
            newItem.setEmoji("⏰");
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

    // 🟢 Edit food item
    @GetMapping("/edit/{id}")
    public String editItem(@PathVariable Long id, Model model) {
        FoodItem item = foodItemRepository.findById(id).orElse(null);
        model.addAttribute("item", item);
        return "edit_item"; // Create this page later if needed
    }

    @PostMapping("/update")
    public String updateItem(@ModelAttribute FoodItem updatedItem) {
        User user = userRepository.findByUsername(currentUsername);
        updatedItem.setUser(user);
        foodItemRepository.save(updatedItem);
        return "redirect:/items";
    }
}
