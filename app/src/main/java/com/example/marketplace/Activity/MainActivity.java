package com.example.marketplace.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.marketplace.R;
import com.example.marketplace.Utils.LocaleHelper;
import com.example.marketplace.databinding.ActivityMainBinding;
import com.example.marketplace.Fragment.*;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        LocaleHelper.setAppLanguage(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGlobalFields();
        initListeners();
    }

    private void switchFragment(int itemId) {
        Fragment selectedFragment = null;

        switch (itemId) {
            case 1:
                selectedFragment = new HomeFragment();
                binding.mainLayout.setBackgroundColor(Color.WHITE);
                break;
            case 2:
                selectedFragment = new FavoriteFragment();
                binding.mainLayout.setBackgroundColor(Color.WHITE);
                break;
            case 3:
                selectedFragment = new SellingFragment();
                binding.mainLayout.setBackgroundColor(Color.WHITE);
                break;
            case 4:
                selectedFragment = new CartFragment();
                break;
            case 5:
                selectedFragment = new ProfileFragment();
                binding.mainLayout.setBackgroundColor(Color.WHITE);
                break;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (binding.navigationBar.getVisibility() == View.GONE) {
            binding.navigationBar.setVisibility(View.VISIBLE);
        }
    }
    private void initGlobalFields() {
        binding.navigationBar.add(new MeowBottomNavigation.Model(1, R.drawable.vector_home));
        binding.navigationBar.add(new MeowBottomNavigation.Model(2, R.drawable.vector_favorite));
        binding.navigationBar.add(new MeowBottomNavigation.Model(3, R.drawable.vector_add_outline));
        binding.navigationBar.add(new MeowBottomNavigation.Model(4, R.drawable.vector_cart));
        binding.navigationBar.add(new MeowBottomNavigation.Model(5, R.drawable.vector_profile));
        binding.navigationBar.setVisibility(View.VISIBLE);
        switchFragment(1);
        binding.navigationBar.show(1, true);
    }
    private void initListeners() {
        binding.navigationBar.setOnClickMenuListener(model -> {
            switchFragment(model.getId());
            return null;
        });
    }
}