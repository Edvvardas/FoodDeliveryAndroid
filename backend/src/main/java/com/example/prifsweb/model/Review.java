package com.example.prifsweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int rating;
    private String reviewText;
    private LocalDateTime dateCreated;

    @ManyToOne
    @JsonIgnore
    private User reviewer;

    @ManyToOne
    @JsonIgnore
    private Restaurant restaurant;

    public Review(int rating, String reviewText, LocalDateTime dateCreated, User reviewer, Restaurant restaurant) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.dateCreated = dateCreated;
        this.reviewer = reviewer;
        this.restaurant = restaurant;
    }
}
