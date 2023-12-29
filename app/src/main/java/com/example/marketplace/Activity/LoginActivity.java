package com.example.marketplace.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.marketplace.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import ir.androidexception.andexalertdialog.AndExAlertDialog;


public class LoginActivity extends AppCompatActivity {
    static final int RC_SIGN_IN = 9001;
    EditText tvEmail;
    EditText tvPassword;
    Button login;
    ImageView withGoogle;
    TextView toRegister;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login);

        tvEmail = findViewById(R.id.email);
        tvPassword = findViewById(R.id.password);
        login = findViewById(R.id.signInBtn);
        withGoogle = findViewById(R.id.with_google);
        toRegister = findViewById(R.id.toRegister);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        login.setOnClickListener(v -> {
            String email = tvEmail.getText().toString();
            String password = tvPassword.getText().toString();
            signIn(email, password);
        });

        toRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
        
        withGoogle.setOnClickListener(v-> {
            SingInWithGoogle();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showErrorDialog("Login Failed", "Email or Password cannot be empty");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        Log.d("login", "signInWithEmail:success");
                    } else {
                        Log.w("login", "signInWithEmail:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            showErrorDialog("Login Failed", "Email or Password is incorrect");
                        } else {
                            showErrorDialog("Authentication failed", "Users in this date range don't exist");
                        }
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
                        System.out.println(user.getEmail());
                    } else {
                        Log.w("GOOGLE: ", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    public void SingInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
