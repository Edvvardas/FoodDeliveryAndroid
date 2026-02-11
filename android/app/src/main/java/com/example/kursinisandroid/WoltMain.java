package com.example.kursinisandroid;

import static com.example.kursinisandroid.Utils.Constants.GET_ALL_RESTAURANTS_URL;

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
import com.example.kursinisandroid.adapters.RestaurantAdapter;
import com.example.kursinisandroid.models.Restaurant;
import com.example.kursinisandroid.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WoltMain extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wolt_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String userInfo = intent.getStringExtra("userJsonObject");

        if (userInfo != null && !userInfo.isEmpty()) {
            Gson gson = new Gson();
            currentUser = gson.fromJson(userInfo, User.class);
        }

        loadRestaurants();
    }

    private void loadRestaurants() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_ALL_RESTAURANTS_URL);
                System.out.println("Restaurants response: " + response);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            Gson gson = new Gson();
                            Type restaurantListType = new TypeToken<List<Restaurant>>() {}.getType();
                            List<Restaurant> restaurantList = gson.fromJson(response, restaurantListType);

                            ListView restaurantListView = findViewById(R.id.restaurantList);
                            RestaurantAdapter adapter = new RestaurantAdapter(this, restaurantList);
                            restaurantListView.setAdapter(adapter);

                            restaurantListView.setOnItemClickListener((parent, view, position, id) -> {
                                Restaurant selectedRestaurant = restaurantList.get(position);
                                Intent menuIntent = new Intent(WoltMain.this, MenuActivity.class);
                                menuIntent.putExtra("restaurantId", selectedRestaurant.getId());
                                menuIntent.putExtra("restaurantName", selectedRestaurant.getName());
                                if (currentUser != null) {
                                    menuIntent.putExtra("userId", currentUser.getId());
                                }
                                startActivity(menuIntent);
                            });
                        } else {
                            Toast.makeText(this, "No restaurants available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading restaurants", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void viewMyOrders(View view) {
        if (currentUser != null) {
            Intent intent = new Intent(WoltMain.this, MyOrdersActivity.class);
            intent.putExtra("userId", currentUser.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void viewReviews(View view) {
        if (currentUser != null) {
            Intent intent = new Intent(WoltMain.this, ReviewsActivity.class);
            intent.putExtra("userId", currentUser.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
        }
    }
}