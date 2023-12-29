package com.example.marketplace.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.marketplace.Model.ProductModel;
import com.example.marketplace.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class SellingFragment extends Fragment {
    final int PICK_IMAGE_REQUEST = 1;
    static final String[] categories = new String[]{"Rose", "Lily", "Լօtus", "Jasmine"};
    final String PRODUCT_TABLE = "Products";
    TextInputEditText Category_view;
    TextInputEditText productName;
    TextInputEditText productDetails;
    TextInputEditText productPrice;
    ImageView productPhoto;
    ImageView btnDone;
    ProductModel productModel;
    FirebaseFirestore db;
    Uri imagePath;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    int selectedCategory = -1;

    public SellingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selling, container, false);
        db = FirebaseFirestore.getInstance();
        imagePath = null;
        productModel = new ProductModel();
        productModel.setProductId(db.collection("Products").document().getId());

        btnDone = view.findViewById(R.id.btn_done);
        productPrice = view.findViewById(R.id.product_price);
        productDetails = view.findViewById(R.id.product_details);
        productName = view.findViewById(R.id.product_name);
        productPhoto = view.findViewById(R.id.product_photo);

        progressDialog = new ProgressDialog(requireContext());
        Category_view = view.findViewById(R.id.Category_view);

        Category_view.setOnClickListener(v1 -> selectCategory());

        productPhoto.setOnClickListener(v2 -> chooseImage());

        btnDone.setOnClickListener(v3 -> submitProduct());

        return view;
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
            Category_view.setText(categories[i]);
            selectedCategory = i;
        });

        builder.setNegativeButton("OK", null);
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imagePath);
                productPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadToFirestore() {
        db.collection("Products").document(productModel.getProductId()).set(productModel)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.hide();
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.hide();
                    Toast.makeText(requireContext(), "Failed to upload product: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void submitProduct() {
        productModel.setTitle(productName.getText().toString());
        if (productModel.getTitle().isEmpty()) {
            Toast.makeText(requireContext(), "Product name can't be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        productModel.setCategory(Category_view.getText().toString());
        if (productModel.getCategory().isEmpty()) {
            Toast.makeText(requireContext(), "You must select product category.", Toast.LENGTH_SHORT).show();
            selectCategory();
            return;
        }

        if (productPrice.getText().toString().isEmpty()) {
            Toast.makeText(requireContext(), "Product price can't be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            productModel.setPrice(Integer.parseInt(productPrice.getText().toString()));
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Invalid price format.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productModel.getPrice() < 0) {
            Toast.makeText(requireContext(), "Product price can't be less than zero.", Toast.LENGTH_SHORT).show();
            return;
        }

        productModel.setDetails(productDetails.getText().toString());

        if (imagePath == null) {
            Toast.makeText(requireContext(), "You must select product photo.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child("products/" + productModel.getProductId()).putFile(imagePath)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(requireContext(), "Uploaded Successfully!", Toast.LENGTH_SHORT).show();

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
                    progressDialog.hide();
                    Toast.makeText(requireContext(), "Failed to upload photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
