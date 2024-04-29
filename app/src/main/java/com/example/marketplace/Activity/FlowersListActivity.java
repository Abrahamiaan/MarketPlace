package com.example.marketplace.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.marketplace.Adapter.ProductGridAdapter;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.databinding.ActivityFlowersListBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FlowersListActivity extends AppCompatActivity {
    ActivityFlowersListBinding binding;
    private ProductGridAdapter productGridAdapter;
    ArrayList<FlowerModel> flowersList = new ArrayList<>();
    String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFlowersListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
        initListeners();
    }

    private void initGlobalFields() {
        categoryName = getIntent().getStringExtra("categoryName");
        String title = getIntent().getStringExtra("pageTitle");
        binding.titleTxt.setText(title);
        productGridAdapter = new ProductGridAdapter(this, flowersList);
        binding.productRecyclerView.setLayoutManager(new GridLayoutManager(FlowersListActivity.this,2));
        binding.productRecyclerView.setAdapter(productGridAdapter);

        fetchDataFromFirestore();
    }

    private void initListeners() {
        binding.toBack.setOnClickListener(v -> onBackPressed());
    }

    private void fetchDataFromFirestore() {
        binding.progressBar.setVisibility(View.VISIBLE);
        CollectionReference flowersCollection = FirebaseFirestore.getInstance().collection("Products");
        flowersCollection
                .whereEqualTo("category", categoryName)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    FlowerModel flowerModel = document.toObject(FlowerModel.class);
                    flowersList.add(flowerModel);
                    productGridAdapter.notifyItemInserted(flowersList.size());
                }
                binding.progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Failed to fetch data from Firestore", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
}