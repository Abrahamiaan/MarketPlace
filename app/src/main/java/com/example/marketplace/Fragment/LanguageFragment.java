package com.example.marketplace.Fragment;


import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentLanguageBinding;

import java.util.Locale;

public class LanguageFragment extends Fragment {

    private static final String SELECTED_LANGUAGE = "Selected-Language";
    FragmentLanguageBinding binding;
    ConstraintLayout parentLayout;

    public LanguageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLanguageBinding.inflate(inflater, container, false);

        parentLayout = binding.parentLayout;

        setupLanguageSelection();
        MeowBottomNavigation navigationBar =  getActivity().findViewById(R.id.navigationBar);
        if (navigationBar != null) {
            navigationBar.setVisibility(View.GONE);
        }

        binding.toBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        binding.armLinear.setOnClickListener(v -> updateLocale("hy"));
        binding.engLinear.setOnClickListener(v -> updateLocale("en"));
        binding.rusLinear.setOnClickListener(v -> updateLocale("ru"));

        return binding.getRoot();
    }

    private void setupLanguageSelection() {
        String selectedLanguageCode = getSelectedLanguageCode();
        updateLanguageSelectionUI(selectedLanguageCode);
    }

    private void updateLanguageSelectionUI(String selectedLanguageCode) {
        Drawable languageBg = ContextCompat.getDrawable(requireContext(), R.drawable.language_bg);
        Drawable selectedBg = ContextCompat.getDrawable(requireContext(), R.drawable.selected_bg);
        binding.armLinear.setBackground(languageBg);
        binding.rusLinear.setBackground(languageBg);
        binding.engLinear.setBackground(languageBg);

        String currentLg = getSelectedLanguageCode();

        switch (selectedLanguageCode) {
            case "hy":
                binding.armLinear.setBackground(selectedBg);
                if (currentLg.equals("en")) {
                    updateConstraints(R.id.arm_linear, R.id.linearLayoutParent);
                    updateConstraints(R.id.eng_linear, R.id.arm_linear);
                    updateConstraints(R.id.rus_linear, R.id.eng_linear);
                } else if (currentLg.equals("ru")) {
                    updateConstraints(R.id.arm_linear, R.id.linearLayoutParent);
                    updateConstraints(R.id.rus_linear, R.id.arm_linear);
                    updateConstraints(R.id.eng_linear, R.id.rus_linear);
                }
                break;
            case "en":
                binding.engLinear.setBackground(selectedBg);
                if (currentLg.equals("hy")) {
                    updateConstraints(R.id.eng_linear, R.id.linearLayoutParent);
                    updateConstraints(R.id.arm_linear, R.id.eng_linear);
                    updateConstraints(R.id.rus_linear, R.id.arm_linear);
                }
                else if(currentLg.equals("ru")) {
                    updateConstraints(R.id.eng_linear, R.id.linearLayoutParent);
                    updateConstraints(R.id.rus_linear, R.id.eng_linear);
                    updateConstraints(R.id.arm_linear, R.id.rus_linear);
                } else if (currentLg.equals("en")) {
                    updateConstraints(R.id.eng_linear, R.id.linearLayoutParent);
                    updateConstraints(R.id.arm_linear, R.id.eng_linear);
                    updateConstraints(R.id.rus_linear, R.id.arm_linear);
                }
                break;
            case "ru":
                binding.rusLinear.setBackground(selectedBg);
                if (currentLg.equals("en")) {
                    updateConstraints(R.id.rus_linear, R.id.linearLayoutParent);
                    updateConstraints(R.id.eng_linear, R.id.rus_linear);
                    updateConstraints(R.id.arm_linear, R.id.eng_linear);
                }
                else if(currentLg.equals("hy")) {
                    updateConstraints(R.id.rus_linear, R.id.linearLayoutParent);
                    updateConstraints(R.id.arm_linear, R.id.rus_linear);
                    updateConstraints(R.id.eng_linear, R.id.arm_linear);
                } else if (currentLg.equals("ru")) {
                    updateConstraints(R.id.rus_linear, R.id.linearLayoutParent);
                    updateConstraints(R.id.eng_linear, R.id.rus_linear);
                    updateConstraints(R.id.arm_linear, R.id.eng_linear);
                }
                break;
        }
    }

    private String getSelectedLanguageCode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return preferences.getString(SELECTED_LANGUAGE, "en");
    }

    private void saveSelectedLanguage(String languageCode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        preferences.edit().putString(SELECTED_LANGUAGE, languageCode).apply();
    }

    private void updateLocale(String languageCode) {
        updateLanguageSelectionUI(languageCode);
        Locale locale = new Locale(languageCode);
        getResources().getConfiguration().setLocale(locale);
        getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());
        saveSelectedLanguage(languageCode);
        getActivity().onBackPressed();
    }

    private void updateConstraints(int fromViewId, int toViewId) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parentLayout);
        constraintSet.connect(fromViewId, ConstraintSet.TOP, toViewId, ConstraintSet.BOTTOM);
        constraintSet.applyTo(parentLayout);
    }
}
