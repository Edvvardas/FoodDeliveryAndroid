package com.example.kursinisandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kursinisandroid.R;
import com.example.kursinisandroid.models.Review;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {
    private Context context;
    private List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        }

        Review review = reviews.get(position);

        TextView restaurantName = convertView.findViewById(R.id.restaurantName);
        TextView rating = convertView.findViewById(R.id.rating);
        TextView reviewerName = convertView.findViewById(R.id.reviewerName);
        TextView reviewText = convertView.findViewById(R.id.reviewText);
        TextView dateCreated = convertView.findViewById(R.id.dateCreated);

        restaurantName.setText(review.getRestaurantName());

        rating.setText(review.getRating() + "/5");

        String reviewer = review.getReviewerName();
        if (reviewer != null && !reviewer.isEmpty()) {
            reviewerName.setText("Reviewed by: " + reviewer);
        } else {
            reviewerName.setText("Reviewed by: Anonymous");
        }

        reviewText.setText(review.getReviewText().isEmpty() ? "No review text" : review.getReviewText());

        String date = review.getDateCreated();
        if (date != null && date.length() >= 10) {
            dateCreated.setText(date.substring(0, 10));
        } else {
            dateCreated.setText(date);
        }

        return convertView;
    }
}
