package com.example.kursinisandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.kursinisandroid.R;
import com.example.kursinisandroid.models.FoodOrder;

import java.util.List;

public class DriverOrderAdapter extends BaseAdapter {
    private Context context;
    private List<FoodOrder> orders;
    private OnOrderActionListener listener;
    private boolean showAvailable;

    public interface OnOrderActionListener {
        void onAcceptOrder(FoodOrder order);
        void onCompleteOrder(FoodOrder order);
        void onChatOrder(FoodOrder order);
    }

    public DriverOrderAdapter(Context context, List<FoodOrder> orders, OnOrderActionListener listener, boolean showAvailable) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
        this.showAvailable = showAvailable;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_driver_order, parent, false);
        }

        FoodOrder order = orders.get(position);

        TextView orderIdText = convertView.findViewById(R.id.orderIdText);
        TextView orderStatus = convertView.findViewById(R.id.orderStatus);
        TextView restaurantName = convertView.findViewById(R.id.restaurantName);
        TextView customerInfo = convertView.findViewById(R.id.customerInfo);
        TextView deliveryAddress = convertView.findViewById(R.id.deliveryAddress);
        TextView totalPrice = convertView.findViewById(R.id.totalPrice);
        TextView orderDate = convertView.findViewById(R.id.orderDate);
        Button acceptOrderButton = convertView.findViewById(R.id.acceptOrderButton);
        Button completeOrderButton = convertView.findViewById(R.id.completeOrderButton);
        Button chatButton = convertView.findViewById(R.id.chatButton);

        orderIdText.setText("Order #" + order.getId());
        orderStatus.setText(order.getStatus());

        if (order.getStatus().equals("Ready")) {
            orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else if (order.getStatus().equals("Delivering")) {
            orderStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            orderStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        restaurantName.setText("Restaurant: " + order.getRestaurantName());
        customerInfo.setText("Customer: " + order.getCustomerName() + " (" + order.getCustomerPhone() + ")");
        deliveryAddress.setText("Address: " + order.getDeliveryAddress());
        totalPrice.setText(String.format("Total: €%.2f", order.getTotalPrice()));

        String date = order.getDateCreated();
        if (date != null && date.length() >= 10) {
            orderDate.setText(date.substring(0, 10));
        } else {
            orderDate.setText(date);
        }

        if (showAvailable && order.getStatus().equals("Ready")) {
            acceptOrderButton.setVisibility(View.VISIBLE);
            completeOrderButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.GONE);

            acceptOrderButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptOrder(order);
                }
            });
        } else if (!showAvailable && order.getStatus().equals("Delivering")) {
            acceptOrderButton.setVisibility(View.GONE);
            completeOrderButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.VISIBLE);

            completeOrderButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCompleteOrder(order);
                }
            });

            chatButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatOrder(order);
                }
            });
        } else if (!showAvailable && order.getStatus().equals("Delivered")) {
            acceptOrderButton.setVisibility(View.GONE);
            completeOrderButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.VISIBLE);

            chatButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatOrder(order);
                }
            });
        } else {
            acceptOrderButton.setVisibility(View.GONE);
            completeOrderButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.GONE);
        }

        return convertView;
    }
}
