package com.example.prifsweb.repo;

import com.example.prifsweb.model.Chat;
import com.example.prifsweb.model.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepo extends JpaRepository<Chat, Integer> {
    List<Chat> findByFoodOrderOrderByTimestampAsc(FoodOrder foodOrder);
}
