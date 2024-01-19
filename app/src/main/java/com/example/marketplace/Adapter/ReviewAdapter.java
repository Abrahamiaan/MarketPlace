package com.example.marketplace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.R;
import com.example.marketplace.Model.ReviewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    List<ReviewModel> reviewList;

    public ReviewAdapter(List<ReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel currentReview = reviewList.get(position);

        holder.reviewer.setText(currentReview.getReviewerName());
        holder.review.setText(currentReview.getComment());
        holder.ratingBar.setRating(currentReview.getRating());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        String formattedDate = dateFormat.format(currentReview.getCreatedAt());
        holder.createdAt.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewer;
        TextView createdAt;
        TextView review;
        RatingBar ratingBar;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewer = itemView.findViewById(R.id.reviewer);
            review = itemView.findViewById(R.id.reviewTxt);
            ratingBar = itemView.findViewById(R.id.ratingByCommentator);
            createdAt = itemView.findViewById(R.id.createDate);
        }
    }
}
