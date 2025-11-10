package com.tracker.repository;

import com.tracker.FoodItem;
import com.tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByUser(User user);
}
