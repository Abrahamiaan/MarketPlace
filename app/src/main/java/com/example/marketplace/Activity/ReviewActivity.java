package com.example.marketplace.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.marketplace.Adapter.ReviewAdapter;
import com.example.marketplace.Model.ReviewModel;
import com.example.marketplace.databinding.ActivityReviewBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {
    static final String TAG = "REVIEW: ";
    ActivityReviewBinding binding;
    FirebaseFirestore db;
    ReviewAdapter reviewAdapter;
    List<ReviewModel> reviewList = new ArrayList<>();
    int reviewsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.GONE);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        binding.recyclerView.setAdapter(reviewAdapter);

        binding.toBack.setOnClickListener(v -> onBackPressed());

        fetchReviews();
        binding.addReview.setOnClickListener(v -> {
            String subject = getIntent().getStringExtra("subject");
            String subjectId = getIntent().getStringExtra("subjectId");
            Intent intent = new Intent(ReviewActivity.this, AddReviewActivity.class);
            intent.putExtra("subject", subject);
            intent.putExtra("subjectId", subjectId);
            startActivity(intent);
        });
    }

    private void fetchReviews() {
        String subjectId = getIntent().getStringExtra("subjectId");
        Query query = db.collection("Reviews").whereEqualTo("subjectId", subjectId);

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewsCount = task.getResult().size();
                        binding.textReviewsCount.setText(reviewsCount + " reviews");

                        float totalRating = 0;
                        int[] ratingCounts = new int[5];

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ReviewModel review = document.toObject(ReviewModel.class);
                            reviewList.add(review);

                            totalRating += review.getRating();

                            int ratingIndex = (int) review.getRating() - 1;
                            if (ratingIndex >= 0 && ratingIndex < 5) {
                                ratingCounts[ratingIndex]++;
                            }
                        }

                        updateRatingUI(ratingCounts, reviewsCount, totalRating);
                        reviewAdapter.notifyDataSetChanged();
                        binding.progressBar.setVisibility(View.GONE);
                        binding.nestedScrollView.setVisibility(View.VISIBLE);
                    } else {
                        Log.e(TAG, "Error getting reviews", task.getException());
                    }
                });
    }

    private void updateRatingUI(int[] ratingCounts, int reviewsCount, float totalRating) {
        if (reviewsCount > 0) {
            float averageRating = totalRating / reviewsCount;
            String formattedRating = String.format("%.1f", averageRating);
            binding.overallRating.setText(formattedRating);
        }

        binding.oneTotal.setText(String.valueOf(ratingCounts[0]));
        binding.secondTotal.setText(String.valueOf(ratingCounts[1]));
        binding.threeTotal.setText(String.valueOf(ratingCounts[2]));
        binding.fourTotal.setText(String.valueOf(ratingCounts[3]));
        binding.fiveTotal.setText(String.valueOf(ratingCounts[4]));
        setViewWeight(binding.oneActive, ratingCounts[0] * 10);
        setViewWeight(binding.secondActive, ratingCounts[1] * 10);
        setViewWeight(binding.threeActive, ratingCounts[2] * 10);
        setViewWeight(binding.fourActive, ratingCounts[3] * 10);
        setViewWeight(binding.fiveActive, ratingCounts[4] * 10);
    }

    private void setViewWeight(View view, int weight) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) params;
            layoutParams.weight = weight;
            view.setLayoutParams(layoutParams);
        }
    }
}