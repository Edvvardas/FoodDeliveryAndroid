package com.example.kursinisandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kursinisandroid.R;
import com.example.kursinisandroid.models.Chat;

import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<Chat> messages;
    private int currentUserId;

    public ChatAdapter(Context context, List<Chat> messages, int currentUserId) {
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        }

        Chat chat = messages.get(position);

        TextView senderTextView = convertView.findViewById(R.id.messageSender);
        TextView messageTextView = convertView.findViewById(R.id.messageText);
        TextView timeTextView = convertView.findViewById(R.id.messageTime);

        if (chat.getSenderName() != null && !chat.getSenderName().isEmpty()) {
            if (chat.getSenderId() == currentUserId) {
                senderTextView.setText("You");
            } else {
                senderTextView.setText(chat.getSenderName());
            }
        } else {
            senderTextView.setText("Unknown");
        }

        if (chat.getMessage() != null && !chat.getMessage().isEmpty()) {
            messageTextView.setText(chat.getMessage());
        } else {
            messageTextView.setText("");
        }

        if (chat.getTimestamp() != null && !chat.getTimestamp().isEmpty()) {
            timeTextView.setText(chat.getTimestamp());
        } else {
            timeTextView.setText("");
        }

        return convertView;
    }
}
