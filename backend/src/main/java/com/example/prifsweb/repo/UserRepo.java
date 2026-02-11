package com.example.prifsweb.repo;

import com.example.prifsweb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
    User getUserByLoginAndPassword(String login, String password);

}
