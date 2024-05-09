package com.example.marketplace.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.marketplace.Activity.SplashActivity;
import com.example.marketplace.Admin.ConfirmationActivity;
import com.example.marketplace.Admin.OrdersActivity;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import ir.androidexception.andexalertdialog.AndExAlertDialog;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    boolean isAdmin;


    public ProfileFragment() { }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();

        return binding.getRoot();
    }

    public void showConfirmDialog(String title, String message) {
        new AndExAlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveBtnText(getString(R.string.log_out))
                .setNegativeBtnText(getString(R.string.cancel))
                .setCancelableOnTouchOutside(false)
                .OnPositiveClicked(v -> {
                    if (mAuth.getCurrentUser() != null) {
                        mAuth.signOut();
                        Intent intent = new Intent(requireContext(), SplashActivity.class);
                        startActivity(intent);
                    }
                })
                .OnNegativeClicked(v -> {})
                .build();
    }
    private void initGlobalFields() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            String currentUserName = currentUser.getDisplayName();
            Uri photoUrl = currentUser.getPhotoUrl();

            binding.tvName.setText(currentUserName);

            if (photoUrl != null) {
                System.out.println(photoUrl);
                Glide.with(this)
                        .load(photoUrl)
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

            fetchUserMetaData();
        }
    }
    private void initListeners() {
        binding.linearOut.setOnClickListener(v -> showConfirmDialog(getString(R.string.oops), getString(R.string.are_you_sure_you_want_to_log_out)));
        binding.linearLanguage.setOnClickListener(v -> {
            LanguageFragment languageFragment = new LanguageFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_layout, languageFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });
        binding.constraintEditAccount.setOnClickListener(v -> {
            EditAccountFragment fragment = new EditAccountFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_layout, fragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });
        binding.linearAds.setOnClickListener(v -> {
            AdsFragment fragment = new AdsFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_layout, fragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });
        binding.linearContacts.setOnClickListener(v -> {
            ContactInfoFragment fragment = new ContactInfoFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_layout, fragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });
        binding.switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = requireActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE).edit();
            editor.putBoolean("notification_enabled", isChecked);
            editor.apply();
        });
        binding.linearConfirmProducts.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ConfirmationActivity.class);
            startActivity(intent);
        });
        binding.linearOrders.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), OrdersActivity.class);
            startActivity(intent);
        });
    }
    private void fetchUserMetaData() {
        db.collection("UserMetaData")
                .document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            isAdmin = task.getResult().getBoolean("isAdmin");
                            binding.adminParent.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
                        }
                    }
                })
                .addOnFailureListener(aVoid -> {
                    isAdmin = false;
                    binding.adminParent.setVisibility(View.GONE);
                });
    }
}