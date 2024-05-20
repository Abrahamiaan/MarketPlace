package com.example.marketplace.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.marketplace.R;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;
import ir.androidexception.andexalertdialog.AndExAlertDialog;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    static final int RC_SIGN_IN = 9001;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
        initListeners();
    }

    private void signIn() {
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        boolean isValid = true;

        if (password.isEmpty()) {
            binding.textInputPassword.setError("Password cannot be empty");
            isValid = false;
        }

        if (email.isEmpty()) {
            binding.textInputEmail.setError("Email cannot be empty");
            isValid= false;
        }

        if (!isValid)
            return;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        binding.progressBar3.setVisibility(View.GONE);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("Login: ", "Sign In With Email Failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            binding.textInputPassword.setError(getString(R.string.email_or_password_is_incorrect));
                            binding.textInputEmail.setError(getString(R.string.email_or_password_is_incorrect));
                        } else {
                            showErrorDialog("Authentication failed", "Users in this date range don't exist");
                        }
                    }
                });
    }

    private void continueAsGuest() {
        binding.progressBar3.setVisibility(View.VISIBLE);
        String email = "sictst1@gmail.com";
        String password = "Samsung2023";
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        binding.progressBar3.setVisibility(View.GONE);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GOOGLE: ", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("GOOGLE", "Google sign in failed", e);
                binding.parentLinear.setVisibility(View.VISIBLE);
                binding.progressBar3.setVisibility(View.GONE);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w("GOOGLE: ", "signInWithCredential:failure", task.getException());
                    }
                });
    }
    public void singInWithGoogle() {
        binding.progressBar3.setVisibility(View.VISIBLE);
        binding.parentLinear.setVisibility(View.GONE);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }
    public void showErrorDialog(String title, String message) {
        new AndExAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveBtnText("OK")
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(input -> {})
                .build();
    }
    private void initListeners() {
        binding.signInBtn.setOnClickListener(v -> signIn());
        binding.toRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        binding.withGoogle.setOnClickListener(v-> singInWithGoogle());
        binding.toForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        binding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textInputEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(binding.email.getText())) {
                    binding.textInputEmail.setError("Email cannot be empty");
                }
            }
        });
        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textInputPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(binding.password.getText())) {
                    binding.textInputPassword.setError("Password cannot be empty");
                }
            }
        });

        binding.withFb.setOnClickListener(v -> continueAsGuest());
    }
    private void initGlobalFields() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
}
