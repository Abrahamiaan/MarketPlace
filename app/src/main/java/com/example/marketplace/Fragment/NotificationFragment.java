package com.example.marketplace.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Adapter.NotificationAdapter;
import com.example.marketplace.Model.NotificationModel;
import com.example.marketplace.R;
import com.example.marketplace.databinding.FragmentNotificationBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;
    NotificationAdapter notificationAdapter;
    List<NotificationModel> notificationList;

    public NotificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);

//        binding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        notificationList = new ArrayList<>();
//        notificationAdapter = new NotificationAdapter(notificationList);
//        binding.notificationRecyclerView.setAdapter(notificationAdapter);
//
//        boolean show = notificationList.isEmpty();
//        binding.noMessagesLayout.setVisibility(show ? View.VISIBLE : View.GONE);

        return binding.getRoot();
    }
}
