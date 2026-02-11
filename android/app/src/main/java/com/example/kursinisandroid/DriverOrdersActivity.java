package com.example.kursinisandroid;

import static com.example.kursinisandroid.Utils.Constants.ACCEPT_ORDER;
import static com.example.kursinisandroid.Utils.Constants.COMPLETE_ORDER;
import static com.example.kursinisandroid.Utils.Constants.GET_DRIVER_ORDERS;
import static com.example.kursinisandroid.Utils.Constants.GET_READY_ORDERS;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kursinisandroid.Utils.RestOperations;
import com.example.kursinisandroid.adapters.DriverOrderAdapter;
import com.example.kursinisandroid.models.FoodOrder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriverOrdersActivity extends AppCompatActivity implements DriverOrderAdapter.OnOrderActionListener {

    private int driverId;
    private DriverOrderAdapter orderAdapter;
    private boolean showingAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        driverId = getIntent().getIntExtra("userId", 0);

        loadAvailableOrders();
    }

    public void showAvailableOrders(View view) {
        showingAvailable = true;
        loadAvailableOrders();
    }

    public void showMyDeliveries(View view) {
        showingAvailable = false;
        loadMyDeliveries();
    }

    private void loadAvailableOrders() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_READY_ORDERS);
                System.out.println("Ready orders response: " + response);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            Gson gson = new Gson();
                            Type orderListType = new TypeToken<List<FoodOrder>>() {}.getType();
                            List<FoodOrder> orderList = gson.fromJson(response, orderListType);

                            ListView ordersListView = findViewById(R.id.driverOrdersList);
                            orderAdapter = new DriverOrderAdapter(this, orderList, this, true);
                            ordersListView.setAdapter(orderAdapter);

                            if (orderList.isEmpty()) {
                                Toast.makeText(this, "No available orders", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No orders available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadMyDeliveries() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_DRIVER_ORDERS + driverId);
                System.out.println("My deliveries response: " + response);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            Gson gson = new Gson();
                            Type orderListType = new TypeToken<List<FoodOrder>>() {}.getType();
                            List<FoodOrder> orderList = gson.fromJson(response, orderListType);

                            ListView ordersListView = findViewById(R.id.driverOrdersList);
                            orderAdapter = new DriverOrderAdapter(this, orderList, this, false);
                            ordersListView.setAdapter(orderAdapter);

                            if (orderList.isEmpty()) {
                                Toast.makeText(this, "No deliveries assigned", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No deliveries available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading deliveries", Toast.LENGTH_SHORT).show();
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
    public void onAcceptOrder(FoodOrder order) {
        Gson gson = new Gson();
        JsonObject orderJson = new JsonObject();
        orderJson.addProperty("orderId", order.getId());
        orderJson.addProperty("driverId", driverId);

        String orderData = gson.toJson(orderJson);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(ACCEPT_ORDER, orderData);
                System.out.println("Accept order response: " + response);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty() && !response.equals("null")) {
                        Toast.makeText(this, "Order accepted!", Toast.LENGTH_SHORT).show();
                        loadAvailableOrders();
                    } else {
                        Toast.makeText(this, "Failed to accept order", Toast.LENGTH_SHORT).show();
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
    public void onCompleteOrder(FoodOrder order) {
        Gson gson = new Gson();
        JsonObject orderJson = new JsonObject();
        orderJson.addProperty("orderId", order.getId());

        String orderData = gson.toJson(orderJson);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(COMPLETE_ORDER, orderData);
                System.out.println("Complete order response: " + response);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty() && !response.equals("null")) {
                        Toast.makeText(this, "Order marked as delivered!", Toast.LENGTH_SHORT).show();
                        loadMyDeliveries();
                    } else {
                        Toast.makeText(this, "Failed to complete order", Toast.LENGTH_SHORT).show();
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
    public void onChatOrder(FoodOrder order) {
        Intent intent = new Intent(DriverOrdersActivity.this, ChatActivity.class);
        intent.putExtra("orderId", order.getId());
        intent.putExtra("userId", driverId);
        intent.putExtra("orderStatus", order.getStatus());
        startActivity(intent);
    }
}
