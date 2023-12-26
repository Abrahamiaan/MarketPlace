package com.example.marketplace.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.marketplace.R;
import com.example.marketplace.databinding.ActivityMainBinding;
import com.example.marketplace.Fragment.*;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        System.out.println(binding);
        setContentView(binding.getRoot());

        bottomNavigation = binding.navigationBar;

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.vector_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.vector_cart));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.vector_search));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.vector_notification));
        bottomNavigation.add(new MeowBottomNavigation.Model(5, R.drawable.vector_sale));

        bottomNavigation.setOnClickMenuListener(model -> {
            switchFragment(model.getId());
            return null;
        });

        switchFragment(1);
        bottomNavigation.show(1, true);
    }

    private void switchFragment(int itemId) {
        Fragment selectedFragment = null;

        switch (itemId) {
            case 1:
                selectedFragment = new HomeFragment();
                break;
            case 2:
                selectedFragment = new HomeFragment();
                break;
            case 3:
                selectedFragment = new HomeFragment();
                break;
            case 4:
                selectedFragment = new NotificationFragment();
                break;
            case 5:
                selectedFragment = new HomeFragment();
                break;
        }

        // Replace the current fragment with the selected one
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
        }
    }
}