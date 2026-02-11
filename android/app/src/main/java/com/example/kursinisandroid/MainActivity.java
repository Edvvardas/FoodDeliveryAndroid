package com.example.kursinisandroid;

import static com.example.kursinisandroid.Utils.Constants.VALIDATE_USER_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kursinisandroid.Utils.RestOperations;
import com.example.kursinisandroid.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText loginField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginField = findViewById(R.id.loginField);
        passwordField = findViewById(R.id.passwordField);
    }

    public void validateUser(View view) {
        String login = loginField.getText().toString();
        String password = passwordField.getText().toString();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", login);
        jsonObject.addProperty("password", password);
        String info = gson.toJson(jsonObject);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(VALIDATE_USER_URL, info);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty() && !response.equals("null")) {
                        User user = gson.fromJson(response, User.class);

                        Intent intent;
                        if (user.getUserType() != null && user.getUserType().equals("Driver")) {
                            intent = new Intent(MainActivity.this, DriverOrdersActivity.class);
                            intent.putExtra("userId", user.getId());
                        } else {
                            intent = new Intent(MainActivity.this, WoltMain.class);
                            intent.putExtra("userJsonObject", response);
                        }

                        startActivity(intent);
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void loadRegWindow(View view) {
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }
}