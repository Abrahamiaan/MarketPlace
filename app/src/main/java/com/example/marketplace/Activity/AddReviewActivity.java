package com.example.marketplace.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplace.Model.ReviewModel;
import com.example.marketplace.databinding.ActivityAddReviewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class AddReviewActivity extends AppCompatActivity {
    static final String TAG = "ADD REVIEW: ";
    ActivityAddReviewBinding binding;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Intent currentIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        currentIntent = getIntent();
        binding.button.setOnClickListener(v -> saveReview());
    }

    private void saveReview() {
        String comment = binding.review.getText().toString();
        float rating = binding.rating.getRating();
        String reviewerId = currentUser.getUid();
        String reviewerName = currentUser.getDisplayName();
        String subjectId = currentIntent.getStringExtra("subjectId");
        String subjectName = currentIntent.getStringExtra("subject");

        ReviewModel review = new ReviewModel();
        review.setReviewerId(reviewerId);
        review.setReviewerName(reviewerName);
        review.setRating(rating);
        review.setSubjectId(subjectId);
        review.setSubjectName(subjectName);
        review.setComment(comment);
        review.setCreatedAt(new Date());

        db.collection("Reviews").add(review)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Review added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding review", e);
                });
    }
}
