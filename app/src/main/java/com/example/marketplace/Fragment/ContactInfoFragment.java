package com.example.marketplace.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentContactInfoBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ContactInfoFragment extends Fragment {
    FragmentContactInfoBinding binding;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactInfoBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void addNewPhoneNumberCard() {
        MaterialCardView cardView = new MaterialCardView(requireContext());
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setCardElevation(getResources().getDimension(com.intuit.sdp.R.dimen._2sdp));
        cardView.setClickable(false);
        cardView.setFocusable(true);
        cardView.setCheckable(false);
        cardView.setChecked(false);
        cardView.setUseCompatPadding(true);
        cardView.setPreventCornerOverlap(false);
        cardView.setStrokeWidth(0);
        cardView.setClipToPadding(true);

        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);
        linearLayout.setPadding(getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp));

        ImageView imageView = new ImageView(requireContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp),
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)
        ));

        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp),
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._30sdp)
        );
        imageParams.setMargins(
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                0,
                getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp),
                0
        );
        imageView.setLayoutParams(imageParams);

        imageView.setImageResource(R.drawable.vector_phone);

        TextView textView = new TextView(requireContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        textView.setText("123456789");
        textView.setTextColor(Color.parseColor("#2962FF"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        linearLayout.addView(imageView);
        linearLayout.addView(textView);

        cardView.addView(linearLayout);

        binding.parentLayout.addView(cardView, 0);
    }
    private void initGlobalFields() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        binding.emailAddress.setText(currentUser.getEmail());
    }
    private void initListeners() {
        binding.addPhoneBtn.setOnClickListener(v -> addNewPhoneNumberCard());
        binding.toBack.setOnClickListener(v -> getActivity().onBackPressed());
    }
}