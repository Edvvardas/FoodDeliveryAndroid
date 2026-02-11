package com.example.prifsweb.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String message;
    private LocalDateTime timestamp;

    @ManyToOne
    private User sender;

    @ManyToOne
    private FoodOrder foodOrder;

    public Chat(String message, LocalDateTime timestamp, User sender, FoodOrder foodOrder) {
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
        this.foodOrder = foodOrder;
    }
}
