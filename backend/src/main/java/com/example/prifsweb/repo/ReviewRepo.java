package com.example.prifsweb.repo;

import com.example.prifsweb.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Integer> {
    List<Review> findByRestaurantId(int restaurantId);
}
