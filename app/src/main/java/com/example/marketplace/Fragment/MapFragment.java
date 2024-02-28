package com.example.marketplace.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import android.Manifest;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentMapBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private AutocompleteSupportFragment autocompleteFragment;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private double latitude = -91;
    private double longitude = 181;
    SharedPreferences sharedPreferences;

    private static final int REQUEST_LOCATION_PERMISSION = 123;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);

        Places.initialize(requireActivity().getApplicationContext(), getString(R.string.API_KEY));

        autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);

        checkPermissions();

        return binding.getRoot();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            initListeners();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initListeners();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initListeners() {
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    updateMapLocation(latLng);
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getContext(), "An error occurred: " + status, Toast.LENGTH_SHORT).show();
                Log.i("PlaceError", "An error occurred: " + status);
            }
        });

        supportMapFragment.getMapAsync(this);
        binding.confirmBtn.setOnClickListener(v -> {
            Intent i = requireActivity().getIntent();
            i.putExtra("latitude", latitude);
            i.putExtra("longitude", longitude);
            requireActivity().onBackPressed();
        });
    }

    private void updateMapLocation(LatLng latLng) {
        if (googleMap != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        googleMap.setOnMapClickListener(this::updateMapLocationWithLabel);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("last_known_latitude", (float) latLng.latitude);
                    editor.putFloat("last_known_longitude", (float) latLng.longitude);
                    editor.apply();
                } else {
                    float latitude = sharedPreferences.getFloat("last_known_latitude", -91f);
                    float longitude = sharedPreferences.getFloat("last_known_longitude", 181f);
                    if (latitude != -91 && longitude != 181) {
                        LatLng latLng = new LatLng(latitude, longitude);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Last Known Location"));
                    } else {
                        Toast.makeText(requireContext(), "Last known location not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();

            float latitude = sharedPreferences.getFloat("last_known_latitude", -91f);
            float longitude = sharedPreferences.getFloat("last_known_longitude", 181f);
            if (latitude != -91 && longitude != 181) {
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Last Known Location"));
            } else {
                Toast.makeText(requireContext(), "Last known location not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateMapLocationWithLabel(LatLng latLng) {
        if (googleMap != null) {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Clicked Location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
            latitude = latLng.latitude;
            longitude = latLng.longitude;
        }
    }
}
