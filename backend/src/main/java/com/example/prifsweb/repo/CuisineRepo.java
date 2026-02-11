package com.example.prifsweb.repo;

import com.example.prifsweb.model.Cuisine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuisineRepo extends JpaRepository<Cuisine, Integer> {
}
