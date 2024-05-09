package com.example.marketplace.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;

import com.example.marketplace.Adapter.ProductAdapter;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.Model.NotificationModel;
import com.example.marketplace.Model.ProductModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.Utils.NotificationHelper;
import com.example.marketplace.databinding.ActivityConfirmationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConfirmationActivity extends AppCompatActivity {
    ActivityConfirmationBinding binding;
    final String COLLECTION = "UnconfirmedProducts";
    List<FlowerModel> unconfirmedList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ProductAdapter productAdapter;
    NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivityConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
        initListeners();
    }

    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        unconfirmedList = new ArrayList<>();
        notificationHelper = new NotificationHelper(this);

        initProductRecycler();
        fetchDataFromFirestore();
    }
    private void initListeners() {
        binding.toBack.setOnClickListener(v -> onBackPressed());
    }
    private void fetchDataFromFirestore() {
        CollectionReference flowersCollection = db.collection(COLLECTION);

        binding.progressBar.setVisibility(View.VISIBLE);
        flowersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    FlowerModel flowerModel = document.toObject(FlowerModel.class);
                    unconfirmedList.add(flowerModel);
                    productAdapter.notifyItemInserted(unconfirmedList.size());
                }
                binding.progressBar.setVisibility(View.GONE);
                if (unconfirmedList.size() == 0) {
                    binding.notUnconfirmed.setVisibility(View.VISIBLE);
                }
            } else {
                 Log.e("Confirm Data", "Failed to fetch data from Firestore");
                 binding.progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void initProductRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        binding.productRecycler.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(ConfirmationActivity.this,this, unconfirmedList, R.layout.product_list_item);
        binding.productRecycler.setAdapter(productAdapter);
    }
    private void confirmProduct(int position) {
        ProductModel product = unconfirmedList.get(position);
        NotificationModel notificationModel = new NotificationModel("Your product '" + product.getTitle() + "' has been confirmed.",
                "Product Confirmation", product.getSellerId(),
                "PRODUCT_CONFIRMATION_STATUS");

        db.collection("Products").document(product.getProductId())
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    notificationHelper.makeNotification(notificationModel);
                    removeProduct(position, 0);
                })
                .addOnFailureListener(e -> Log.e("Confirmation", "Error confirming product: " + e.getMessage(), e));
    }
    private void removeProduct(int position, int mode) {
        ProductModel product = unconfirmedList.get(position);
        if (mode == 1) {
            NotificationModel notificationModel = new NotificationModel("Your product '" + product.getTitle() + "' has been disapproved.","Product Disapproval", product.getSellerId(), "PRODUCT_CONFIRMATION_STATUS");
            notificationHelper.makeNotification(notificationModel);
        }

        db.collection(COLLECTION).document(product.getProductId())
                .delete()
                .addOnSuccessListener(aVoid1 -> {
                    Log.d("Confirmation", "Product disproved and removed successfully");
                    unconfirmedList.remove(product);
                    productAdapter.notifyItemRemoved(position);
                    if (unconfirmedList.size() == 0) {
                        binding.notUnconfirmed.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> Log.e("Confirmation", "Error removing product: " + e.getMessage(), e));
    }
    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.admin_confirm_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.approve) {
                confirmProduct(position);
                return true;
            } else if (itemId == R.id.disprove) {
                removeProduct(position, 1);
                return true;
            }
            return false;
        });

        popup.show();
    }
}