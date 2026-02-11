package com.example.kursinisandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kursinisandroid.R;
import com.example.kursinisandroid.models.FoodOrder;

import java.util.List;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<FoodOrder> orders;

    public OrderAdapter(Context context, List<FoodOrder> orders) {
        this.context = context;
        this.orders = orders;
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
        return orders.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        }

        FoodOrder order = orders.get(position);

        TextView restaurantNameTextView = convertView.findViewById(R.id.orderRestaurantName);
        TextView addressTextView = convertView.findViewById(R.id.orderAddress);
        TextView priceTextView = convertView.findViewById(R.id.orderPrice);
        TextView statusTextView = convertView.findViewById(R.id.orderStatus);
        TextView dateTextView = convertView.findViewById(R.id.orderDate);

        if (order.getRestaurantName() != null && !order.getRestaurantName().isEmpty()) {
            restaurantNameTextView.setText(order.getRestaurantName());
        } else {
            restaurantNameTextView.setText("Restaurant");
        }

        if (order.getDeliveryAddress() != null && !order.getDeliveryAddress().isEmpty()) {
            addressTextView.setText(order.getDeliveryAddress());
        } else {
            addressTextView.setText("No address");
        }

        if (order.getTotalPrice() != null) {
            priceTextView.setText(String.format("Total: %.2f", order.getTotalPrice()));
        } else {
            priceTextView.setText("Total: 0.00");
        }

        if (order.getStatus() != null && !order.getStatus().isEmpty()) {
            statusTextView.setText("Status: " + order.getStatus());
        } else {
            statusTextView.setText("Status: Unknown");
        }

        if (order.getDateCreated() != null && !order.getDateCreated().isEmpty()) {
            dateTextView.setText(order.getDateCreated());
        } else {
            dateTextView.setText("");
        }

        return convertView;
    }
}
