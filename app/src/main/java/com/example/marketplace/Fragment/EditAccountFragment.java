package com.example.marketplace.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentEditAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditAccountFragment extends Fragment {
    private ActivityResultLauncher<Intent> imageSelectionLauncher;
    FragmentEditAccountBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    Uri onStartPhotoUri;
    Uri selectedImageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();
        

        return binding.getRoot();
    }

    private void initGlobalFields() {
        setupImageSelection();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        onStartPhotoUri = currentUser.getPhotoUrl();
        fetchAddress();

        binding.userName.setText(currentUser.getDisplayName());
        Glide.with(this)
                .load(onStartPhotoUri)
                .transform(new CircleCrop())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.profilePhoto.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }
    private void initListeners() {
        binding.toBack.setOnClickListener(v -> requireActivity().onBackPressed());
        binding.saveBtn.setOnClickListener(v -> {
            checkChangesAndSave();
            requireActivity().onBackPressed();
        });
        binding.sendResetPassword.setOnClickListener(v -> sendPasswordResetEmail());
        binding.mainLinearProfessional.setOnClickListener(v -> {
            binding.professionalDone.setVisibility(View.VISIBLE);
            binding.individualDone.setVisibility(View.GONE);
        });
        binding.mainLinearIndividual.setOnClickListener(v -> {
            binding.professionalDone.setVisibility(View.GONE);
            binding.individualDone.setVisibility(View.VISIBLE);
        });
        binding.uploadProfilePhoto.setOnClickListener(v -> selectImage());
    }
    private void setupImageSelection() {
        imageSelectionLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            requireActivity();
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this)
                            .load(selectedImageUri)
                            .transform(new CircleCrop())
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    binding.profilePhoto.setBackground(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                            });
                }
            }
        });
    }
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageSelectionLauncher.launch(intent);
    }
    private void checkChangesAndSave() {
        checkAndSaveChanges();
        boolean areNameChanges = checkNameChanges();
        if (areNameChanges) {
            changeDisplayName();
        }

        if (selectedImageUri != null) {
            updateProfilePhoto(selectedImageUri);
        }
    }
    private void changeDisplayName() {
        String username = binding.userName.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(updateProfileTask -> {
                    if (updateProfileTask.isSuccessful()) {
                        Log.d("Edit Account: ", "Update Profile Successfully");
                    } else {
                        Exception updateProfileException = updateProfileTask.getException();
                        Log.e("Update Profile Failed", Objects.requireNonNull(updateProfileException).getMessage());
                    }
                });
    }
    private void updateProfilePhoto(Uri photoUri) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(updateProfileTask -> {
                    if (updateProfileTask.isSuccessful()) {
                        Log.d("Edit Account: ", "Update Profile Photo Successfully");
                    } else {
                        Exception updateProfileException = updateProfileTask.getException();
                        Log.e("Update Profile Photo Failed", Objects.requireNonNull(updateProfileException).getMessage());
                    }
                });
    }
    private void sendPasswordResetEmail() {
        mAuth.sendPasswordResetEmail(currentUser.getEmail())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireActivity(), R.string.password_reset_email_sent_successfully, Toast.LENGTH_SHORT).show();
                        Log.d("Edit Account: ", "Password Reset Email Sent Successfully");
                    } else {
                        Log.e("Edit Account: ", "Error Sending Password Reset Email", task.getException());
                    }
                });
    }
    private void checkAndSaveChanges() {
        DocumentReference documentReference = db.collection("DeliveryAddresses").document(currentUser.getUid());
        String newAddress = binding.deliveryAddress.getText().toString();

        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String storedAddress = document.getString("address");
                    if (storedAddress != null && !storedAddress.equals(newAddress)) {
                        updateAddressInFirebase(newAddress);
                    }
                } else {
                    updateAddressInFirebase(newAddress);
                }
            } else {
                Exception e = task.getException();
                Log.e("Edit Account: ", Arrays.toString(e.getStackTrace()));
            }
        });
    }

    private void fetchAddress() {
        DocumentReference docRef = db.collection("DeliveryAddresses").document(currentUser.getUid());

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    binding.deliveryAddress.setText(document.getString("address"));
                }
            } else {
                Exception e = task.getException();
                Log.e("Edit Account: ", Arrays.toString(e.getStackTrace()));
            }
        });
    }

    private void updateAddressInFirebase(String newAddress) {
    DocumentReference address = db.collection("DeliveryAddresses").document(currentUser.getUid());

    Map<String, Object> addressData = new HashMap<>();
    addressData.put("address", newAddress);
    System.out.println(newAddress);

    address.set(addressData, SetOptions.merge())
            .addOnSuccessListener(aVoid -> Log.d("Edit Account", "Address updated successfully"))
            .addOnFailureListener(e -> {
                Log.e("Edit Account", "Error updating address", e);
                if (e instanceof FirebaseFirestoreException && ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                    address.set(addressData)
                            .addOnSuccessListener(aVoid -> Log.d("Edit Account", "Address added successfully"))
                            .addOnFailureListener(ex -> Log.e("Edit Account", "Error adding address", ex));
                }
            });
}

    private boolean checkNameChanges() {
        return !(binding.userName.getText().toString().equals(currentUser.getDisplayName()));
    }
}