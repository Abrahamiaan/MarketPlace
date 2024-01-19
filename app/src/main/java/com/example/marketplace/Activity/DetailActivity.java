package com.example.marketplace.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.databinding.ActivityDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG_FAVORITE = "Favorite: ";
    ActivityDetailBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private boolean isFavorite = false;
    private int drawableRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setAppLanguage(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FlowerModel flowerModel = (FlowerModel) getIntent().getSerializableExtra("object");
        initProduct(flowerModel);

        binding.toBack.setOnClickListener(v-> onBackPressed());

        checkIfFavorite(flowerModel);

        binding.favoriteImg.setOnClickListener(v -> {
            if (!isFavorite)
                addToFavorites(flowerModel);
            else
                removeFromFavorites(flowerModel);
        });

        binding.sellerReviewLayout.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, ReviewActivity.class);
            intent.putExtra("subject", flowerModel.getSeller());
            intent.putExtra("subjectId",flowerModel.getSellerId());
            startActivity(intent);
        });
    }

    private void addToFavorites(FlowerModel flowerModel) {
        String productId = flowerModel.getProductId();
        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference userFavoritesDocument = db.collection("Favorites").document(userId);

        Map<String, Object> newFavorite = Collections.singletonMap(productId, true);

        userFavoritesDocument.set(newFavorite, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG_FAVORITE, "Added to favorites");
                    isFavorite = true;
                    binding.favoriteImg.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.vector_favorite2));
                })
                .addOnFailureListener(e -> Log.e(TAG_FAVORITE, "Failed to add to favorites"));
    }

    private void removeFromFavorites(FlowerModel flowerModel) {
        String productId = flowerModel.getProductId();
        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference userFavoritesDocument = db.collection("Favorites").document(userId);

        userFavoritesDocument.update(productId, FieldValue.delete())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG_FAVORITE, "Removed from favorites");
                    isFavorite = false;
                    binding.favoriteImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_favorite1));
                })
                .addOnFailureListener(e -> Log.e(TAG_FAVORITE, "Failed to remove from favorites"));
    }

    private void checkIfFavorite(FlowerModel flowerModel) {
        String productId = flowerModel.getProductId();
        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference userFavoritesDocument = db.collection("Favorites").document(userId);

        userFavoritesDocument.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            Map<String, Object> favoritesMap = documentSnapshot.getData();

                            isFavorite = favoritesMap != null && favoritesMap.containsKey(productId);

                            drawableRes = isFavorite ? R.drawable.vector_favorite2 : R.drawable.vector_favorite1;
                            binding.favoriteImg.setImageDrawable(ContextCompat.getDrawable(this, drawableRes));

                        } else {
                            Log.e(TAG_FAVORITE, "DocumentSnapshot is null");
                        }
                    } else {
                        Log.e(TAG_FAVORITE, "Error fetching user document", task.getException());
                    }
                });
    }

    private void initProduct(FlowerModel flowerModel) {
        if (flowerModel != null) {
            binding.titleTxt.setText(flowerModel.getTitle());
            binding.sellerTxt.setText(flowerModel.getSeller());
            binding.priceTxt.setText(String.format("%s ֏", flowerModel.getPrice()));
            binding.description.setText(flowerModel.getDetails());


            List<String> colors = flowerModel.getColors();
            if (colors == null || colors.isEmpty()) {
                binding.colorInit.setVisibility(View.GONE);
            }

            List<Double> lengths = flowerModel.getLengths();

            if (lengths == null || lengths.isEmpty()) {
                binding.lengthInit.setVisibility(View.GONE);
            }

            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, R.layout.autocomplete_item, colors);
            ArrayAdapter<Double> lengthAdapter = new ArrayAdapter<>(this, R.layout.autocomplete_item, lengths);

            binding.ColorAutoComplete.setAdapter(colorAdapter);
            binding.LengthAutoComplete.setAdapter(lengthAdapter);

            binding.ColorAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Toast.makeText(DetailActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            });

            binding.LengthAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                Double selectedItem = (Double) parent.getItemAtPosition(position);
                Toast.makeText(DetailActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            });

            Glide.with(this)
                    .load(flowerModel.getPhoto())
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            binding.productImg.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        } else {
            Log.e("Product: ", "Product is Null");
        }
    }
}

