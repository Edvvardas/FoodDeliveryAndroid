package com.example.prifsweb.repo;

import com.example.prifsweb.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepo extends JpaRepository<Restaurant, Integer> {
}
