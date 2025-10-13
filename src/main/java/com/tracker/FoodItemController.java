package com.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/food")
public class FoodItemController {

    @Autowired
    private FoodItemRepository repository;

    @Autowired
    private TwilioNotifier notifier;

    @GetMapping
    public String redirectToView() {
        return "redirect:/food/view";
    }

    @GetMapping("/view")
    public String viewItems(Model model) {
        List<FoodItem> allItems = repository.findAll();
        LocalDate today = LocalDate.now();

        for (FoodItem item : allItems) {
            if (item.getExpiryDate().isBefore(today)) {
                item.setStatus("❌ Expired");
            } else if (item.getExpiryDate().isBefore(today.plusDays(3))) {
                item.setStatus("⚠️ Near Expiry");
            } else {
                item.setStatus("✅ Fresh");
            }

            // Auto emoji
            String name = item.getName().toLowerCase();
            if (name.contains("apple")) item.setEmoji("🍎");
            else if (name.contains("banana")) item.setEmoji("🍌");
            else if (name.contains("milk")) item.setEmoji("🥛");
            else if (name.contains("bread")) item.setEmoji("🍞");
            else if (name.contains("rice")) item.setEmoji("🍚");
            else if (name.contains("egg")) item.setEmoji("🥚");
            else if (name.contains("fish")) item.setEmoji("🐟");
            else if (name.contains("chicken")) item.setEmoji("🍗");
            else if (name.contains("pizza")) item.setEmoji("🍕");
            else if (name.contains("burger")) item.setEmoji("🍔");
            else if (name.contains("cake")) item.setEmoji("🍰");
            else item.setEmoji("🍽️");
        }

        model.addAttribute("items", allItems);
        model.addAttribute("newItem", new FoodItem());

        return "items"; 
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute FoodItem foodItem) {
        repository.save(foodItem);
        return "redirect:/food/view";
    }

    @PostMapping("/notify/{id}")
    public String notifyItem(@PathVariable("id") Long id) {
        FoodItem item = repository.findById(id).orElse(null);
        if (item != null) {
            notifier.sendNotification(item.getName(), item.getExpiryDate().toString());
        }
        return "redirect:/food/view";
    }
}
