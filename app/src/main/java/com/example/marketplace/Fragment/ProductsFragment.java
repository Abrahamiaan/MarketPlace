package com.example.marketplace.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marketplace.Adapter.ProductAdapter;
import com.example.marketplace.Adapter.TestAdapter;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.Constants;
import com.example.marketplace.databinding.FragmentProductsBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {
    public static final String[] categories = Constants.categories;
    public static final String TAG = "PRODUCT_FRAGMENT";
    FragmentProductsBinding binding;
    TestAdapter productAdapterOne;
    TestAdapter productAdapterSecond;
    TestAdapter productAdapterThird;
    List<FlowerModel> productModelsOne = new ArrayList<>();
    List<FlowerModel> productModelsSecond = new ArrayList<>();
    List<FlowerModel> productModelsThird = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

//    private void initGlobalFields() {
//        productAdapterOne = new TestAdapter(requireContext(), productModelsOne, R.layout.product_item);
//        productAdapterSecond = new TestAdapter(requireContext(), productModelsSecond, R.layout.product_item);
//        productAdapterThird = new TestAdapter(requireContext(), productModelsThird, R.layout.product_item);
//
//        binding.categoryHousePlantsRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//        binding.categoryHousePlantsRecycler.setAdapter(productAdapterOne);
//
//        binding.categoryDecorativeRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//        binding.categoryDecorativeRecycler.setAdapter(productAdapterSecond);
//
//        binding.categoryFlowersRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//        binding.categoryFlowersRecycler.setAdapter(productAdapterThird);
//
//        fetchDataFromFirestore();
//    }

//    private void initListeners() { }
//
//    private void fetchDataFromFirestore() {
//        CollectionReference flowersCollection = FirebaseFirestore.getInstance().collection("Products");
//
//        flowersCollection.get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            FlowerModel flowerModel = document.toObject(FlowerModel.class);
//                            if (flowerModel.getCategory().equals(categories[0])) {
//                                productModelsOne.add(flowerModel);
//                                productAdapterOne.notifyItemInserted(productModelsOne.size());
//                            } else if (flowerModel.getCategory().equals(categories[1])) {
//                                productModelsSecond.add(flowerModel);
//                                productAdapterSecond.notifyItemInserted(productModelsSecond.size());
//                            } else if (flowerModel.getCategory().equals(categories[2])) {
//                                productModelsThird.add(flowerModel);
//                                productAdapterThird.notifyItemInserted(productModelsThird.size());
//                            }
//                        }
//                    } else {
//                        Log.e(TAG, "Failed to fetch data from Firestore");
//                    }
//                });
//    }
}