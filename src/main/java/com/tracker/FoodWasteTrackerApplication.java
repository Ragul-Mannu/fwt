package com.tracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.tracker.model.User;
import com.tracker.repository.UserRepository;

@SpringBootApplication
public class FoodWasteTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodWasteTrackerApplication.class, args);
        System.out.println("🚀 Food Waste Tracker started successfully on http://localhost:8080/items");
    }

    // ✅ Automatically create a default test user (ragul)
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("ragul") == null) {
                User user = new User("ragul", "1234");
                user.setPhoneNumber("+91XXXXXXXXXX"); // replace with your actual phone number
                userRepository.save(user);
                System.out.println("✅ Default test user created: username='ragul', password='1234'");
            } else {
                System.out.println("ℹ️ User 'ragul' already exists in database.");
            }
        };
    }
}
