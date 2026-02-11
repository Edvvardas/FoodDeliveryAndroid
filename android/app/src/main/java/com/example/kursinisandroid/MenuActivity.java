package com.example.kursinisandroid;

import static com.example.kursinisandroid.Utils.Constants.CREATE_ORDER;
import static com.example.kursinisandroid.Utils.Constants.GET_RESTAURANT_MENU;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kursinisandroid.Utils.RestOperations;
import com.example.kursinisandroid.adapters.MenuAdapter;
import com.example.kursinisandroid.models.Cuisine;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MenuActivity extends AppCompatActivity implements MenuAdapter.OnQuantityChangeListener {

    private int userId;
    private int restaurantId;
    private MenuAdapter menuAdapter;
    private TextView orderTotalTextView;
    private TextView orderItemsCountTextView;
    private TextView restaurantNameTitle;
    private EditText deliveryAddressInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        restaurantId = intent.getIntExtra("restaurantId", 0);
        String restaurantName = intent.getStringExtra("restaurantName");

        orderTotalTextView = findViewById(R.id.orderTotal);
        orderItemsCountTextView = findViewById(R.id.orderItemsCount);
        restaurantNameTitle = findViewById(R.id.restaurantNameTitle);
        deliveryAddressInput = findViewById(R.id.deliveryAddressInput);

        if (restaurantName != null && !restaurantName.isEmpty()) {
            restaurantNameTitle.setText(restaurantName);
        }

        loadMenu();
    }

    private void loadMenu() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_RESTAURANT_MENU + restaurantId);
                System.out.println("Menu response: " + response);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            Gson gson = new Gson();
                            Type menuListType = new TypeToken<List<Cuisine>>() {}.getType();
                            List<Cuisine> menuList = gson.fromJson(response, menuListType);

                            ListView menuListView = findViewById(R.id.menuItems);
                            menuAdapter = new MenuAdapter(this, menuList);
                            menuListView.setAdapter(menuAdapter);

                            updateOrderSummary();
                        } else {
                            Toast.makeText(this, "Menu is empty", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading menu", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateOrderSummary() {
        if (menuAdapter == null) return;

        Map<Integer, Integer> quantities = menuAdapter.getQuantities();
        List<Cuisine> menuItems = menuAdapter.getMenuItems();

        double total = 0.0;
        int itemCount = 0;

        for (Cuisine cuisine : menuItems) {
            int quantity = quantities.getOrDefault(cuisine.getId(), 0);
            if (quantity > 0) {
                total += cuisine.getPrice() * quantity;
                itemCount += quantity;
            }
        }

        orderTotalTextView.setText(String.format("Total: %.2f", total));
        orderItemsCountTextView.setText(String.format("Items: %d", itemCount));
    }

    public void placeOrder(View view) {
        if (menuAdapter == null) {
            Toast.makeText(this, "Menu not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<Integer, Integer> quantities = menuAdapter.getQuantities();
        List<Cuisine> menuItems = menuAdapter.getMenuItems();

        boolean hasItems = false;
        for (int qty : quantities.values()) {
            if (qty > 0) {
                hasItems = true;
                break;
            }
        }

        if (!hasItems) {
            Toast.makeText(this, "Please add items to your order", Toast.LENGTH_SHORT).show();
            return;
        }

        String deliveryAddress = deliveryAddressInput.getText().toString().trim();
        if (deliveryAddress.isEmpty()) {
            Toast.makeText(this, "Please enter delivery address", Toast.LENGTH_SHORT).show();
            deliveryAddressInput.requestFocus();
            return;
        }

        Gson gson = new Gson();
        JsonObject orderJson = new JsonObject();
        orderJson.addProperty("userId", userId);
        orderJson.addProperty("restaurantId", restaurantId);
        orderJson.addProperty("deliveryAddress", deliveryAddress);

        JsonArray itemsArray = new JsonArray();
        for (Cuisine cuisine : menuItems) {
            int quantity = quantities.getOrDefault(cuisine.getId(), 0);
            if (quantity > 0) {
                JsonObject itemJson = new JsonObject();
                itemJson.addProperty("cuisineId", cuisine.getId());
                itemJson.addProperty("quantity", quantity);
                itemsArray.add(itemJson);
            }
        }
        orderJson.add("items", itemsArray);

        String orderData = gson.toJson(orderJson);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(CREATE_ORDER, orderData);
                System.out.println("Order response: " + response);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty() && !response.equals("null")) {
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        menuAdapter.getQuantities().clear();
                        menuAdapter.notifyDataSetChanged();
                        updateOrderSummary();
                        deliveryAddressInput.setText("");
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onQuantityChanged() {
        updateOrderSummary();
    }
}
