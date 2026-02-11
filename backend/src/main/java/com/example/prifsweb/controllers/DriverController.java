package com.example.prifsweb.controllers;

import com.example.prifsweb.model.Driver;
import com.example.prifsweb.model.FoodOrder;
import com.example.prifsweb.repo.DriverRepo;
import com.example.prifsweb.repo.FoodOrderRepo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class DriverController {

    @Autowired
    private FoodOrderRepo foodOrderRepo;

    @Autowired
    private DriverRepo driverRepo;

    @GetMapping(value = "/getReadyOrders")
    public @ResponseBody List<Map<String, Object>> getReadyOrders() {
        List<FoodOrder> orders = foodOrderRepo.findByStatus("Ready");
        List<Map<String, Object>> orderList = new ArrayList<>();

        for (FoodOrder order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("deliveryAddress", order.getDeliveryAddress());
            orderMap.put("totalPrice", order.getTotalPrice());
            orderMap.put("status", order.getStatus());
            orderMap.put("dateCreated", order.getDateCreated().toString());
            orderMap.put("restaurantName", order.getRestaurant().getName());
            orderMap.put("customerName", order.getCustomer().getName() + " " + order.getCustomer().getSurname());
            orderMap.put("customerPhone", order.getCustomer().getPhoneNumber());
            orderList.add(orderMap);
        }

        return orderList;
    }

    @GetMapping(value = "/getDriverOrders/{driverId}")
    public @ResponseBody List<Map<String, Object>> getDriverOrders(@PathVariable int driverId) {
        List<FoodOrder> orders = foodOrderRepo.findByDriverId(driverId);

        orders.sort(Comparator
                .comparing((FoodOrder o) -> o.getStatus().equals("Delivering") ? 0 : 1)
                .thenComparing((FoodOrder o) -> o.getDateUpdated() != null ? o.getDateUpdated() : o.getDateCreated(),
                        Comparator.reverseOrder()));

        List<Map<String, Object>> orderList = new ArrayList<>();

        for (FoodOrder order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", order.getId());
            orderMap.put("deliveryAddress", order.getDeliveryAddress());
            orderMap.put("totalPrice", order.getTotalPrice());
            orderMap.put("status", order.getStatus());
            orderMap.put("dateCreated", order.getDateCreated().toString());
            orderMap.put("restaurantName", order.getRestaurant().getName());
            orderMap.put("customerName", order.getCustomer().getName() + " " + order.getCustomer().getSurname());
            orderMap.put("customerPhone", order.getCustomer().getPhoneNumber());
            orderList.add(orderMap);
        }

        return orderList;
    }

    @PostMapping(value = "/acceptOrder")
    public @ResponseBody FoodOrder acceptOrder(@RequestBody String orderData) {
        Gson gson = new Gson();
        JsonObject orderJson = gson.fromJson(orderData, JsonObject.class);

        int orderId = orderJson.get("orderId").getAsInt();
        int driverId = orderJson.get("driverId").getAsInt();

        FoodOrder order = foodOrderRepo.findById(orderId).orElse(null);
        Driver driver = driverRepo.findById(driverId).orElse(null);

        if (order == null || driver == null) {
            return null;
        }

        if (!order.getStatus().equals("Ready")) {
            return null;
        }

        order.setDriver(driver);
        order.setStatus("Delivering");
        order.setDateUpdated(LocalDateTime.now());

        return foodOrderRepo.save(order);
    }

    @PostMapping(value = "/completeOrder")
    public @ResponseBody FoodOrder completeOrder(@RequestBody String orderData) {
        Gson gson = new Gson();
        JsonObject orderJson = gson.fromJson(orderData, JsonObject.class);

        int orderId = orderJson.get("orderId").getAsInt();

        FoodOrder order = foodOrderRepo.findById(orderId).orElse(null);

        if (order == null) {
            return null;
        }

        if (!order.getStatus().equals("Delivering")) {
            return null;
        }

        order.setStatus("Delivered");
        order.setDateUpdated(LocalDateTime.now());

        return foodOrderRepo.save(order);
    }

    @GetMapping(value = "/getDriverById/{driverId}")
    public @ResponseBody Map<String, Object> getDriverById(@PathVariable int driverId) {
        Driver driver = driverRepo.findById(driverId).orElse(null);

        if (driver == null) {
            return null;
        }

        Map<String, Object> driverMap = new HashMap<>();
        driverMap.put("id", driver.getId());
        driverMap.put("name", driver.getName());
        driverMap.put("surname", driver.getSurname());
        driverMap.put("login", driver.getLogin());
        driverMap.put("phoneNumber", driver.getPhoneNumber() != null ? driver.getPhoneNumber() : "");
        driverMap.put("vehicleType", driver.getVehicleType() != null ? driver.getVehicleType().name() : "");

        return driverMap;
    }

    @GetMapping(value = "/getAllDrivers")
    public @ResponseBody List<Map<String, Object>> getAllDrivers() {
        List<Driver> drivers = driverRepo.findAll();
        List<Map<String, Object>> driverList = new ArrayList<>();

        for (Driver driver : drivers) {
            Map<String, Object> driverMap = new HashMap<>();
            driverMap.put("id", driver.getId());
            driverMap.put("name", driver.getName());
            driverMap.put("surname", driver.getSurname());
            driverMap.put("login", driver.getLogin());
            driverMap.put("phoneNumber", driver.getPhoneNumber() != null ? driver.getPhoneNumber() : "");
            driverMap.put("vehicleType", driver.getVehicleType() != null ? driver.getVehicleType().name() : "");
            driverList.add(driverMap);
        }

        return driverList;
    }

    @PutMapping(value = "/updateDriver/{driverId}")
    public @ResponseBody Driver updateDriver(@PathVariable int driverId, @RequestBody String driverData) {
        Driver driver = driverRepo.findById(driverId).orElse(null);

        if (driver == null) {
            return null;
        }

        Gson gson = new Gson();
        JsonObject driverJson = gson.fromJson(driverData, JsonObject.class);

        if (driverJson.has("name")) {
            driver.setName(driverJson.get("name").getAsString());
        }
        if (driverJson.has("surname")) {
            driver.setSurname(driverJson.get("surname").getAsString());
        }
        if (driverJson.has("phoneNumber")) {
            driver.setPhoneNumber(driverJson.get("phoneNumber").getAsString());
        }
        if (driverJson.has("login")) {
            driver.setLogin(driverJson.get("login").getAsString());
        }
        if (driverJson.has("password")) {
            driver.setPassword(driverJson.get("password").getAsString());
        }

        return driverRepo.save(driver);
    }
}
