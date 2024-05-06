package com.example.marketplace.Fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.marketplace.Model.CartModel;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentConfirmOrderBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfirmOrderFragment extends Fragment {
    FragmentConfirmOrderBinding binding;
    private List<CartModel> cartItems;

    double latitude = -91;
    double longitude = 181;


    public ConfirmOrderFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConfirmOrderBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent data = requireActivity().getIntent();
        latitude = data.getDoubleExtra("latitude", -91);
        longitude = data.getDoubleExtra("longitude", 181);
        try {
            if (latitude != -91 && longitude != 181) {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                binding.selectLocation.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGlobalFields() {
        MeowBottomNavigation navigationBar = getActivity().findViewById(R.id.navigationBar);
        if (navigationBar != null) {
            navigationBar.setVisibility(View.GONE);
        }

        Bundle args = getArguments();
        if (args != null) {
            cartItems = new ArrayList<>();
            int i = 0;
            while (args.containsKey("cart-items" + i)) {
                CartModel item = (CartModel) args.getSerializable("cart-items" + i);
                cartItems.add(item);
                i++;
            }
        }

        updateTotalSum();
    }

    private void initListeners() {
        binding.toBack.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.selectLocation.setOnClickListener(v -> {
            MapFragment mapFragment = new MapFragment();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.replace(R.id.frame_layout, mapFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });
    }
    public void updateTotalSum() {
        int subTotalSum = 0;
        int deliveryPrice = Integer.parseInt(getArguments().getString("deliveryPrice"));
        for (int i = 0; i < cartItems.size(); i++) {
            subTotalSum += cartItems.get(i).getCount() * cartItems.get(i).getProductModel().getPrice();
        }
        binding.subTotal.setText(String.valueOf(subTotalSum));
        binding.deliveryCharge.setText(String.valueOf(deliveryPrice));
        binding.total.setText(String.valueOf(subTotalSum + deliveryPrice));
    }
}