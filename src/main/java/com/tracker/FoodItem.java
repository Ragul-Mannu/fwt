package com.tracker;

import com.tracker.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "food_items")
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate expiryDate;
    private String status;
    private String emoji;

    // 🔗 Relationship with User (many items can belong to one user)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 🏗️ Constructors
    public FoodItem() {}

    public FoodItem(String name, LocalDate expiryDate, String status, String emoji, User user) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.status = status;
        this.emoji = emoji;
        this.user = user;
    }

    // ⚙️ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
