package com.example.marketplace.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.AttachedSurfaceControl;
import android.view.View;
import android.widget.RadioButton;

import com.example.marketplace.Adapter.ProductAdapter;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.Utils.SortHelper;
import com.example.marketplace.Utils.TestingHelper;
import com.example.marketplace.databinding.ActivitySearchResultBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    enum ViewMode {
        Grid,
        List,
    }

    final static String TAG = "Search Result: ";
    ActivitySearchResultBinding binding;
    FirebaseFirestore db;
    CollectionReference productsCollection;
    List<FlowerModel> flowersList;
    List<FlowerModel> flowersListCopy;
    ProductAdapter productAdapter;
    RadioButton bestMatch;
    RadioButton lowToHigh;
    RadioButton highToLow;
    RadioButton newlyListed;
    BottomSheetDialog bottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        productsCollection = db.collection("Products");
        flowersList = new ArrayList<>();

        final ViewMode[] currentViewMode = {ViewMode.Grid};

        binding.gridViewIcon.setOnClickListener(v -> {
            if (currentViewMode[0] != ViewMode.Grid) {
                currentViewMode[0] = ViewMode.Grid;
                initRecycler(currentViewMode[0]);
            }
        });

        binding.listViewIcon.setOnClickListener(v -> {
            if (currentViewMode[0] != ViewMode.List) {
                currentViewMode[0] = ViewMode.List;
                initRecycler(currentViewMode[0]);
            }
        });

        binding.toSearch.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        binding.toBack.setOnClickListener(v -> onBackPressed());
        setSortData();
        searchByTitle();
        initRecycler(ViewMode.Grid);
    }

    private void searchByTitle() {
        String query = getIntent().getStringExtra("query");
        binding.searchQuery.setText(query);

        Query titleQuery = productsCollection
                .orderBy("title")
                .startAt(query)
                .endAt(query +'\uf8ff' );

        titleQuery.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String title = document.getString("title");
                                String category = document.getString("category");
                                long price = document.getLong("price");
                                String details = document.getString("details");
                                String photo = document.getString("photo");
                                String seller = document.getString("seller");
                                String sellerId = document.getString("sellerId");
                                Timestamp listedTimeTimeStamp = document.getTimestamp("listedTime");
                                Date listedTime = null;

                                if (listedTimeTimeStamp != null)
                                    listedTime = listedTimeTimeStamp.toDate();

                                int flowerPrice = (int) price;
                                FlowerModel flowerModel = new FlowerModel(title, flowerPrice, photo);
                                flowerModel.setProductId(document.getId());
                                flowerModel.setDetails(details);
                                flowerModel.setCategory(category);
                                flowerModel.setSeller(seller);
                                flowerModel.setSellerId(sellerId);
                                flowerModel.setListedTime(listedTime);
                                flowersList.add(flowerModel);
                            }

                            binding.progressBar.setVisibility(View.GONE);
                            binding.notMatches.setVisibility(View.GONE);
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            flowersListCopy = new ArrayList<>(flowersList);
                            productAdapter.notifyDataSetChanged();
                        }
                        else {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.notMatches.setVisibility(View.VISIBLE);
                            binding.recyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e(TAG, exception.getMessage());
                        }
                    }
                });
    }
    private void initRecycler(ViewMode view) {
        RecyclerView recyclerView = binding.recyclerView;

        int layoutResId = 0;
        if (view == ViewMode.Grid) {
            layoutResId = R.layout.product_item;
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            binding.gridViewIcon.setColorFilter(R.color.pink);
            binding.gridViewIcon.setColorFilter(R.color.grey);
        } else if (view == ViewMode.List) {
            layoutResId = R.layout.product_list_item;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            binding.gridViewIcon.setColorFilter(R.color.grey);
            binding.listViewIcon.setColorFilter(R.color.pink);
        }

        productAdapter = new ProductAdapter(this, flowersList, layoutResId);
        recyclerView.setAdapter(productAdapter);
    }
    private void setRadioButtonListener(RadioButton radioButton, Runnable action) {
        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                action.run();
            }
        });
    }

    private void setSortData() {
        binding.sortLayout.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.sort_by_dialog, null);

            bestMatch = dialogView.findViewById(R.id.bestMatch);
            lowToHigh = dialogView.findViewById(R.id.lowToHigh);
            highToLow = dialogView.findViewById(R.id.highToLow);
            newlyListed = dialogView.findViewById(R.id.newlyListed);

            bottomSheetDialog = new BottomSheetDialog(SearchResultActivity.this);
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();

            setRadioButtonListener(lowToHigh, () -> {
                flowersList.sort(SortHelper.sortByAscPrice);
                productAdapter.notifyDataSetChanged();
            });

            setRadioButtonListener(highToLow, () -> {
                flowersList.sort(SortHelper.sortByDescPrice);
                productAdapter.notifyDataSetChanged();
            });

            setRadioButtonListener(newlyListed, () -> {
                flowersList.sort(SortHelper.sortByListedDate);
                productAdapter.notifyDataSetChanged();
            });
        });
    }
}