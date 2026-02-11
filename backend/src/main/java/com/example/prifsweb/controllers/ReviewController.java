package com.example.prifsweb.controllers;

import com.example.prifsweb.model.Customer;
import com.example.prifsweb.model.Restaurant;
import com.example.prifsweb.model.Review;
import com.example.prifsweb.repo.CustomerRepo;
import com.example.prifsweb.repo.RestaurantRepo;
import com.example.prifsweb.repo.ReviewRepo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @GetMapping(value = "/getReviewsByRestaurant/{restaurantId}")
    public @ResponseBody List<Map<String, Object>> getReviewsByRestaurant(@PathVariable int restaurantId) {
        List<Review> reviews = reviewRepo.findByRestaurantId(restaurantId);
        List<Map<String, Object>> reviewList = new ArrayList<>();

        for (Review review : reviews) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("id", review.getId());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("reviewText", review.getReviewText());
            reviewMap.put("dateCreated", review.getDateCreated().toString());
            reviewMap.put("reviewerName", getReviewerName(review));

            reviewMap.put("restaurantId", review.getRestaurant().getId());
            reviewMap.put("restaurantName", review.getRestaurant().getName());
            reviewList.add(reviewMap);
        }

        return reviewList;
    }

    @GetMapping(value = "/getAllReviews")
    public @ResponseBody List<Map<String, Object>> getAllReviews() {
        List<Review> reviews = reviewRepo.findAll();
        List<Map<String, Object>> reviewList = new ArrayList<>();

        for (Review review : reviews) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("id", review.getId());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("reviewText", review.getReviewText());
            reviewMap.put("dateCreated", review.getDateCreated().toString());
            reviewMap.put("reviewerName", getReviewerName(review));

            reviewMap.put("restaurantId", review.getRestaurant().getId());
            reviewMap.put("restaurantName", review.getRestaurant().getName());
            reviewList.add(reviewMap);
        }

        return reviewList;
    }

    @PostMapping(value = "/createReview")
    public @ResponseBody Review createReview(@RequestBody String reviewData) {
        Gson gson = new Gson();
        JsonObject reviewJson = gson.fromJson(reviewData, JsonObject.class);

        int userId = reviewJson.get("userId").getAsInt();
        int restaurantId = reviewJson.get("restaurantId").getAsInt();
        int rating = reviewJson.get("rating").getAsInt();
        String reviewText = reviewJson.has("reviewText") ? reviewJson.get("reviewText").getAsString() : "";

        Customer customer = customerRepo.findById(userId).orElse(null);
        Restaurant restaurant = restaurantRepo.findById(restaurantId).orElse(null);

        if (customer == null || restaurant == null || rating < 1 || rating > 5) {
            return null;
        }

        Review review = new Review();
        review.setRating(rating);
        review.setReviewText(reviewText);
        review.setDateCreated(LocalDateTime.now());
        review.setReviewer(customer);
        review.setRestaurant(restaurant);

        return reviewRepo.save(review);
    }

    @GetMapping(value = "/getReviewById/{reviewId}")
    public @ResponseBody Map<String, Object> getReviewById(@PathVariable int reviewId) {
        Review review = reviewRepo.findById(reviewId).orElse(null);

        if (review == null) {
            return null;
        }

        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("id", review.getId());
        reviewMap.put("rating", review.getRating());
        reviewMap.put("reviewText", review.getReviewText());
        reviewMap.put("dateCreated", review.getDateCreated().toString());
        reviewMap.put("reviewerName", getReviewerName(review));
        reviewMap.put("restaurantId", review.getRestaurant().getId());
        reviewMap.put("restaurantName", review.getRestaurant().getName());

        return reviewMap;
    }

    @PutMapping(value = "/updateReview/{reviewId}")
    public @ResponseBody Review updateReview(@PathVariable int reviewId, @RequestBody String reviewData) {
        Review review = reviewRepo.findById(reviewId).orElse(null);

        if (review == null) {
            return null;
        }

        Gson gson = new Gson();
        JsonObject reviewJson = gson.fromJson(reviewData, JsonObject.class);

        if (reviewJson.has("rating")) {
            int rating = reviewJson.get("rating").getAsInt();
            if (rating >= 1 && rating <= 5) {
                review.setRating(rating);
            }
        }
        if (reviewJson.has("reviewText")) {
            review.setReviewText(reviewJson.get("reviewText").getAsString());
        }

        return reviewRepo.save(review);
    }

    @DeleteMapping(value = "/deleteReview/{reviewId}")
    public @ResponseBody String deleteReview(@PathVariable int reviewId) {
        Review review = reviewRepo.findById(reviewId).orElse(null);
        if (review == null) {
            return "Review not found";
        }

        reviewRepo.deleteById(reviewId);

        Review deletedReview = reviewRepo.findById(reviewId).orElse(null);
        if (deletedReview != null) {
            return "fail on delete";
        } else {
            return "success";
        }
    }

    private String getReviewerName(Review review) {
        String reviewerName = "Anonymous";
        if (review.getReviewer() != null) {
            String name = review.getReviewer().getName() != null ? review.getReviewer().getName() : "";
            String surname = review.getReviewer().getSurname() != null ? review.getReviewer().getSurname() : "";
            reviewerName = (name + " " + surname).trim();
            if (reviewerName.isEmpty()) {
                reviewerName = "Anonymous";
            }
        }
        return reviewerName;
    }
}
