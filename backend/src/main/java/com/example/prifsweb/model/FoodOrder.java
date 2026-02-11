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
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String deliveryAddress;
    private Double totalPrice;
    private String status;
    private LocalDateTime dateCreated;
    private LocalDateTime dateUpdated;

    @ManyToOne
    @JsonIgnore
    private Customer customer;

    @ManyToOne
    @JsonIgnore
    private Restaurant restaurant;

    @ManyToOne
    @JsonIgnore
    private Driver driver;

    @OneToMany(mappedBy = "foodOrder", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<OrderItem> orderItems = new ArrayList<>();

    public FoodOrder(String deliveryAddress, Double totalPrice, String status, LocalDateTime dateCreated,
                     LocalDateTime dateUpdated, Customer customer, Restaurant restaurant) {
        this.deliveryAddress = deliveryAddress;
        this.totalPrice = totalPrice;
        this.status = status;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.customer = customer;
        this.restaurant = restaurant;
    }
}
