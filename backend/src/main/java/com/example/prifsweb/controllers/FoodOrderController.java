package com.example.prifsweb.controllers;

import com.example.prifsweb.model.Customer;
import com.example.prifsweb.model.FoodOrder;
import com.example.prifsweb.repo.CustomerRepo;
import com.example.prifsweb.repo.FoodOrderRepo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class FoodOrderController {

    @Autowired
    private FoodOrderRepo foodOrderRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @GetMapping(value = "/getOrdersByUser/{userId}")
    public @ResponseBody String getOrdersByUser(@PathVariable int userId) {
        try {
            Customer customer = customerRepo.findById(userId).orElse(null);
            if (customer == null) {
                return "[]";
            }

            List<FoodOrder> orders = foodOrderRepo.findByCustomer(customer);

            Gson gson = new Gson();
            JsonArray ordersArray = new JsonArray();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (FoodOrder order : orders) {
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("id", order.getId());
                orderJson.addProperty("deliveryAddress", order.getDeliveryAddress());
                orderJson.addProperty("totalPrice", order.getTotalPrice());
                orderJson.addProperty("status", order.getStatus());
                orderJson.addProperty("dateCreated", order.getDateCreated() != null ? order.getDateCreated().format(formatter) : "");
                orderJson.addProperty("restaurantName", order.getRestaurant() != null ? order.getRestaurant().getName() : "");
                ordersArray.add(orderJson);
            }

            return gson.toJson(ordersArray);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @GetMapping(value = "/getOrderById/{orderId}")
    public @ResponseBody String getOrderById(@PathVariable int orderId) {
        try {
            FoodOrder order = foodOrderRepo.findById(orderId).orElse(null);
            if (order == null) {
                return "null";
            }

            Gson gson = new Gson();
            JsonObject orderJson = new JsonObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            orderJson.addProperty("id", order.getId());
            orderJson.addProperty("deliveryAddress", order.getDeliveryAddress());
            orderJson.addProperty("totalPrice", order.getTotalPrice());
            orderJson.addProperty("status", order.getStatus());
            orderJson.addProperty("dateCreated", order.getDateCreated() != null ? order.getDateCreated().format(formatter) : "");
            orderJson.addProperty("dateUpdated", order.getDateUpdated() != null ? order.getDateUpdated().format(formatter) : "");
            orderJson.addProperty("restaurantId", order.getRestaurant() != null ? order.getRestaurant().getId() : 0);
            orderJson.addProperty("restaurantName", order.getRestaurant() != null ? order.getRestaurant().getName() : "");
            orderJson.addProperty("customerId", order.getCustomer() != null ? order.getCustomer().getId() : 0);
            orderJson.addProperty("customerName", order.getCustomer() != null ? order.getCustomer().getName() + " " + order.getCustomer().getSurname() : "");
            orderJson.addProperty("driverId", order.getDriver() != null ? order.getDriver().getId() : 0);
            orderJson.addProperty("driverName", order.getDriver() != null ? order.getDriver().getName() + " " + order.getDriver().getSurname() : "");

            return gson.toJson(orderJson);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @GetMapping(value = "/getAllOrders")
    public @ResponseBody String getAllOrders() {
        try {
            List<FoodOrder> orders = foodOrderRepo.findAll();

            Gson gson = new Gson();
            JsonArray ordersArray = new JsonArray();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (FoodOrder order : orders) {
                JsonObject orderJson = new JsonObject();
                orderJson.addProperty("id", order.getId());
                orderJson.addProperty("deliveryAddress", order.getDeliveryAddress());
                orderJson.addProperty("totalPrice", order.getTotalPrice());
                orderJson.addProperty("status", order.getStatus());
                orderJson.addProperty("dateCreated", order.getDateCreated() != null ? order.getDateCreated().format(formatter) : "");
                orderJson.addProperty("restaurantName", order.getRestaurant() != null ? order.getRestaurant().getName() : "");
                orderJson.addProperty("customerName", order.getCustomer() != null ? order.getCustomer().getName() + " " + order.getCustomer().getSurname() : "");
                ordersArray.add(orderJson);
            }

            return gson.toJson(ordersArray);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @PutMapping(value = "/updateOrder/{orderId}")
    public @ResponseBody String updateOrder(@PathVariable int orderId, @RequestBody String orderData) {
        try {
            FoodOrder order = foodOrderRepo.findById(orderId).orElse(null);
            if (order == null) {
                return "Order not found";
            }

            Gson gson = new Gson();
            JsonObject orderJson = gson.fromJson(orderData, JsonObject.class);

            if (orderJson.has("deliveryAddress")) {
                order.setDeliveryAddress(orderJson.get("deliveryAddress").getAsString());
            }
            if (orderJson.has("status")) {
                order.setStatus(orderJson.get("status").getAsString());
            }
            if (orderJson.has("totalPrice")) {
                order.setTotalPrice(orderJson.get("totalPrice").getAsDouble());
            }

            order.setDateUpdated(java.time.LocalDateTime.now());
            foodOrderRepo.save(order);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.addProperty("id", order.getId());
            return gson.toJson(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @DeleteMapping(value = "/deleteOrder/{orderId}")
    public @ResponseBody String deleteOrder(@PathVariable int orderId) {
        try {
            FoodOrder order = foodOrderRepo.findById(orderId).orElse(null);
            if (order == null) {
                return "Order not found";
            }

            foodOrderRepo.deleteById(orderId);

            FoodOrder deletedOrder = foodOrderRepo.findById(orderId).orElse(null);
            if (deletedOrder != null) {
                return "fail on delete";
            } else {
                return "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}
