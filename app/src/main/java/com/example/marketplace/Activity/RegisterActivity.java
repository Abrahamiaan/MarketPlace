package com.example.marketplace.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.marketplace.Model.User;
import com.example.marketplace.R;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ir.androidexception.andexalertdialog.AndExAlertDialog;

public class RegisterActivity extends AppCompatActivity {
    static final int RC_SIGN_IN = 9001;
    EditText tvEmail;
    EditText tvUsername;
    EditText tvPassword;
    TextView tvToLogin;
    Button signUpBtn;
    ImageView btnSignUpWithGoogle;
    ImageView btnBack;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_register);

        tvToLogin = findViewById(R.id.toLogin);
        tvEmail = findViewById(R.id.email);
        tvUsername = findViewById(R.id.name);
        tvPassword = findViewById(R.id.password);
        signUpBtn = findViewById(R.id.signUpBtn);
        btnBack = findViewById(R.id.to_back_arrow);
        btnSignUpWithGoogle = findViewById(R.id.with_google);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignUpWithGoogle.setOnClickListener(v -> {
            SingInWithGoogle();
        });

        signUpBtn.setOnClickListener(v -> {
            String email = tvEmail.getText().toString();
            String password = tvPassword.getText().toString();
            if (validateInput()) {
                signUp(email, password);
            }
        });

        tvToLogin.setOnClickListener(v->{
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        saveUserDataInFirestore(user);
                    } else {
                        Log.w("GOOGLE: ", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Sign Up: " + "Register Successfully");
                        String username = tvUsername.getText().toString();
                        User user = new User(username, email);
                        saveUserInFirestore(user);
                    }
                    else {
                        Exception exception = task.getException();
                        showErrorDialog("Register Failed", Objects.requireNonNull(exception).getMessage());
                    }
                });
    }

    public void SingInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void saveUserDataInFirestore(FirebaseUser user) {
        String userId = user.getUid();
        String displayName = user.getDisplayName();
        String email = user.getEmail();

        db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(userId);

        Map<String, Object> userData = new HashMap<>();
        userData.put("displayName", displayName);
        userData.put("email", email);

        userRef.set(userData)
                .addOnSuccessListener(aVoid -> Log.d("Google Firestore: ", "User data successfully written to Firestore"))
                .addOnFailureListener(e -> Log.w("Google Firestore:" , "Error writing user data to Firestore", e));
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void saveUserInFirestore(User user) {
        try {
            db = FirebaseFirestore.getInstance();
            db.collection("Users")
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "User successfully written with ID: " + documentReference.getId());
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firestore", "Error writing document", e);
                    });
        } catch (Exception e) {
            Log.e("Save In Firestore", e.getMessage().toString());
        }
    }

    // Util Functions
    private boolean validateInput() {
        String email = tvEmail.getText().toString();
        String password = tvPassword.getText().toString();
        String username = tvUsername.getText().toString();

        ArrayList<String> errorMessages = new ArrayList<>();

        if (TextUtils.isEmpty(email)) {
            errorMessages.add("Email is required.");
        }

        if (TextUtils.isEmpty(username)) {
            errorMessages.add("Username is required.");
        }

        if (TextUtils.isEmpty(password)) {
            errorMessages.add("Password is required.");
        } else if (password.length() < 8) {
            errorMessages.add("Password must be at least 8 characters long.");
        }

        if (!errorMessages.isEmpty()) {
            String errorMessage = TextUtils.join("\n", errorMessages);
            showErrorDialog("Input Validation Failed", errorMessage);
            return false;
        }

        return true;
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
}
