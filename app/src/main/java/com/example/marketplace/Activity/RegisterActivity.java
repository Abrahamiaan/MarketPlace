package com.example.marketplace.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.marketplace.R;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.databinding.ActivityRegisterBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ir.androidexception.andexalertdialog.AndExAlertDialog;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setupListeners();
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
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w("GOOGLE: ", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    public void SingInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            firebaseUser.sendEmailVerification()
                                    .addOnCompleteListener(emailVerificationTask -> {
                                        if (emailVerificationTask.isSuccessful()) {
                                            String username = binding.name.getText().toString();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(username)
                                                    .build();

                                            firebaseUser.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(updateProfileTask -> {
                                                        if (updateProfileTask.isSuccessful()) {
                                                            saveUserDataInFirestore(firebaseUser);
                                                            Log.d("Sign Up: ",  "Register Successfully");
                                                        } else {
                                                            Exception updateProfileException = updateProfileTask.getException();
                                                            Log.e("Update Profile Failed", Objects.requireNonNull(updateProfileException).getMessage());
                                                        }
                                                    });
                                        } else {
                                            Exception emailVerificationException = emailVerificationTask.getException();
                                            Log.e("Email Verification Failed", Objects.requireNonNull(emailVerificationException).getMessage());
                                        }
                                    });
                        }
                    } else {
                        Exception exception = task.getException();
                        showErrorDialog("Register Failed", Objects.requireNonNull(exception).getMessage());
                        Log.e("Register Failed", exception.getMessage());
                    }
                });
    }

    private void saveUserDataInFirestore(FirebaseUser user) {
        String userId = user.getUid();
        String displayName = user.getDisplayName();
        String email = user.getEmail();

        DocumentReference userRef = db.collection("Users").document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("displayName", displayName);
        userData.put("email", email);

        userRef.set(userData)
                .addOnSuccessListener(aVoid -> Log.d("Google Firestore: ", "User data successfully written to Firestore"))
                .addOnFailureListener(e -> Log.w("Google Firestore:" , "Error writing user data to Firestore", e));
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateInput() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString();
        String username = binding.name.getText().toString();
        String confirm = binding.confirm.getText().toString();
        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            binding.textInputEmail.setError(getString(R.string.email_cannot_be_empty));
            isValid = false;
        }  else {
            binding.toLogin.setError(null);
        }

        if (TextUtils.isEmpty(username)) {
            binding.textInputName.setError(getString(R.string.name_cannot_be_empty));
            isValid = false;
        } else {
            binding.name.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.textInputPassword.setError(getString(R.string.password_cannot_be_empty));
            isValid = false;
        } else if (password.length() < 8) {
            binding.textInputPassword.setError(getString(R.string.password_must_be_at_least_8_characters_long));
            isValid = false;
        } else {
            binding.password.setError(null);
        }

        if (!password.equals(confirm)) {
            binding.textInputConfirm.setError(getString(R.string.passwords_do_not_match));
            isValid = false;
        } else {
            binding.confirm.setError(null);
        }

        return isValid;
    }

    public void showErrorDialog(String title, String message) {
        new AndExAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveBtnText("OK")
                .setCancelableOnTouchOutside(true)
                .OnPositiveClicked(input -> {

                })
                .build();
    }

    public void setupListeners() {
        binding.withGoogle.setOnClickListener(v -> SingInWithGoogle());

        binding.signUpBtn.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString();
            if (validateInput()) {
                signUp(email, password);
            }
        });

        binding.toLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
                    binding.textInputEmail.setError(getString(R.string.email_cannot_be_empty));
                }
            }
        });
        binding.name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textInputName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(binding.name.getText())) {
                    binding.textInputName.setError(getString(R.string.name_cannot_be_empty));
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
                String password = binding.password.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    binding.textInputPassword.setError(getString(R.string.password_cannot_be_empty));
                }
            }
        });
    }
}
