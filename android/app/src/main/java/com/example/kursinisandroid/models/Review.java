package com.example.kursinisandroid.models;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("id")
    private int id;

    @SerializedName("rating")
    private int rating;

    @SerializedName("reviewText")
    private String reviewText;

    @SerializedName("dateCreated")
    private String dateCreated;

    @SerializedName("reviewerName")
    private String reviewerName;

    @SerializedName("restaurantId")
    private int restaurantId;

    @SerializedName("restaurantName")
    private String restaurantName;

    public Review() {
    }

    public Review(int id, int rating, String reviewText, String dateCreated, String reviewerName, int restaurantId, String restaurantName) {
        this.id = id;
        this.rating = rating;
        this.reviewText = reviewText;
        this.dateCreated = dateCreated;
        this.reviewerName = reviewerName;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
