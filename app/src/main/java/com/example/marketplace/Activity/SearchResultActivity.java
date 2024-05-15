package com.example.marketplace.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.example.marketplace.Adapter.ProductGridAdapter;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.Utils.SortHelper;
import com.example.marketplace.databinding.ActivitySearchResultBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    enum ViewMode {
        Grid,
        List,
    }
    ActivitySearchResultBinding binding;
    FirebaseFirestore db;
    List<FlowerModel> flowersList;
    List<FlowerModel> flowersListCopy;
    ProductGridAdapter productAdapter;
    RadioButton bestMatch;
    RadioButton lowToHigh;
    RadioButton highToLow;
    RadioButton newlyListed;
    BottomSheetDialog bottomSheetDialog;
    final ViewMode[] currentViewMode = {ViewMode.Grid};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
        initListeners();
        setSortData();
        searchByTitle();
        initRecycler(ViewMode.Grid);
    }

    private void searchByTitle() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String query = getIntent().getStringExtra("query");
        binding.searchQuery.setText(query);

        db.collection("Products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FlowerModel flowerModel = document.toObject(FlowerModel.class);
                            if (flowerModel.getTitle().toLowerCase().contains(query.toLowerCase())
                                    || flowerModel.getCategory().toLowerCase().contains(query.toLowerCase())) {
                                flowersList.add(flowerModel);
                                productAdapter.notifyItemInserted(flowersList.size());
                            }
                        }

                        binding.progressBar.setVisibility(View.GONE);
                        if (flowersList.isEmpty()) {
                            binding.notMatches.setVisibility(View.VISIBLE);
                        } else {
                            flowersListCopy = new ArrayList<>();
                            flowersListCopy.addAll(flowersList);
                        }
                    }
                    else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.notMatches.setVisibility(View.VISIBLE);
                    }
                });
    }
    private void initRecycler(ViewMode view) {
        RecyclerView recyclerView = binding.recyclerView;

        if (view == ViewMode.Grid) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            binding.gridViewIcon.setColorFilter(R.color.pink);
            binding.gridViewIcon.setColorFilter(R.color.grey);
        } else if (view == ViewMode.List) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            binding.gridViewIcon.setColorFilter(R.color.grey);
            binding.listViewIcon.setColorFilter(R.color.pink);
        }

        productAdapter = new ProductGridAdapter(this, flowersList);
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
            @SuppressLint("InflateParams")
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
                productAdapter.notifyItemRangeChanged(0, flowersList.size());
            });

            setRadioButtonListener(highToLow, () -> {
                flowersList.sort(SortHelper.sortByDescPrice);
                productAdapter.notifyItemRangeChanged(0, flowersList.size());
            });

            setRadioButtonListener(newlyListed, () -> {
                flowersList.sort((o1, o2) -> o2.getListedTime().compareTo(o1.getListedTime()));
                productAdapter.notifyItemRangeChanged(0, flowersList.size());
            });

            setRadioButtonListener(bestMatch, () -> {
                flowersList.clear();
                flowersList.addAll(flowersListCopy);
                productAdapter.notifyItemRangeChanged(0, flowersList.size());
            });
        });
    }
    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        flowersList = new ArrayList<>();
    }
    private void initListeners() {
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
    }
}