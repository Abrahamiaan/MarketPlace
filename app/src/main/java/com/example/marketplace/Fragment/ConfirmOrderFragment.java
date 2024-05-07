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
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.marketplace.Activity.CongratulationsActivity;
import com.example.marketplace.Model.CartModel;
import com.example.marketplace.Model.OrderModel;
import com.example.marketplace.Model.ProductModel;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentConfirmOrderBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfirmOrderFragment extends Fragment {
    FragmentConfirmOrderBinding binding;
    private List<CartModel> cartItems;
    double latitude = -91;
    double longitude = 181;
    int deliveryForKm;
    private static final double EARTH_RADIUS = 6371.0;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

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
                updateTotalSum();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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
            deliveryForKm = Integer.parseInt(getArguments().getString("deliveryPrice"));
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

        binding.orderBtn.setOnClickListener(v -> {
            if (!(latitude == -91 || longitude == 181)) {
                createOrders();
            } else {
                Toast.makeText(requireContext(), getString(R.string.you_must_select_delivery_address), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTotalSum() {
        int subTotalSum = 0;
        int deliveryCharge = 1;
        for (int i = 0; i < cartItems.size(); i++) {
            ProductModel productModel = cartItems.get(i).getProductModel();
            subTotalSum += cartItems.get(i).getCount() * cartItems.get(i).getProductModel().getPrice();
            double distance = calculateDistance(productModel.getLatitude(), productModel.getLongitude(), latitude, longitude);
            deliveryCharge += Math.round(distance) * deliveryForKm;
        }

        binding.subTotal.setText(String.valueOf(subTotalSum));
        if (latitude != -91 && longitude != 181) {
            binding.deliveryCharge.setText(String.valueOf(deliveryCharge));
            binding.total.setText(String.valueOf(subTotalSum + deliveryCharge));
        }
    }

    private void createOrders() {
        OrderModel order = new OrderModel();
        order.setUserId(currentUser.getUid());
        order.setLatitude(latitude);
        order.setLongitude(longitude);

        for (int i = 0; i < cartItems.size(); i++) {
            ProductModel productModel = cartItems.get(i).getProductModel();
            order.setOrderId(db.collection("Orders").document().getId());
            order.setProduct(cartItems.get(i));
            order.setTotalPrice(cartItems.get(i).getCount() * productModel.getPrice());
            uploadOrder(order);
        }
        Intent intent = new Intent(requireActivity(), CongratulationsActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void uploadOrder(OrderModel orderModel) {
        db.collection("Orders")
                .document(orderModel.getOrderId())
                .set(orderModel);
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double latDiff = lat2Rad - lat1Rad;
        double lonDiff = lon2Rad - lon1Rad;

        double a = Math.pow(Math.sin(latDiff / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(lonDiff / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}