package com.example.kursinisandroid.models;

import com.google.gson.annotations.SerializedName;

public class FoodOrder {
    @SerializedName("id")
    private int id;

    @SerializedName("deliveryAddress")
    private String deliveryAddress;

    @SerializedName("totalPrice")
    private Double totalPrice;

    @SerializedName("status")
    private String status;

    @SerializedName("dateCreated")
    private String dateCreated;

    @SerializedName("restaurantName")
    private String restaurantName;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("customerPhone")
    private String customerPhone;

    public FoodOrder() {
    }

    public FoodOrder(int id, String deliveryAddress, Double totalPrice, String status, String dateCreated, String restaurantName) {
        this.id = id;
        this.deliveryAddress = deliveryAddress;
        this.totalPrice = totalPrice;
        this.status = status;
        this.dateCreated = dateCreated;
        this.restaurantName = restaurantName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
}
