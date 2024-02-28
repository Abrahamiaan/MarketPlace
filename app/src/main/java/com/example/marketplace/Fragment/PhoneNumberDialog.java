package com.example.marketplace.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.marketplace.R;

public class PhoneNumberDialog extends Dialog {

    private EditText otpET1, otpET2, otpET3, otpET4;
    private TextView resendBtn;
    private Button verifyBtn;
    boolean resendEnabled;
    int resendTime = 60;
    int selectedEdTxtPosition = 0;
    Activity activity;

    public PhoneNumberDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) ;
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        setContentView(R.layout.add_phone_dialog);

        initGlobalFields();
        initListeners();
    }

    private void initGlobalFields() {
        otpET1 = findViewById(R.id.otp1);
        otpET2 = findViewById(R.id.otp2);
        otpET3 = findViewById(R.id.otp3);
        otpET4 = findViewById(R.id.otp4);

        resendBtn = findViewById(R.id.resendBtn);
        verifyBtn = findViewById(R.id.verifyBtn);

        showKeyboard(otpET1);
        startCountDownTimer();
    }

    private void initListeners() {
        otpET1.addTextChangedListener(textWatcher);
        otpET2.addTextChangedListener(textWatcher);
        otpET3.addTextChangedListener(textWatcher);
        otpET4.addTextChangedListener(textWatcher);

        resendBtn.setOnClickListener(v -> {
            if (resendEnabled) {
                startCountDownTimer();
            }
        });

        verifyBtn.setOnClickListener(v -> {
            final String getOTP = otpET1.getText().toString() +
                            otpET2.getText().toString() +
                            otpET3.getText().toString() +
                            otpET4.getText().toString();

            if (getOTP.length() == 4) {
                sendVerificationCode();
            }
        });

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                if (selectedEdTxtPosition == 0) {
                    selectedEdTxtPosition = 1;
                    showKeyboard(otpET2);
                } else if (selectedEdTxtPosition == 1) {
                    selectedEdTxtPosition = 2;
                    showKeyboard(otpET3);
                } else if (selectedEdTxtPosition == 2) {
                    selectedEdTxtPosition = 3;
                    showKeyboard(otpET4);
                }
            }
        }
    };

    private void showKeyboard(EditText otpET) {
        otpET.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountDownTimer() {
        resendEnabled = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime * 1000L, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText(R.string.resend + "(" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendBtn.setText(R.string.resend);
                resendBtn.setTextColor (getContext().getResources().getColor(android.R.color.holo_blue_dark));
            }
        }.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            if(selectedEdTxtPosition == 3){
                selectedEdTxtPosition = 2;
                showKeyboard(otpET3);
            } else if(selectedEdTxtPosition == 2) {
                selectedEdTxtPosition = 1;
                showKeyboard(otpET2);
            } else if(selectedEdTxtPosition == 1) {
                selectedEdTxtPosition = 0;
                showKeyboard(otpET1);
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    private void sendVerificationCode() {
        //
    }

}
