package com.example.kursinisandroid;

import static com.example.kursinisandroid.Utils.Constants.GET_ORDERS_BY_USER;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kursinisandroid.Utils.RestOperations;
import com.example.kursinisandroid.adapters.OrderAdapter;
import com.example.kursinisandroid.models.FoodOrder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MyOrdersActivity extends AppCompatActivity {

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);

        loadOrders();
    }

    private void loadOrders() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_ORDERS_BY_USER + userId);
                System.out.println("Orders response: " + response);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            Gson gson = new Gson();
                            Type orderListType = new TypeToken<List<FoodOrder>>() {}.getType();
                            List<FoodOrder> orderList = gson.fromJson(response, orderListType);

                            ListView orderListView = findViewById(R.id.ordersList);
                            OrderAdapter adapter = new OrderAdapter(this, orderList);
                            orderListView.setAdapter(adapter);

                            orderListView.setOnItemClickListener((parent, view, position, id) -> {
                                FoodOrder selectedOrder = orderList.get(position);
                                Intent chatIntent = new Intent(MyOrdersActivity.this, ChatActivity.class);
                                chatIntent.putExtra("orderId", selectedOrder.getId());
                                chatIntent.putExtra("userId", userId);
                                chatIntent.putExtra("orderStatus", selectedOrder.getStatus());
                                startActivity(chatIntent);
                            });
                        } else {
                            Toast.makeText(this, "No orders found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading orders", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
