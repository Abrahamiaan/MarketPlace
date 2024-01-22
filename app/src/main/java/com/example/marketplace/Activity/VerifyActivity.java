package com.example.marketplace.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplace.Utils.MailHelper;
import com.example.marketplace.databinding.ActivityVerifyBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class VerifyActivity extends AppCompatActivity {
    ActivityVerifyBinding binding;
    EditText otp1;
    EditText otp2;
    EditText otp3;
    EditText otp4;
    String userEmail;
    FirebaseAuth mAuth;
    private int otpNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        otp1 = binding.otp1;
        otp2 = binding.otp2;
        otp3 = binding.otp3;
        otp4 = binding.otp4;

        mAuth = FirebaseAuth.getInstance();
        userEmail = mAuth.getCurrentUser().getEmail();

        Random random = new Random();
        otpNum = random.nextInt(8999) + 1000;

        try {
            MailHelper.sendEmail(userEmail, String.valueOf(otpNum));
        }
        catch (Exception ex) {
            System.out.println("Error: Send Email Failed!!!");
            ex.printStackTrace();
        }

        String verifyText = binding.verifyEmail.getText() + userEmail;
        binding.verifyEmail.setText(verifyText);

        binding.verifyBtn.setOnClickListener(v -> {
            if (verifyOTP(otpNum)) {
                Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(this, "Verify Email Failed!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.resend.setOnClickListener(v -> {
            otpNum = random.nextInt(8999) + 1000;

            try {
                MailHelper.sendEmail(userEmail, String.valueOf(otpNum));
            }
            catch (Exception ex) {
                System.out.println("Error: Send Email Failed!!!");
            }
        });

        binding.toBack.setOnClickListener(v -> onBackPressed());

        setOtpTextWatcher(otp1, otp2);
        setOtpTextWatcher(otp2, otp3);
        setOtpTextWatcher(otp3, otp4);
        setOtpTextWatcher(otp4, null);
    }
    private void setOtpTextWatcher(final EditText currentEditText, final View nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count == 1 && nextEditText != null) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private boolean verifyOTP(int number) {
        String buffer = "";

        buffer += otp1.getText().toString() +
                otp2.getText().toString() +
                otp3.getText().toString() +
                otp4.getText().toString();

        String num = String.valueOf(number);

        return num.equals(buffer);
    }
}