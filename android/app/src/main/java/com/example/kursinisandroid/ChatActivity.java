package com.example.kursinisandroid;

import static com.example.kursinisandroid.Utils.Constants.GET_MESSAGES_BY_ORDER;
import static com.example.kursinisandroid.Utils.Constants.SEND_MESSAGE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kursinisandroid.Utils.RestOperations;
import com.example.kursinisandroid.adapters.ChatAdapter;
import com.example.kursinisandroid.models.Chat;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private int orderId;
    private int userId;
    private String orderStatus;
    private EditText messageInput;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private Handler handler;
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        orderId = intent.getIntExtra("orderId", 0);
        userId = intent.getIntExtra("userId", 0);
        orderStatus = intent.getStringExtra("orderStatus");

        messageInput = findViewById(R.id.messageInput);
        chatListView = findViewById(R.id.chatList);

        handler = new Handler(Looper.getMainLooper());

        if (orderStatus != null && orderStatus.equals("Delivered")) {
            messageInput.setEnabled(false);
            messageInput.setHint("Chat is closed for delivered orders");
            findViewById(R.id.sendButton).setEnabled(false);
            findViewById(R.id.sendButton).setAlpha(0.5f);
        }

        loadMessages();
        startAutoRefresh();
    }

    private void loadMessages() {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String response = RestOperations.sendGet(GET_MESSAGES_BY_ORDER + orderId);
                System.out.println("Messages response: " + response);
                handler.post(() -> {
                    try {
                        if (!response.equals("Error") && !response.isEmpty()) {
                            Gson gson = new Gson();
                            Type chatListType = new TypeToken<List<Chat>>() {}.getType();
                            List<Chat> chatList = gson.fromJson(response, chatListType);

                            chatAdapter = new ChatAdapter(this, chatList, userId);
                            chatListView.setAdapter(chatAdapter);

                            chatListView.setSelection(chatAdapter.getCount() - 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void startAutoRefresh() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessages();
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(refreshRunnable, 3000);
    }

    public void sendMessage(View view) {
        if (orderStatus != null && orderStatus.equals("Delivered")) {
            Toast.makeText(this, "Cannot send messages for delivered orders", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("orderId", orderId);
        messageJson.addProperty("senderId", userId);
        messageJson.addProperty("message", message);

        String messageData = gson.toJson(messageJson);

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(SEND_MESSAGE, messageData);
                System.out.println("Send message response: " + response);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty() && !response.equals("null")) {
                        messageInput.setText("");
                        loadMessages();
                    } else {
                        Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }
}
