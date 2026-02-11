package com.example.kursinisandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.kursinisandroid.R;
import com.example.kursinisandroid.models.Restaurant;

import java.util.List;

public class RestaurantAdapter extends ArrayAdapter<Restaurant> {

    public RestaurantAdapter(@NonNull Context context, @NonNull List<Restaurant> restaurants) {
        super(context, 0, restaurants);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_restaurant, parent, false);
        }

        Restaurant restaurant = getItem(position);

        if (restaurant != null) {
            TextView nameTextView = convertView.findViewById(R.id.restaurantName);
            TextView addressTextView = convertView.findViewById(R.id.restaurantAddress);
            TextView phoneTextView = convertView.findViewById(R.id.restaurantPhone);

            nameTextView.setText(restaurant.getName());

            if (restaurant.getAddress() != null && !restaurant.getAddress().isEmpty()) {
                addressTextView.setText("Address: " + restaurant.getAddress());
            } else {
                addressTextView.setText("Address: Not available");
            }

            if (restaurant.getPhoneNumber() != null && !restaurant.getPhoneNumber().isEmpty()) {
                phoneTextView.setText("Phone: " + restaurant.getPhoneNumber());
            } else {
                phoneTextView.setText("Phone: Not available");
            }
        }

        return convertView;
    }
}
