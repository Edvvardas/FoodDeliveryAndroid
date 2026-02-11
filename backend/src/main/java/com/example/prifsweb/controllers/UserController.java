package com.example.prifsweb.controllers;

import com.example.prifsweb.model.Customer;
import com.example.prifsweb.model.Driver;
import com.example.prifsweb.model.Restaurant;
import com.example.prifsweb.model.User;
import com.example.prifsweb.repo.CustomerRepo;
import com.example.prifsweb.repo.DriverRepo;
import com.example.prifsweb.repo.RestaurantRepo;
import com.example.prifsweb.repo.UserRepo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

@RestController
public class UserController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private DriverRepo driverRepo;
    @Autowired
    private RestaurantRepo restaurantRepo;

    @GetMapping(value = "/allUsers")
    public @ResponseBody Iterable<User> getAll() {
        return userRepo.findAll();
    }

    @GetMapping(value = "/getUserById/{userId}")
    public @ResponseBody String getUserById(@PathVariable int userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userType", user.getClass().getSimpleName());
            jsonObject.addProperty("login", user.getLogin());
            jsonObject.addProperty("password", user.getPassword());
            jsonObject.addProperty("name", user.getName());
            jsonObject.addProperty("surname", user.getSurname());
            jsonObject.addProperty("id", user.getId());

            return gson.toJson(jsonObject);
        }
        return null;
    }

    @GetMapping(value = "/allRestaurants")
    public @ResponseBody Iterable<Restaurant> getAllRestaurants() {
        return restaurantRepo.findAll();
    }

    @PostMapping(value = "validateUser")
    public @ResponseBody String getUserByCredentials(@RequestBody String info) {
        System.out.println(info);
        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        var login = properties.getProperty("login");
        var psw = properties.getProperty("password");
        User user = userRepo.getUserByLoginAndPassword(login, psw);
        if (user != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userType", user.getClass().getSimpleName());
            jsonObject.addProperty("login", user.getLogin());
            jsonObject.addProperty("password", user.getPassword());
            jsonObject.addProperty("name", user.getName());
            jsonObject.addProperty("surname", user.getSurname());
            jsonObject.addProperty("id", user.getId());

            String json = gson.toJson(jsonObject);

            return json;
        }
        return null;
    }

    @PutMapping(value = "updateUser")
    public @ResponseBody User updateUser(@RequestBody User user) {
        userRepo.save(user);
        return userRepo.getReferenceById(user.getId());
    }

    @PutMapping(value = "updateUserById/{id}")
    public @ResponseBody User updateUserById(@RequestBody String info, @PathVariable int id) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException());
        Gson gson = new Gson();
        Properties properties = gson.fromJson(info, Properties.class);
        var name = properties.getProperty("name");
        user.setName(name);

        userRepo.save(user);
        return userRepo.getReferenceById(user.getId());
    }

    @PostMapping(value = "insertUser")
    public @ResponseBody User createUser(@RequestBody User user) {
        userRepo.save(user);
        return userRepo.getUserByLoginAndPassword(user.getLogin(), user.getPassword());
    }

    @PostMapping(value = "insertBasicUser")
    public @ResponseBody User createBasicUser(@RequestBody Customer customer) {
        customerRepo.save(customer);
        return userRepo.getUserByLoginAndPassword(customer.getLogin(), customer.getPassword());
    }

    @PostMapping(value = "insertDriver")
    public @ResponseBody User createDriver(@RequestBody Driver driver) {
        driverRepo.save(driver);
        return userRepo.getUserByLoginAndPassword(driver.getLogin(), driver.getPassword());
    }

    @DeleteMapping(value = "deleteUser/{id}")
    public @ResponseBody String deleteUser(@PathVariable int id) {
        userRepo.deleteById(id);
        User user = userRepo.findById(id).orElse(null);
        if (user != null) {
            return "fail on delete";
        } else {
            return "yay";
        }
    }
}
