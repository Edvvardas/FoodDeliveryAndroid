package com.example.prifsweb.controllers;

import com.example.prifsweb.model.*;
import com.example.prifsweb.repo.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class MenuController {

    @Autowired
    private CuisineRepo cuisineRepo;

    @Autowired
    private RestaurantRepo restaurantRepo;

    @Autowired
    private FoodOrderRepo foodOrderRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @GetMapping(value = "/getMenuRestaurant/{restaurantId}")
    public @ResponseBody List<Cuisine> getRestaurantMenu(@PathVariable int restaurantId) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId).orElse(null);
        if (restaurant != null) {
            return restaurant.getMenuItems();
        }
        return List.of();
    }

    @PostMapping(value = "/createOrder")
    public @ResponseBody FoodOrder createOrder(@RequestBody String orderData) {
        Gson gson = new Gson();
        JsonObject orderJson = gson.fromJson(orderData, JsonObject.class);

        int userId = orderJson.get("userId").getAsInt();
        int restaurantId = orderJson.get("restaurantId").getAsInt();
        String deliveryAddress = orderJson.has("deliveryAddress") ? orderJson.get("deliveryAddress").getAsString() : "";
        JsonArray items = orderJson.getAsJsonArray("items");

        Customer customer = customerRepo.findById(userId).orElse(null);
        Restaurant restaurant = restaurantRepo.findById(restaurantId).orElse(null);

        if (customer == null || restaurant == null) {
            return null;
        }

        FoodOrder foodOrder = new FoodOrder();
        foodOrder.setCustomer(customer);
        foodOrder.setRestaurant(restaurant);
        foodOrder.setStatus("Pending");
        foodOrder.setDateCreated(LocalDateTime.now());
        foodOrder.setDateUpdated(LocalDateTime.now());
        foodOrder.setDeliveryAddress(deliveryAddress.isEmpty() ? (customer.getAddress() != null ? customer.getAddress() : "") : deliveryAddress);

        double totalPrice = 0.0;

        foodOrder = foodOrderRepo.save(foodOrder);

        for (int i = 0; i < items.size(); i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            int cuisineId = item.get("cuisineId").getAsInt();
            int quantity = item.get("quantity").getAsInt();

            Cuisine cuisine = cuisineRepo.findById(cuisineId).orElse(null);
            if (cuisine != null && quantity > 0) {
                double itemPrice = cuisine.getPrice() * quantity;
                totalPrice += itemPrice;

                OrderItem orderItem = new OrderItem();
                orderItem.setFoodOrder(foodOrder);
                orderItem.setCuisine(cuisine);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(itemPrice);

                orderItemRepo.save(orderItem);
            }
        }

        foodOrder.setTotalPrice(totalPrice);
        foodOrder = foodOrderRepo.save(foodOrder);

        return foodOrder;
    }

    @GetMapping(value = "/getCuisineById/{cuisineId}")
    public @ResponseBody Cuisine getCuisineById(@PathVariable int cuisineId) {
        return cuisineRepo.findById(cuisineId).orElse(null);
    }

    @PostMapping(value = "/createCuisine")
    public @ResponseBody Cuisine createCuisine(@RequestBody String cuisineData) {
        Gson gson = new Gson();
        JsonObject cuisineJson = gson.fromJson(cuisineData, JsonObject.class);

        int restaurantId = cuisineJson.get("restaurantId").getAsInt();
        String name = cuisineJson.get("name").getAsString();
        String description = cuisineJson.has("description") ? cuisineJson.get("description").getAsString() : "";
        double price = cuisineJson.get("price").getAsDouble();

        Restaurant restaurant = restaurantRepo.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            return null;
        }

        Cuisine cuisine = new Cuisine(name, description, price, restaurant);
        return cuisineRepo.save(cuisine);
    }

    @PutMapping(value = "/updateCuisine/{cuisineId}")
    public @ResponseBody Cuisine updateCuisine(@PathVariable int cuisineId, @RequestBody String cuisineData) {
        Cuisine cuisine = cuisineRepo.findById(cuisineId).orElse(null);

        if (cuisine == null) {
            return null;
        }

        Gson gson = new Gson();
        JsonObject cuisineJson = gson.fromJson(cuisineData, JsonObject.class);

        if (cuisineJson.has("name")) {
            cuisine.setName(cuisineJson.get("name").getAsString());
        }
        if (cuisineJson.has("description")) {
            cuisine.setDescription(cuisineJson.get("description").getAsString());
        }
        if (cuisineJson.has("price")) {
            cuisine.setPrice(cuisineJson.get("price").getAsDouble());
        }

        return cuisineRepo.save(cuisine);
    }

    @DeleteMapping(value = "/deleteCuisine/{cuisineId}")
    public @ResponseBody String deleteCuisine(@PathVariable int cuisineId) {
        Cuisine cuisine = cuisineRepo.findById(cuisineId).orElse(null);
        if (cuisine == null) {
            return "Cuisine not found";
        }

        cuisineRepo.deleteById(cuisineId);

        Cuisine deletedCuisine = cuisineRepo.findById(cuisineId).orElse(null);
        if (deletedCuisine != null) {
            return "fail on delete";
        } else {
            return "success";
        }
    }

    @GetMapping(value = "/getRestaurantById/{restaurantId}")
    public @ResponseBody Restaurant getRestaurantById(@PathVariable int restaurantId) {
        return restaurantRepo.findById(restaurantId).orElse(null);
    }

    @PostMapping(value = "/createRestaurant")
    public @ResponseBody Restaurant createRestaurant(@RequestBody String restaurantData) {
        Gson gson = new Gson();
        JsonObject restaurantJson = gson.fromJson(restaurantData, JsonObject.class);

        String name = restaurantJson.get("name").getAsString();
        String address = restaurantJson.get("address").getAsString();
        String phoneNumber = restaurantJson.get("phoneNumber").getAsString();

        Restaurant restaurant = new Restaurant(name, address, phoneNumber, null, LocalDateTime.now(), LocalDateTime.now());
        return restaurantRepo.save(restaurant);
    }

    @PutMapping(value = "/updateRestaurant/{restaurantId}")
    public @ResponseBody Restaurant updateRestaurant(@PathVariable int restaurantId, @RequestBody String restaurantData) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId).orElse(null);

        if (restaurant == null) {
            return null;
        }

        Gson gson = new Gson();
        JsonObject restaurantJson = gson.fromJson(restaurantData, JsonObject.class);

        if (restaurantJson.has("name")) {
            restaurant.setName(restaurantJson.get("name").getAsString());
        }
        if (restaurantJson.has("address")) {
            restaurant.setAddress(restaurantJson.get("address").getAsString());
        }
        if (restaurantJson.has("phoneNumber")) {
            restaurant.setPhoneNumber(restaurantJson.get("phoneNumber").getAsString());
        }
        if (restaurantJson.has("rating")) {
            restaurant.setRating(restaurantJson.get("rating").getAsDouble());
        }

        restaurant.setDateUpdated(LocalDateTime.now());
        return restaurantRepo.save(restaurant);
    }

    @DeleteMapping(value = "/deleteRestaurant/{restaurantId}")
    public @ResponseBody String deleteRestaurant(@PathVariable int restaurantId) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId).orElse(null);
        if (restaurant == null) {
            return "Restaurant not found";
        }

        restaurantRepo.deleteById(restaurantId);

        Restaurant deletedRestaurant = restaurantRepo.findById(restaurantId).orElse(null);
        if (deletedRestaurant != null) {
            return "fail on delete";
        } else {
            return "success";
        }
    }
}
