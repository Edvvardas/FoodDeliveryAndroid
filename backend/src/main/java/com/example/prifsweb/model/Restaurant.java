package com.example.prifsweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String address;
    private String phoneNumber;
    private Double rating;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @ManyToOne
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Cuisine> menuItems = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<FoodOrder> orders = new ArrayList<>();

    public Restaurant(String name, String address, String phoneNumber, User owner, LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.owner = owner;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.rating = 0.0;
    }
}
