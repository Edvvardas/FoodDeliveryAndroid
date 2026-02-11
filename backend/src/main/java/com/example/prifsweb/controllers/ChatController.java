package com.example.prifsweb.controllers;

import com.example.prifsweb.model.Chat;
import com.example.prifsweb.model.FoodOrder;
import com.example.prifsweb.model.User;
import com.example.prifsweb.repo.ChatRepo;
import com.example.prifsweb.repo.FoodOrderRepo;
import com.example.prifsweb.repo.UserRepo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private FoodOrderRepo foodOrderRepo;

    @Autowired
    private UserRepo userRepo;

    @GetMapping(value = "/getMessagesByOrder/{orderId}")
    public @ResponseBody String getMessagesByOrder(@PathVariable int orderId) {
        try {
            FoodOrder order = foodOrderRepo.findById(orderId).orElse(null);
            if (order == null) {
                return "[]";
            }

            List<Chat> messages = chatRepo.findByFoodOrderOrderByTimestampAsc(order);

            Gson gson = new Gson();
            JsonArray messagesArray = new JsonArray();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Chat chat : messages) {
                JsonObject messageJson = new JsonObject();
                messageJson.addProperty("id", chat.getId());
                messageJson.addProperty("orderId", chat.getFoodOrder().getId());
                messageJson.addProperty("senderId", chat.getSender().getId());
                messageJson.addProperty("senderName", chat.getSender().getName() + " " + chat.getSender().getSurname());
                messageJson.addProperty("message", chat.getMessage());
                messageJson.addProperty("timestamp", chat.getTimestamp() != null ? chat.getTimestamp().format(formatter) : "");
                messagesArray.add(messageJson);
            }

            return gson.toJson(messagesArray);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @PostMapping(value = "/sendMessage")
    public @ResponseBody String sendMessage(@RequestBody String messageData) {
        try {
            Gson gson = new Gson();
            JsonObject messageJson = gson.fromJson(messageData, JsonObject.class);

            int orderId = messageJson.get("orderId").getAsInt();
            int senderId = messageJson.get("senderId").getAsInt();
            String message = messageJson.get("message").getAsString();

            FoodOrder order = foodOrderRepo.findById(orderId).orElse(null);
            User sender = userRepo.findById(senderId).orElse(null);

            if (order == null || sender == null) {
                return "Error";
            }

            Chat chat = new Chat(message, LocalDateTime.now(), sender, order);
            chatRepo.save(chat);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            return gson.toJson(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @GetMapping(value = "/getChatById/{chatId}")
    public @ResponseBody String getChatById(@PathVariable int chatId) {
        try {
            Chat chat = chatRepo.findById(chatId).orElse(null);
            if (chat == null) {
                return "null";
            }

            Gson gson = new Gson();
            JsonObject messageJson = new JsonObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            messageJson.addProperty("id", chat.getId());
            messageJson.addProperty("orderId", chat.getFoodOrder().getId());
            messageJson.addProperty("senderId", chat.getSender().getId());
            messageJson.addProperty("senderName", chat.getSender().getName() + " " + chat.getSender().getSurname());
            messageJson.addProperty("message", chat.getMessage());
            messageJson.addProperty("timestamp", chat.getTimestamp() != null ? chat.getTimestamp().format(formatter) : "");

            return gson.toJson(messageJson);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @GetMapping(value = "/getAllChats")
    public @ResponseBody String getAllChats() {
        try {
            List<Chat> messages = chatRepo.findAll();

            Gson gson = new Gson();
            JsonArray messagesArray = new JsonArray();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Chat chat : messages) {
                JsonObject messageJson = new JsonObject();
                messageJson.addProperty("id", chat.getId());
                messageJson.addProperty("orderId", chat.getFoodOrder().getId());
                messageJson.addProperty("senderId", chat.getSender().getId());
                messageJson.addProperty("senderName", chat.getSender().getName() + " " + chat.getSender().getSurname());
                messageJson.addProperty("message", chat.getMessage());
                messageJson.addProperty("timestamp", chat.getTimestamp() != null ? chat.getTimestamp().format(formatter) : "");
                messagesArray.add(messageJson);
            }

            return gson.toJson(messagesArray);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @PutMapping(value = "/updateMessage/{chatId}")
    public @ResponseBody String updateMessage(@PathVariable int chatId, @RequestBody String messageData) {
        try {
            Chat chat = chatRepo.findById(chatId).orElse(null);
            if (chat == null) {
                return "Message not found";
            }

            Gson gson = new Gson();
            JsonObject messageJson = gson.fromJson(messageData, JsonObject.class);

            if (messageJson.has("message")) {
                chat.setMessage(messageJson.get("message").getAsString());
            }

            chat.setTimestamp(LocalDateTime.now());
            chatRepo.save(chat);

            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.addProperty("id", chat.getId());
            return gson.toJson(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @DeleteMapping(value = "/deleteMessage/{chatId}")
    public @ResponseBody String deleteMessage(@PathVariable int chatId) {
        try {
            Chat chat = chatRepo.findById(chatId).orElse(null);
            if (chat == null) {
                return "Message not found";
            }

            chatRepo.deleteById(chatId);

            Chat deletedChat = chatRepo.findById(chatId).orElse(null);
            if (deletedChat != null) {
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
