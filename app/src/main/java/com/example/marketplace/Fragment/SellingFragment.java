package com.example.marketplace.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.civitasv.ioslike.dialog.DialogHud;
import com.example.marketplace.Adapter.ColorSizeAdapter;
import com.example.marketplace.Model.FlowerModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.Constants;
import com.example.marketplace.databinding.FragmentSellingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class SellingFragment extends Fragment {
    FragmentSellingBinding binding;
    final int PICK_IMAGE_REQUEST = 1;
    static final String[] categories = Constants.categories;
    FlowerModel productModel;
    FirebaseFirestore db;
    Uri imagePath;
    StorageReference storageReference;
    int selectedCategory = -1;
    ColorSizeAdapter colorAdapter;
    List<EditText> editTextList = new ArrayList<>();
    DialogHud dialogHud;
    double latitude = -91;
    double longitude = 181;
    Bitmap bitmap;

    public SellingFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSellingBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();

        return binding.getRoot();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getActivity();
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagePath = data.getData();
            requireActivity().getIntent().putExtra("imagePath", imagePath.toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imagePath);
                binding.productPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent data = requireActivity().getIntent();
        latitude = data.getDoubleExtra("latitude", -91);
        longitude = data.getDoubleExtra("longitude", 181);

        if (bitmap != null) {
            binding.productPhoto.setImageBitmap(bitmap);
        }

        try {
            if (latitude != -91 && longitude != 181) {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                binding.selectLocation.setText(address);
                productModel.setLatitude(latitude);
                productModel.setLongitude(longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        imagePath = null;
        productModel = new FlowerModel();
        productModel.setProductId(db.collection("Products").document().getId());
        List<EditText> colorItems = new ArrayList<>();
        binding.colorRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        colorAdapter = new ColorSizeAdapter(colorItems);
        binding.colorRecyclerView.setAdapter(colorAdapter);
    }
    private void initListeners() {
        binding.CategoryView.setOnClickListener(v -> selectCategory());
        binding.productPhoto.setOnClickListener(v -> chooseImage());
        binding.selectLocation.setOnClickListener(v -> {
            MapFragment mapFragment = new MapFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_layout, mapFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });
        binding.btnDone.setOnClickListener(v -> submitProduct());
        binding.moreColors.setOnClickListener(v -> {
            if (binding.moreColorDetails.getVisibility() == View.VISIBLE) {
                binding.moreColorDetails.setVisibility(View.GONE);
            } else {
                binding.moreColorDetails.setVisibility(View.VISIBLE);
            }
        });
        binding.addItem.setOnClickListener(v -> {
            EditText newEditText = new EditText(requireContext());
            colorAdapter.addItem(newEditText);
            editTextList.add(newEditText);
        });
    }
    private void uploadToFirestore() {
        db.collection("Products").document(productModel.getProductId()).set(productModel)
                .addOnSuccessListener(aVoid -> dialogHud.dismiss())
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), getString(R.string.failed_to_upload_product) + e.getMessage(), Toast.LENGTH_LONG).show();
                    dialogHud.dismiss();
                });
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select product image"), PICK_IMAGE_REQUEST);
    }
    private void selectCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select category");
        builder.setSingleChoiceItems(categories, selectedCategory, (dialogInterface, i) -> {
            binding.CategoryView.setText(categories[i]);
            selectedCategory = i;
        });

        builder.setNegativeButton("OK", null);
        builder.show();
    }
    private void submitProduct() {
        String productName = binding.productName.getText().toString();
        String category = binding.CategoryView.getText().toString();
        String priceText = binding.productPrice.getText().toString();
        String details = binding.productDetails.getText().toString();
        String minPurchaseCount = binding.minPurchaseCount.getText().toString();
        String availableCount = binding.availableCount.getText().toString();

        dialogHud = new DialogHud(requireContext())
                .setMode(DialogHud.Mode.LOADING)
                .setLabel(R.string.uploading)
                .setLabelDetail(R.string.please_wait)
                .setCancelable(false);


        if (productName.isEmpty()) {
            showToast(getString(R.string.product_name_can_t_be_empty));
            return;
        }

        if (category.isEmpty()) {
            showToast(getString(R.string.you_must_select_product_category));
            selectCategory();
            return;
        }

        if (priceText.isEmpty()) {
            showToast(getString(R.string.product_price_can_t_be_empty));
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceText);
            if (price < 0) {
                showToast(getString(R.string.product_price_can_t_be_less_than_zero));
                return;
            }
        } catch (NumberFormatException e) {
            showToast(getString(R.string.invalid_price_format));
            return;
        }

        if (details.isEmpty()) {
            showToast(getString(R.string.you_must_enter_product_details));
            return;
        }

        String imagePathStr = requireActivity().getIntent().getStringExtra("imagePath");
        if (imagePathStr != null) {
            imagePath = Uri.parse(imagePathStr);
            if (imagePath == null) {
                showToast(getString(R.string.you_must_select_product_photo));
                return;
            }
        }

        if (latitude == -91 || longitude == 181) {
            showToast(getString(R.string.you_must_select_product_location));
            return;
        }

        if(minPurchaseCount.isEmpty() || availableCount.isEmpty()) {
            showToast("You must enter quantity");
            return;
        }

        productModel.setTitle(productName);
        productModel.setCategory(category);
        productModel.setPrice(price);
        productModel.setDetails(details);
        productModel.setAvailableCount(Integer.parseInt(availableCount));
        productModel.setMinimumPurchaseCount(Integer.parseInt(minPurchaseCount));

        productModel.setColors(colorAdapter.getColorItems());

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            productModel.setSeller(currentUser.getDisplayName());
            productModel.setSellerId(currentUser.getUid());
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        dialogHud.show();

        uploadToStorage();
    }
    private void uploadToStorage() {
        storageReference.child("products/" + productModel.getProductId()).putFile(imagePath)
                .addOnSuccessListener(taskSnapshot -> storageReference.child("products/" + productModel.getProductId()).getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            productModel.setPhoto(uri.toString());
                            uploadToFirestore();
                        }))
                .addOnFailureListener(e -> {
                    dialogHud.dismiss();
                    Toast.makeText(requireContext(), getString(R.string.failed_to_upload_photo) + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
