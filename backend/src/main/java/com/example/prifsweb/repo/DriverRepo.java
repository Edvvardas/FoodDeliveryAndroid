package com.example.prifsweb.repo;

import com.example.prifsweb.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepo extends JpaRepository<Driver, Integer> {
}
