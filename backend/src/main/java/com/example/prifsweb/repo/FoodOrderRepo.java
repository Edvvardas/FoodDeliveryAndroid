package com.example.prifsweb.repo;

import com.example.prifsweb.model.Customer;
import com.example.prifsweb.model.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodOrderRepo extends JpaRepository<FoodOrder, Integer> {
    List<FoodOrder> findByCustomer(Customer customer);
    List<FoodOrder> findByStatus(String status);
    List<FoodOrder> findByDriverId(int driverId);
}
