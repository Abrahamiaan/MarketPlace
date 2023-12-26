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

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<NotificationModel> notificationList;

    public NotificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.notificationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Notification List
        notificationList = new ArrayList<>();

        // Populate Notification List (replace this with your actual data)
//        notificationList.add(new NotificationModel("Notification 1"));
//        notificationList.add(new NotificationModel("Notification 2"));
        // Add more notifications as needed

        // Initialize Adapter and set it to RecyclerView
        notificationAdapter = new NotificationAdapter(notificationList);
        recyclerView.setAdapter(notificationAdapter);

        // Check if there are no notifications and show the placeholder if needed
        boolean show = notificationList.isEmpty();

        View placeholderLayout = view.findViewById(R.id.noMessagesLayout);
        if (placeholderLayout != null) {
            placeholderLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        return view;
    }
}
