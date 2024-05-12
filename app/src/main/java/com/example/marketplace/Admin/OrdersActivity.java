package com.example.marketplace.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.marketplace.Adapter.OrderAdapter;
import com.example.marketplace.Model.OrderModel;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.databinding.ActivityOrdersBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {
    ActivityOrdersBinding binding;
    FirebaseFirestore db;
    List<OrderModel> orders;
    OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
    }

    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        orders = new ArrayList<>();
        setupRecycler();
        fetchDataFromFirestore();
        initListeners();
    }

    private void initListeners() {
        binding.toBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.orderRecycler.setLayoutManager(layoutManager);
        orderAdapter = new OrderAdapter(this, orders, OrdersActivity.this);
        binding.orderRecycler.setAdapter(orderAdapter);
    }

    private void fetchDataFromFirestore() {
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("Orders")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    orders.clear();
                    for (QueryDocumentSnapshot document : value) {
                        OrderModel orderModel = document.toObject(OrderModel.class);
                        orders.add(orderModel);
                    }

                    orderAdapter.notifyItemRangeChanged(0, orders.size());
                    binding.progressBar.setVisibility(View.GONE);

                    if (orders.size() == 0) {
                        binding.notOrders.setVisibility(View.VISIBLE);
                    } else {
                        binding.notOrders.setVisibility(View.GONE);
                    }
                });
    }
    public void updateOrder(OrderModel orderModel, String driverId) {
        orderModel.setAssignedDriverId(driverId);
        orderModel.setStatus("Assigned");
        db.collection("Orders").document(orderModel.getOrderId())
                .set(orderModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("OrderUpdate", "Order update successful: " + orderModel.getOrderId());
                    } else {
                        Log.e("OrderUpdate", "Order update failed: " + orderModel.getOrderId(), task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.e("OrderUpdate", "Error updating order: " + orderModel.getOrderId(), e));
    }
}