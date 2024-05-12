package com.example.marketplace.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.marketplace.Adapter.ReviewAdapter;
import com.example.marketplace.Model.ReviewModel;
import com.example.marketplace.R;
import com.example.marketplace.databinding.ActivityReviewBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {
    ActivityReviewBinding binding;
    FirebaseFirestore db;
    ReviewAdapter reviewAdapter;
    List<ReviewModel> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
        initListeners();
    }

    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.GONE);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reviewAdapter = new ReviewAdapter(this ,reviewList);
        binding.recyclerView.setAdapter(reviewAdapter);

        fetchReviews();
    }

    private void initListeners() {
        binding.toBack.setOnClickListener(v -> onBackPressed());
        binding.nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                binding.addReview.collapse(true);
            } else {
                binding.addReview.expand(true);
            }
        });
    }

    private void fetchReviews() {
        binding.nestedScrollView.setVisibility(View.GONE);
        binding.notReviewLayout.setVisibility(View.GONE);
        String subjectId = getIntent().getStringExtra("subjectId");

        db.collection("Reviews")
                .whereEqualTo("subjectId", subjectId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        float totalRating = 0;
                        int[] ratingCounts = new int[5];
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ReviewModel review = document.toObject(ReviewModel.class);
                            reviewList.add(review);
                            reviewAdapter.notifyItemInserted(reviewList.size());
                            totalRating += review.getRating();

                            int ratingIndex = (int) review.getRating() - 1;
                            if (ratingIndex >= 0 && ratingIndex < 5) {
                                ratingCounts[ratingIndex]++;
                            }
                        }
                        reviewList.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

                        int reviewsCount = reviewList.size();
                        String reviewString = reviewsCount + " "+ getString(R.string.reviews);
                        binding.textReviewsCount.setText(reviewString);

                        if (reviewsCount == 0) {
                            binding.nestedScrollView.setVisibility(View.GONE);
                            binding.notReviewLayout.setVisibility(View.VISIBLE);
                        } else {
                            updateRatingUI(ratingCounts, reviewsCount, totalRating);
                            binding.progressBar.setVisibility(View.GONE);
                            binding.nestedScrollView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e("REVIEW", "Error getting reviews", task.getException());
                    }
                });
    }

    private void updateRatingUI(int[] ratingCounts, int reviewsCount, float totalRating) {
        if (reviewsCount <= 0) { return; }

        float averageRating = totalRating / reviewsCount;
        String formattedRating = String.format(Locale.US, "%.1f", averageRating);
        binding.overallRating.setText(formattedRating);

        float weightCharge = (float) (100.0 / reviewsCount);
        binding.oneTotal.setText(String.valueOf(ratingCounts[0]));
        binding.secondTotal.setText(String.valueOf(ratingCounts[1]));
        binding.threeTotal.setText(String.valueOf(ratingCounts[2]));
        binding.fourTotal.setText(String.valueOf(ratingCounts[3]));
        binding.fiveTotal.setText(String.valueOf(ratingCounts[4]));
        setViewWeight(binding.oneActive, ratingCounts[0] * weightCharge);
        setViewWeight(binding.secondActive, ratingCounts[1] * weightCharge);
        setViewWeight(binding.threeActive, ratingCounts[2] * weightCharge);
        setViewWeight(binding.fourActive, ratingCounts[3] * weightCharge);
        setViewWeight(binding.fiveActive, ratingCounts[4] * weightCharge);
    }

    private void setViewWeight(View view, float weight) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) params;
            layoutParams.weight = weight;
            view.setLayoutParams(layoutParams);
        }
    }
}