package com.example.marketplace.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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


        initListeners();
        initRecyclerView();
        setCategoryRecycler(categoryList);
        setProductRecycler(flowersList);

        return binding.getRoot();
    }

    private void fetchDataFromFirestore() {
        CollectionReference flowersCollection = FirebaseFirestore.getInstance().collection("Products");

        flowersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                        FlowerModel flowerModel = document.toObject(FlowerModel.class);
                        flowersList.add(flowerModel);
                        productAdapter.notifyItemInserted(flowersList.size());
                }
            } else {
                Toast.makeText(requireContext(), "Failed to fetch data from Firestore", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initRecyclerView() {
        categoryList = new ArrayList<>();

        categoryList.add(new Category(1, R.drawable.rose, "FIRST"));
        categoryList.add(new Category(2, R.drawable.tulip, "SECOND"));
        categoryList.add(new Category(3, R.drawable.lily, "THIRD"));

        flowersList = new ArrayList<> ();
        fetchDataFromFirestore();
    }
    private void setCategoryRecycler(List<Category> categoryDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.topAdsRecycler.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(requireContext(), categoryDataList);
        binding.topAdsRecycler.setAdapter(categoryAdapter);
    }
    private void setProductRecycler(List<FlowerModel> flowerDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.productRecycler.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(requireContext(), flowerDataList, R.layout.product_item);
        binding.productRecycler.setAdapter(productAdapter);
    }
    private void initListeners() {
        binding.notifications.setOnClickListener(v -> replaceFragment(new NotificationFragment()));
        binding.searchBar.setOnClickListener(v -> replaceFragment(new SearchFragment()));
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
