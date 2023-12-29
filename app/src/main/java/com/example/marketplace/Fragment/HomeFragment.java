package com.example.marketplace.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Adapter.CategoryAdapter;
import com.example.marketplace.Adapter.ProductAdapter;
import com.example.marketplace.Model.Category;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView categoryRecyclerView;
    RecyclerView productRecyclerView;
    List<Category> categoryList;
    List<FlowerModel> flowersList;

    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.categoryRecycler);
        productRecyclerView = view.findViewById(R.id.productRecycler);

        initRecyclerView();
        setCategoryRecycler(categoryList);
        setProductRecycler(flowersList);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchDataFromFirestore() {
        CollectionReference flowersCollection = FirebaseFirestore.getInstance().collection("Products");

        flowersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("title");
                    String category = document.getString("category");
                    long price = document.getLong("price");
                    String details = document.getString("details");
                    String photo = document.getString("photo");

                    int flowerPrice = (int) price;

                    flowersList.add(new FlowerModel(title, flowerPrice, photo));
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "Failed to fetch data from Firestore", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerView() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, R.drawable.rose, "Rose"));
        categoryList.add(new Category(2, R.drawable.lily, "Lily"));
        categoryList.add(new Category(3, R.drawable.lotus, "Lotus"));
        categoryList.add(new Category(4, R.drawable.jasmine, "Jasmine"));

        flowersList = new ArrayList<>();
        fetchDataFromFirestore();
    }

    private void setCategoryRecycler(List<Category> categoryDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(requireContext(), categoryDataList);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setProductRecycler(List<FlowerModel> categoryDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        productRecyclerView.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(requireContext(), categoryDataList);
        productRecyclerView.setAdapter(productAdapter);
    }
}
