package com.example.marketplace.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Adapter.CategoryAdapter;
import com.example.marketplace.Adapter.ProductAdapter;
import com.example.marketplace.Adapter.SpecialOffersAdapter;
import com.example.marketplace.Model.Category;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.Model.SpecialOffer;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    FirebaseFirestore db;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();

        return binding.getRoot();
    }

    private void fetchDataFromFirestore() {
        CollectionReference flowersCollection = db.collection("Products");

        flowersCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                        FlowerModel flowerModel = document.toObject(FlowerModel.class);
                        flowersList.add(flowerModel);
                        productAdapter.notifyItemInserted(flowersList.size());
                }
            } else {
                Log.e("Home Fragment", "Failed to fetch data from Firestore");
            }
        });
    }

    private void initRecyclerView() {
        categoryList = new ArrayList<>();

        categoryList.add(new Category(1,  getString(R.string.house_plants), "House Plants"));
        categoryList.add(new Category(2,  getString(R.string.roses), "Roses"));
        categoryList.add(new Category(3,  getString(R.string.flowers), "Flowers"));
        categoryList.add(new Category(4,  getString(R.string.outdoor_plants), "Outdoor Plants"));
        categoryList.add(new Category(5,  getString(R.string.lilies), "Lilies"));

        flowersList = new ArrayList<> ();
        fetchDataFromFirestore();
        setCategoryRecycler(categoryList);
        setProductRecycler(flowersList);
        setSpecialOffersRecycler();
        fetchUnreadCount();
    }
    private void setCategoryRecycler(List<Category> categoryDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.categoriesRecycler.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(requireContext(), categoryDataList);
        binding.categoriesRecycler.setAdapter(categoryAdapter);
    }
    private void setSpecialOffersRecycler() {
        List<SpecialOffer> specialOffers = new ArrayList<>();
        SpecialOffer specialOffer1 = new SpecialOffer(getString(R.string._100_off_special_delivery_in_april),
                    R.drawable.rose_example, R.drawable.union_1, R.color.specialOfferFirst);
        SpecialOffer specialOffer2 = new SpecialOffer(getString(R.string.special_offer_second),
                    R.drawable.rose_example_t, R.drawable.union_2, R.color.specialOfferSecond);

        specialOffers.add(specialOffer1);
        specialOffers.add(specialOffer2);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.specialOfferRecycler.setLayoutManager(layoutManager);
        SpecialOffersAdapter categoryAdapter1 = new SpecialOffersAdapter(requireContext(), specialOffers);
        binding.specialOfferRecycler.setAdapter(categoryAdapter1);
    }
    private void setProductRecycler(List<FlowerModel> flowerDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.productRecycler.setLayoutManager(layoutManager);
        productAdapter = new ProductAdapter(requireContext(), flowerDataList, R.layout.product_item);
        binding.productRecycler.setAdapter(productAdapter);
    }
    private void fetchUnreadCount() {
        binding.unreadProgressBar.setVisibility(View.VISIBLE);
        binding.notificationNumberContainer.setVisibility(View.GONE);

        db.collection("Notifications")
                .whereEqualTo("ownerId", currentUser.getUid())
                .whereEqualTo("read", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int count = task.getResult().size();
                        if (count != 0) {
                            binding.unreadCount.setText(String.valueOf(count));
                        }
                    } else {
                        Log.e("UnreadCount", "Error getting documents: ", task.getException());
                    }
                    binding.unreadProgressBar.setVisibility(View.GONE);
                    binding.notificationNumberContainer.setVisibility(View.VISIBLE);
                });
    }
    private void initListeners() {
        binding.notifications.setOnClickListener(v -> replaceFragment(new NotificationFragment()));
        binding.searchBar.setOnClickListener(v -> replaceFragment(new SearchFragment()));
    }
    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        initRecyclerView();
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
