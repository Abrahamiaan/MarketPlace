package com.example.marketplace.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView categoryRecyclerView;
    RecyclerView productRecyclerView;
    List<Category> categoryList;
    List<FlowerModel> flowersList;

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

    private void initRecyclerView() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category(1, R.drawable.rose, "Rose"));
        categoryList.add(new Category(2, R.drawable.lily, "Lily"));
        categoryList.add(new Category(3, R.drawable.lotus, "Lotus"));
        categoryList.add(new Category(4, R.drawable.jasmine, "Jasmine"));
        categoryList.add(new Category(5, R.drawable.rose, "Rose"));
        categoryList.add(new Category(6, R.drawable.lily, "Lily"));
        categoryList.add(new Category(7, R.drawable.lotus, "Lotus"));

        flowersList = new ArrayList<>();
        flowersList.add(new FlowerModel("Rose", 125, R.drawable.item1));
        flowersList.add(new FlowerModel("Lily", 220, R.drawable.item2));
        flowersList.add(new FlowerModel("Red Rose", 150, R.drawable.item3));
        flowersList.add(new FlowerModel("Piano", 60, R.drawable.item4));
        flowersList.add(new FlowerModel("Lotus", 100, R.drawable.item5));
    }

    private void setCategoryRecycler(List<Category> categoryDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(layoutManager);
        CategoryAdapter categoryAdapter = new CategoryAdapter(requireContext(), categoryDataList);
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void setProductRecycler(List<FlowerModel> categoryDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        productRecyclerView.setLayoutManager(layoutManager);
        ProductAdapter productAdapter = new ProductAdapter(requireContext(), categoryDataList);
        productRecyclerView.setAdapter(productAdapter);
    }
}
