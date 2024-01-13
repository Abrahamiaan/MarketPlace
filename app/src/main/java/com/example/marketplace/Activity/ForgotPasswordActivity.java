package com.example.marketplace.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.databinding.ActivityForgotPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    static final String TAG = "Forgot Password: ";
    FirebaseAuth mAuth;
    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.sendBtn.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            if (!TextUtils.isEmpty(email)) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password Reset Email Sent Successfully");
                                onBackPressed();
                            } else {
                                Log.e(TAG, "Error Sending Password Reset Email", task.getException());
                            }
                        });
            } else {
                binding.email.setError("Email can not be empty");
            }
        });

        binding.toBackArrow.setOnClickListener(v -> onBackPressed());
    }
}
