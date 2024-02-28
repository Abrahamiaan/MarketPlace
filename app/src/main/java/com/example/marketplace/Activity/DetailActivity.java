package com.example.marketplace.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.marketplace.Model.CartModel;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.databinding.ActivityDetailBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG_FAVORITE = "Favorite: ";
    private static final int MAP_ZOOM = 15;
    ActivityDetailBinding binding;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    private boolean isFavorite = false;
    private int drawableRes;
    int countOfProductItem = 1;
    FlowerModel flowerModel;
    CartModel cartModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
        initProduct();
        initListeners();
        checkIfFavorite();
    }

    private void addToFavorites(FlowerModel flowerModel) {
        String productId = flowerModel.getProductId();
        String userId = currentUser.getUid();

        DocumentReference userFavoritesDocument = db.collection("Favorites").document(userId);

        Map<String, Object> newFavorite = Collections.singletonMap(productId, true);

        userFavoritesDocument.set(newFavorite, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG_FAVORITE, "Added to favorites");
                    isFavorite = true;
                    binding.favoriteImg.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.vector_favorite2));
                })
                .addOnFailureListener(e -> Log.e(TAG_FAVORITE, "Failed to add to favorites"));
    }
    private void removeFromFavorites(FlowerModel flowerModel) {
        String productId = flowerModel.getProductId();
        String userId = currentUser.getUid();

        DocumentReference userFavoritesDocument = db.collection("Favorites").document(userId);

        userFavoritesDocument.update(productId, FieldValue.delete())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG_FAVORITE, "Removed from favorites");
                    isFavorite = false;
                    binding.favoriteImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.vector_favorite1));
                })
                .addOnFailureListener(e -> Log.e(TAG_FAVORITE, "Failed to remove from favorites"));
    }
    private void checkIfFavorite() {
        String productId = flowerModel.getProductId();
        String userId = currentUser.getUid();

        DocumentReference userFavoritesDocument = db.collection("Favorites").document(userId);

        userFavoritesDocument.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            Map<String, Object> favoritesMap = documentSnapshot.getData();

                            isFavorite = favoritesMap != null && favoritesMap.containsKey(productId);

                            drawableRes = isFavorite ? R.drawable.vector_favorite2 : R.drawable.vector_favorite1;
                            binding.favoriteImg.setImageDrawable(ContextCompat.getDrawable(this, drawableRes));

                        } else {
                            Log.e(TAG_FAVORITE, "DocumentSnapshot is null");
                        }
                    } else {
                        Log.e(TAG_FAVORITE, "Error fetching user document", task.getException());
                    }
                });
    }
    private void initProduct() {
        if (flowerModel != null) {
            binding.titleTxt.setText(flowerModel.getTitle());
            binding.sellerTxt.setText(flowerModel.getSeller());
            binding.priceTxt.setText(String.format("%s ֏", flowerModel.getPrice()));
            binding.description.setText(flowerModel.getDetails());
            binding.countTxt.setText(String.valueOf(flowerModel.getMinimumPurchaseCount()));

            List<String> colors = flowerModel.getColors();
            if (colors == null || colors.isEmpty()) {
                binding.colorInit.setVisibility(View.GONE);
            }

            List<Double> lengths = flowerModel.getLengths();
            if (lengths == null || lengths.isEmpty()) {
                binding.lengthInit.setVisibility(View.GONE);
            }

            ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this, R.layout.autocomplete_item, colors);
            ArrayAdapter<Double> lengthAdapter = new ArrayAdapter<>(this, R.layout.autocomplete_item, lengths);

            binding.ColorAutoComplete.setAdapter(colorAdapter);
            binding.LengthAutoComplete.setAdapter(lengthAdapter);

            binding.ColorAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Toast.makeText(DetailActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            });
            binding.LengthAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
                Double selectedItem = (Double) parent.getItemAtPosition(position);
                Toast.makeText(DetailActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            });

            Glide.with(this)
                    .load(flowerModel.getPhoto())
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            binding.productImg.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        } else {
            Log.e("Product: ", "Product is Null");
        }
    }
    private void initListeners() {
        binding.addToCardBtn.setOnClickListener(v -> {
            cartModel = new CartModel(flowerModel, Integer.parseInt(binding.countTxt.getText().toString()), currentUser.getUid());
            addToCart(cartModel);
        });
        binding.countTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && (event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String text = v.getText().toString();
                try {
                    int count = Integer.parseInt(text);
                    if (count < flowerModel.getMinimumPurchaseCount() ) {
                        v.setText(String.valueOf(flowerModel.getMinimumPurchaseCount()));
                    } else if (count > flowerModel.getAvailableCount()) {
                        v.setText(String.valueOf(flowerModel.getAvailableCount()));
                    }
                    if (binding.countTxt.hasFocus()) {
                        binding.countTxt.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(binding.countTxt.getWindowToken(), 0);
                    }
                } catch (NumberFormatException e) {
                    Log.e("NumberFormatException", Arrays.toString(e.getStackTrace()));
                }
                return true;
            }
            return false;
        });

        binding.toBack.setOnClickListener(v-> onBackPressed());
        binding.favoriteImg.setOnClickListener(v -> {
            if (!isFavorite)
                addToFavorites(flowerModel);
            else
                removeFromFavorites(flowerModel);
        });
        binding.sellerReviewLayout.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, ReviewActivity.class);
            intent.putExtra("subject", flowerModel.getSeller());
            intent.putExtra("subjectId",flowerModel.getSellerId());
            startActivity(intent);
        });

        binding.countPlus.setOnClickListener(v -> {
            int min = flowerModel.getMinimumPurchaseCount();
            int max = flowerModel.getAvailableCount();
            if (binding.countTxt.hasFocus()) {
                binding.countTxt.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.countTxt.getWindowToken(), 0);
            }

            countOfProductItem = Integer.parseInt(String.valueOf(binding.countTxt.getText()));
            if (countOfProductItem < min) {
                binding.countTxt.setText(String.valueOf(min));
            } else if (countOfProductItem + 1 < max) {
                binding.countTxt.setText(String.valueOf(++countOfProductItem));
            } else {
                binding.countTxt.setText(String.valueOf(max));
            }
        });

        binding.countMinus.setOnClickListener(v -> {
            int min = flowerModel.getMinimumPurchaseCount();
            int max = flowerModel.getAvailableCount();

            if (binding.countTxt.hasFocus()) {
                binding.countTxt.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.countTxt.getWindowToken(), 0);
            }

            countOfProductItem = Integer.parseInt(String.valueOf(binding.countTxt.getText()));
            System.out.println(countOfProductItem);
            if (countOfProductItem > max) {
                binding.countTxt.setText(String.valueOf(max));
            } else if (countOfProductItem - 1 > min) {
                binding.countTxt.setText(String.valueOf(--countOfProductItem));
            } else {
                binding.countTxt.setText(String.valueOf(min));
            }
        });
    }
    private void initGlobalFields() {
        flowerModel = (FlowerModel) getIntent().getSerializableExtra("object");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        boolean sellerIsCurrent = flowerModel.getSellerId().equals(currentUser.getUid());

        if (sellerIsCurrent) {
            binding.addToCardBtn.setVisibility(View.GONE);
            binding.view2.setVisibility(View.GONE);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.scrollView.getLayoutParams();
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.actionBarSize);
            layoutParams.bottomMargin = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
            binding.scrollView.setLayoutParams(layoutParams);
        }
        mapFragment.getMapAsync(this);
    }
    private void addToCart(CartModel cartModel) {
        int min = cartModel.getProductModel().getMinimumPurchaseCount();
        int max = cartModel.getProductModel().getAvailableCount();

        if (cartModel.getCount() > max) {
            cartModel.setCount(max);
            Toast.makeText(this, "Available count is " + max, Toast.LENGTH_SHORT).show();
        } else if (cartModel.getCount() < min) {
            cartModel.setCount(min);
            Toast.makeText(this, "Minimum purchase count is " + min, Toast.LENGTH_SHORT).show();
        }

        db.collection("CartItems")
                .add(cartModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Item added to cart successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("AddToCart", Arrays.toString(e.getStackTrace()));
                        }
                    }
                });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        LatLng currentLocation = new LatLng(flowerModel.getLatitude(), flowerModel.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        map.addMarker(new MarkerOptions().position(currentLocation));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, MAP_ZOOM));
    }
}

