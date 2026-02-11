package com.example.prifsweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int quantity;
    private Double price;

    @ManyToOne
    @JsonIgnore
    private FoodOrder foodOrder;

    @ManyToOne
    @JsonIgnore
    private Cuisine cuisine;

    public OrderItem(int quantity, Double price, FoodOrder foodOrder, Cuisine cuisine) {
        this.quantity = quantity;
        this.price = price;
        this.foodOrder = foodOrder;
        this.cuisine = cuisine;
    }
}
