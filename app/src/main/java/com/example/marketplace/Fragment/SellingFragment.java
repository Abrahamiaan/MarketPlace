package com.example.marketplace.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.civitasv.ioslike.dialog.DialogHud;
import com.example.marketplace.Adapter.ColorSizeAdapter;
import com.example.marketplace.Model.ProductModel;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentSellingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class SellingFragment extends Fragment {
    FragmentSellingBinding binding;
    final int PICK_IMAGE_REQUEST = 1;
    static final String[] categories = new String[]{"Rose", "Tulip", "Lily", "Sunflower", "Plants"};
    ProductModel productModel;
    FirebaseFirestore db;
    Uri imagePath;
    StorageReference storageReference;
    int selectedCategory = -1;
    ColorSizeAdapter colorAdapter;
    List<EditText> editTextList = new ArrayList<>();
    DialogHud dialogHud;

    public SellingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSellingBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();
        imagePath = null;
        productModel = new ProductModel();
        productModel.setProductId(db.collection("Products").document().getId());

        binding.CategoryView.setOnClickListener(v1 -> selectCategory());
        binding.productPhoto.setOnClickListener(v2 -> chooseImage());
        binding.btnDone.setOnClickListener(v3 -> submitProduct());
        binding.moreColors.setOnClickListener(v4 -> {
            if (binding.moreColorDetails.getVisibility() == View.VISIBLE) {
                binding.moreColorDetails.setVisibility(View.GONE);
            } else {
                binding.moreColorDetails.setVisibility(View.VISIBLE);
            }
        });

        List<EditText> colorItems = new ArrayList<>();
        binding.colorRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        colorAdapter = new ColorSizeAdapter(colorItems);
        binding.colorRecyclerView.setAdapter(colorAdapter);

        binding.addItem.setOnClickListener(v -> {
            EditText newEditText = new EditText(requireContext());
            colorAdapter.addItem(newEditText);
            editTextList.add(newEditText);
        });


        return binding.getRoot();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getActivity();
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imagePath);
                binding.productPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadToFirestore() {
        db.collection("Products").document(productModel.getProductId()).set(productModel)
                .addOnSuccessListener(aVoid -> {
                    dialogHud.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), getString(R.string.failed_to_upload_product) + e.getMessage(), Toast.LENGTH_LONG).show();
                    dialogHud.dismiss();
                });
    }

    private void submitProduct() {
        productModel.setTitle(binding.productName.getText().toString());

        dialogHud = new DialogHud(requireContext())
                .setMode(DialogHud.Mode.LOADING)
                .setLabel(R.string.uploading)
                .setLabelDetail(R.string.please_wait)
                .setCancelable(false);

        if (productModel.getTitle().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.product_name_can_t_be_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        productModel.setCategory(binding.CategoryView.getText().toString());
        if (productModel.getCategory().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.you_must_select_product_category), Toast.LENGTH_SHORT).show();
            selectCategory();
            return;
        }

        if (binding.productPrice.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), R.string.product_price_can_t_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            productModel.setPrice(Integer.parseInt(binding.productPrice.getText().toString()));
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.invalid_price_format, Toast.LENGTH_SHORT).show();
            return;
        }

        if (productModel.getPrice() < 0) {
            Toast.makeText(requireContext(), R.string.product_price_can_t_be_less_than_zero, Toast.LENGTH_SHORT).show();
            return;
        }

        productModel.setDetails(binding.productDetails.getText().toString());

        if (imagePath == null) {
            Toast.makeText(requireContext(), R.string.you_must_select_product_photo, Toast.LENGTH_SHORT).show();
            return;
        }

        productModel.setColors(colorAdapter.getColorItems());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser =  mAuth.getCurrentUser();
        if (currentUser != null) {
            productModel.setSeller(currentUser.getDisplayName());
            productModel.setSellerId(currentUser.getUid());
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        dialogHud.show();

        storageReference.child("products/" + productModel.getProductId()).putFile(imagePath)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.child("products/" + productModel.getProductId()).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                productModel.setPhoto(uri.toString());
                                uploadToFirestore();
                            })
                            .addOnFailureListener(e -> {
                                productModel.setPhoto("https://images.unsplash.com/photo-1491553895911-0055eca6402d?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=2000&q=80");
                                uploadToFirestore();
                            });
                })
                .addOnFailureListener(e -> {
                    dialogHud.dismiss();
                    Toast.makeText(requireContext(), getString(R.string.failed_to_upload_photo) + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
