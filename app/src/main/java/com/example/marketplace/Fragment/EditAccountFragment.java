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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class EditAccountFragment extends Fragment {
    private ActivityResultLauncher<Intent> imageSelectionLauncher;
    FragmentEditAccountBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Uri onStartPhotoUri;
    Uri selectedImageUri;
    StorageReference storageReference;

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
        onStartPhotoUri = currentUser.getPhotoUrl();

        storageReference = FirebaseStorage.getInstance().getReference();

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
        binding.saveBtn.setOnClickListener(v -> checkChangesAndSave());
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
        binding.progressBar2.setVisibility(View.VISIBLE);
        boolean areNameChanges = checkNameChanges();
        if (areNameChanges) {
            changeDisplayName();
        }

        if (selectedImageUri != null) {
            uploadToStorage();
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

    private void uploadToStorage() {
        String uuid = currentUser.getUid();
        storageReference.child("userProfileImg/" + uuid).putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.child("userProfileImg/" + uuid).getDownloadUrl()
                        .addOnSuccessListener( downloadUri->   {
                            Log.d("Driver Photo", "Successfully added,");
                            updateProfilePhoto(downloadUri);
                            binding.progressBar2.setVisibility(View.GONE);
                            requireActivity().onBackPressed();
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), getString(R.string.failed_to_upload_photo) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar2.setVisibility(View.GONE);
                    requireActivity().onBackPressed();
                });
    }

    private boolean checkNameChanges() {
        return !(binding.userName.getText().toString().equals(currentUser.getDisplayName()));
    }
}