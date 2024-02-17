package com.example.marketplace.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.example.marketplace.Activity.SearchResultActivity;
import com.example.marketplace.Adapter.RecentAdapter;
import com.example.marketplace.R;
import com.example.marketplace.Utils.DataProvider;
import com.example.marketplace.databinding.FragmentSearchBinding;

import java.util.List;

public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    RecentAdapter recentAdapter;
    DataProvider dataProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();

        return binding.getRoot();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.searchBar.clearFocus();
    }
    @Override
    public void onPause() {
        super.onPause();
        binding.searchBar.clearFocus();
    }
    @Override
    public void onStart() {
        super.onStart();
        binding.searchBar.requestFocus();
    }
    private void updateRecentlyRecycler(String query) {
        List<String> suggestionQueries = dataProvider.getSuggestionQuery(query);
        if (suggestionQueries.isEmpty()) {
            binding.recentRecyclerView.setVisibility(View.GONE);
        } else {
            binding.recentRecyclerView.setVisibility(View.VISIBLE);
            recentAdapter.updateData(suggestionQueries);
        }
    }
    private void initRecentlyRecycler(List<String> data) {
        recentAdapter = new RecentAdapter(data, dataProvider);
        binding.recentRecyclerView.setAdapter(recentAdapter);

        binding.recentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
    private void initGlobalFields() {
        dataProvider = DataProvider.getInstance(requireContext());
        initRecentlyRecycler(dataProvider.getRecentQueries());
        binding.searchBar.requestFocus();
    }
    private void initListeners() {
        binding.searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.recentRecyclerView.setVisibility(View.VISIBLE);
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                binding.recentRecyclerView.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.searchBar.getWindowToken(), 0);
            }
        });
        binding.searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = binding.searchBar.getText().toString();
                dataProvider.saveSearchQuery(query);
                binding.searchBar.clearFocus();
                Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return true;
            }
            return false;
        });
        binding.backBtn.setOnClickListener(v -> {
            binding.searchBar.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.searchBar.getWindowToken(), 0);
        });
        binding.clearInputBtn.setOnClickListener(v -> binding.searchBar.getText().clear());
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRecentlyRecycler(s.toString());
            }
        });
    }
}