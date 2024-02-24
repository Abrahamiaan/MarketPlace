package com.example.marketplace.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.marketplace.Adapter.CartAdapter;
import com.example.marketplace.Model.CartModel;
import com.example.marketplace.databinding.FragmentCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    FragmentCartBinding binding;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartModel> cartItems;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    int totalSum;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);

        initGlobalFields();

        return binding.getRoot();
    }
    private void initGlobalFields() {
        recyclerView = binding.cartRecycler;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        cartItems = new ArrayList<>();
        binding.progressView.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(getContext(), cartItems, this);
        recyclerView.setAdapter(cartAdapter);

        fetchCartItems();
    }
    private void fetchCartItems() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("CartItems")
                .whereEqualTo("ownerId", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CartModel cartModel = document.toObject(CartModel.class);
                            cartItems.add(cartModel);
                            cartAdapter.notifyItemInserted(cartItems.size());
                            binding.progressView.setVisibility(View.GONE);
                            binding.nestedScrollView.setVisibility(View.VISIBLE);
                        }
                        if (cartItems.isEmpty()) {
                            binding.cartIsEmptyTxt.setVisibility(View.VISIBLE);
                        } else {
                            binding.cartIsEmptyTxt.setVisibility(View.GONE);
                            mergeDuplicateItems();
                        }
                    } else {
                        Log.e("FetchCart", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void mergeDuplicateItems() {
        Map<String, CartModel> mergedItems = new HashMap<>();

        for (CartModel item : cartItems) {
            String productId = item.getProductModel().getProductId();
            if (mergedItems.containsKey(productId)) {
                CartModel existingItem = mergedItems.get(productId);
                int currentCount = existingItem.getCount() + item.getCount();
                int max = item.getProductModel().getAvailableCount();
                existingItem.setCount(Math.min(currentCount, max));
            } else {
                mergedItems.put(productId, item);
            }
        }

        cartItems.clear();
        cartItems.addAll(mergedItems.values());
        cartAdapter.notifyItemRangeChanged(0, cartItems.size());
        updateTotalSum();
    }
    public void updateTotalSum() {
        totalSum = 0;
        for (int i = 0; i < cartItems.size(); i++) {
            totalSum += cartItems.get(i).getCount() * cartItems.get(i).getProductModel().getPrice();
        }
        binding.totalValue.setText(String.valueOf(totalSum));
    }
}
