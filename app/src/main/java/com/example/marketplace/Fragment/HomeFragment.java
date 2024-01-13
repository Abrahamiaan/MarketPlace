package com.example.marketplace.Fragment;

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
import com.example.marketplace.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    List<Category> categoryList;
    List<FlowerModel> flowersList;
    CategoryAdapter categoryAdapter;
    ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initRecyclerView();
        setCategoryRecycler(categoryList);
        setProductRecycler(flowersList);

        return binding.getRoot();
    }

    private void fetchDataFromFirestore() {
        CollectionReference flowersCollection = FirebaseFirestore.getInstance().collection("Products");

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Date startOfDay = today.getTime();
        today.add(Calendar.DAY_OF_MONTH, 1);
        Date startOfNextDay = today.getTime();

        flowersCollection.whereGreaterThanOrEqualTo("listedTime", startOfDay)
                .whereLessThan("listedTime", startOfNextDay)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Date listedTime = document.getDate("listedTime");

                    if (listedTime != null) {
                        String title = document.getString("title");
                        String category = document.getString("category");
                        long price = document.getLong("price");
                        String details = document.getString("details");
                        String photo = document.getString("photo");
                        String seller = document.getString("seller");

                        int flowerPrice = (int) price;
                        FlowerModel flowerModel = new FlowerModel(title, flowerPrice, photo);
                        flowerModel.setProductId(document.getId());
                        flowerModel.setDetails(details);
                        flowerModel.setCategory(category);
                        flowerModel.setSeller(seller);
                        flowersList.add(flowerModel);
                    }
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
        categoryList.add(new Category(2, R.drawable.tulip, "Tulip"));
        categoryList.add(new Category(3, R.drawable.lily, "Lily"));
        categoryList.add(new Category(4, R.drawable.sunflower, "Sunflower"));
        categoryList.add(new Category(4, R.drawable.plant, "Plants"));

        flowersList = new ArrayList<> ();
        fetchDataFromFirestore();
    }

    private void setCategoryRecycler(List<Category> categoryDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.categoryRecycler.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(requireContext(), categoryDataList);
        binding.categoryRecycler.setAdapter(categoryAdapter);
    }

    private void setProductRecycler(List<FlowerModel> flowerDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.productRecycler.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(requireContext(), flowerDataList, R.layout.product_item);
        binding.productRecycler.setAdapter(productAdapter);
    }
}
