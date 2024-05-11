package com.example.marketplace.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.marketplace.Model.ReviewModel;
import com.example.marketplace.databinding.ActivityAddReviewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class AddReviewActivity extends AppCompatActivity {
    ActivityAddReviewBinding binding;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Intent currentIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityAddReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
        initClickListeners();
    }

    private void saveReview() {
        String reviewTxt = binding.review.getText().toString();
        String reviewerId = currentUser.getUid();
        String reviewerName = currentUser.getDisplayName();
        String subjectId = currentIntent.getStringExtra("subjectId");
        String subjectName = currentIntent.getStringExtra("subject");
        int rating = (int) binding.rating.getRating();

        ReviewModel reviewModel = new ReviewModel();
        reviewModel.setReviewerId(reviewerId);
        reviewModel.setRating(rating);
        reviewModel.setReviewTxt(reviewerName);
        reviewModel.setCreatedAt(new Date());

        db.collection("Reviews")
                .add(reviewModel)
                .addOnSuccessListener(documentReference -> Log.d("ADD REVIEW", "Review added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("ADD REVIEW", "Error adding review", e));
    }

    private void initGlobalFields() {
        currentIntent = getIntent();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void initClickListeners() {
        binding.sendReview.setOnClickListener(v -> saveReview());
    }
}