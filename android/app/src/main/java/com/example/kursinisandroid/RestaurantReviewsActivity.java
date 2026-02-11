package com.example.kursinisandroid;

import static com.example.kursinisandroid.Utils.Constants.CREATE_REVIEW;
import static com.example.kursinisandroid.Utils.Constants.GET_REVIEWS_BY_RESTAURANT;

import android.app.AlertDialog;
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
import com.example.kursinisandroid.adapters.ReviewAdapter;
import com.example.kursinisandroid.models.Review;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RestaurantReviewsActivity extends AppCompatActivity {

    private int userId;
    private int restaurantId;
    private String restaurantName;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_reviews);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getIntExtra("userId", 0);
        restaurantId = getIntent().getIntExtra("restaurantId", 0);
        restaurantName = getIntent().getStringExtra("restaurantName");

        TextView titleView = findViewById(R.id.restaurantNameTitle);
        titleView.setText(restaurantName + " - Reviews");

        loadReviews();
    }

    private void loadReviews() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_REVIEWS_BY_RESTAURANT + restaurantId);
                System.out.println("Reviews response: " + response);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            Gson gson = new Gson();
                            Type reviewListType = new TypeToken<List<Review>>() {}.getType();
                            List<Review> reviewList = gson.fromJson(response, reviewListType);

                            ListView reviewsListView = findViewById(R.id.reviewsList);
                            reviewAdapter = new ReviewAdapter(this, reviewList);
                            reviewsListView.setAdapter(reviewAdapter);

                            if (reviewList.isEmpty()) {
                                Toast.makeText(this, "No reviews yet for this restaurant", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No reviews available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading reviews", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void addReview(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
        builder.setView(dialogView);

        dialogView.findViewById(R.id.restaurantSpinner).setVisibility(View.GONE);
        dialogView.findViewById(R.id.restaurantLabel).setVisibility(View.GONE);

        EditText ratingInput = dialogView.findViewById(R.id.ratingInput);
        EditText reviewTextInput = dialogView.findViewById(R.id.reviewTextInput);

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.submitButton).setOnClickListener(v -> {
            String ratingStr = ratingInput.getText().toString().trim();
            String reviewText = reviewTextInput.getText().toString().trim();

            if (ratingStr.isEmpty()) {
                Toast.makeText(this, "Please enter a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            int rating;
            try {
                rating = Integer.parseInt(ratingStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating < 1 || rating > 5) {
                Toast.makeText(this, "Rating must be between 1 and 5", Toast.LENGTH_SHORT).show();
                return;
            }

            submitReview(rating, reviewText);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void submitReview(int rating, String reviewText) {
        Gson gson = new Gson();
        JsonObject reviewJson = new JsonObject();
        reviewJson.addProperty("userId", userId);
        reviewJson.addProperty("restaurantId", restaurantId);
        reviewJson.addProperty("rating", rating);
        reviewJson.addProperty("reviewText", reviewText);

        String reviewData = gson.toJson(reviewJson);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(CREATE_REVIEW, reviewData);
                System.out.println("Review response: " + response);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty() && !response.equals("null")) {
                        Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                        loadReviews();
                    } else {
                        Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
