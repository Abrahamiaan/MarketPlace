package com.example.marketplace.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marketplace.Adapter.ProductGridAdapter;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.databinding.FragmentFavoriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteFragment extends Fragment {
    private static final String TAG_FAVORITE = "Favorite: ";
    FragmentFavoriteBinding binding;
    ProductGridAdapter productAdapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    List<FlowerModel> favoriteList;
    public FavoriteFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);

        initGlobalFields();
        fetchUserFavorites();

        return binding.getRoot();
    }

    private void fetchUserFavorites() {
        String userId = mAuth.getCurrentUser().getUid();
        binding.progressBar.setVisibility(View.VISIBLE);

        DocumentReference userFavoritesDocument = db.collection("Favorites").document(userId);

        userFavoritesDocument.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Map<String, Object> productIds = documentSnapshot.getData();
                            if (productIds != null) {
                                if (productIds.size() == 0) {
                                    binding.notFavourites.setVisibility(View.VISIBLE);
                                } else {
                                    fetchProducts(new ArrayList<>(productIds.keySet()));
                                }
                            }
                            binding.progressBar.setVisibility(View.GONE);
                        } else {
                            Log.e(TAG_FAVORITE, "User favorites document does not exist");
                            binding.notFavourites.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Log.e(TAG_FAVORITE, "Error fetching user favorites", task.getException());
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
    private void fetchProducts(List<String> productIds) {
        if (productIds != null) {
            for (String productId : productIds) {
                DocumentReference productDocument = db.collection("Products").document(productId);

                productDocument.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot productSnapshot = task.getResult();
                                if (productSnapshot != null && productSnapshot.exists()) {
                                    FlowerModel flowerModel = productSnapshot.toObject(FlowerModel.class);
                                    favoriteList.add(flowerModel);
                                    productAdapter.notifyItemInserted(favoriteList.size());
                                } else {
                                    Log.e(TAG_FAVORITE, "Product document with ID " + productId + " does not exist");
                                }
                            } else {
                                Log.e(TAG_FAVORITE, "Error fetching product document with ID " + productId, task.getException());
                            }
                        });
                if (favoriteList.isEmpty()) { binding.notFavourites.setVisibility(View.VISIBLE);  }
            }
        }
    }

    private void setFavoriteRecycler(List<FlowerModel> favoriteDataList) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.favoriteRecycler.setLayoutManager(layoutManager);
        productAdapter = new ProductGridAdapter(requireContext(), favoriteDataList);
        binding.favoriteRecycler.setAdapter(productAdapter);
    }
    private void initGlobalFields() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        favoriteList = new ArrayList<>();
        setFavoriteRecycler(favoriteList);
    }
}