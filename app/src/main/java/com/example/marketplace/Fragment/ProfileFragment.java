package com.example.marketplace.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marketplace.Activity.SplashActivity;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;


import ir.androidexception.andexalertdialog.AndExAlertDialog;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    FirebaseAuth mAuth;

    public ProfileFragment() { }
    @Override
    public View onCreateView(LayoutInflater inflater,
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
                .OnNegativeClicked(v -> {

                })
                .build();
    }
    private void initListeners() {
        mAuth = FirebaseAuth.getInstance();
        String currentUserName = mAuth.getCurrentUser().getDisplayName();

        binding.tvName.setText(currentUserName);
    }
    private void initGlobalFields() {
        binding.linearOut.setOnClickListener(v -> {
            showConfirmDialog(getString(R.string.oops), getString(R.string.are_you_sure_you_want_to_log_out));
        });
        binding.linearLanguage.setOnClickListener(v -> {
            LanguageFragment languageFragment = new LanguageFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_layout, languageFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });

        binding.linearFavorites.setOnClickListener(v -> {
            FavoriteFragment languageFragment = new FavoriteFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_layout, languageFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });
    }
}