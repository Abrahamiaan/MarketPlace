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

import com.example.marketplace.Adapter.ProductAdapter;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentAdsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdsFragment extends Fragment {
    private static final String TAG_ADS = "Ads: ";
    ProductAdapter productAdapter;
    FragmentAdsBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    List<FlowerModel> adsList;

    public AdsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdsBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();

        return binding.getRoot();
    }

    private void fetchUserAds() {
        String userId = mAuth.getCurrentUser().getUid();
        CollectionReference products = db.collection("Products");

        products.whereEqualTo("sellerId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                FlowerModel flwModel  = document.toObject(FlowerModel.class);
                                adsList.add(flwModel);
                                System.out.println(flwModel.getTitle());
                                productAdapter.notifyItemInserted(adsList.size());
                            }
                            binding.progressBar.setVisibility(View.GONE);
                        } else {
                            Log.e(TAG_ADS, "No ads found for the user");
                            binding.progressBar.setVisibility(View.GONE);
                            binding.haveNotAds.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.e(TAG_ADS, "Error fetching user ads", task.getException());
                    }
                });
    }
    private void setFavoriteRecycler(List<FlowerModel> favoriteDataList) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.adsRecycler.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(requireContext(), favoriteDataList, R.layout.product_item);
        binding.adsRecycler.setAdapter(productAdapter);
    }
    private void initListeners() {
        binding.toBack.setOnClickListener(v-> getActivity().onBackPressed());
    }
    private void initGlobalFields() {
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        adsList = new ArrayList<>();
        setFavoriteRecycler(adsList);
        fetchUserAds();
    }
}