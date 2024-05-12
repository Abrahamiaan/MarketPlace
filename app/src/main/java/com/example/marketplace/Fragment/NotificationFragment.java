package com.example.marketplace.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.marketplace.Adapter.NotificationAdapter;
import com.example.marketplace.Model.NotificationModel;
import com.example.marketplace.R;
import com.example.marketplace.Utils.NotificationHelper;
import com.example.marketplace.databinding.FragmentNotificationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    FragmentNotificationBinding binding;
    NotificationAdapter notificationAdapter;
    List<NotificationModel> notificationList;
    NotificationHelper notificationHelper;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    FirebaseAuth mAuh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);

        initGlobalFields();
        initListeners();

        return binding.getRoot();
    }

    private void initGlobalFields() {
        db = FirebaseFirestore.getInstance();
        mAuh = FirebaseAuth.getInstance();
        currentUser = mAuh.getCurrentUser();
        notificationList = new ArrayList<>();
        notificationHelper = new NotificationHelper(getContext());

        binding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(requireContext(), notificationList, this);
        binding.notificationRecyclerView.setAdapter(notificationAdapter);

        fetchNotificationsFromFirebase();
    }
    private void initListeners() {
        binding.toBack.setOnClickListener(v -> getActivity().onBackPressed());
        binding.moreBtn.setOnClickListener(this::showPopup);
    }
    private void clearNotifications() {
        if (notificationList.isEmpty()) return;

        db.collection("Notifications")
                .whereEqualTo("ownerId", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Notifications").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        int size = notificationList.size();
                                        notificationList.clear();
                                        notificationAdapter.notifyItemRangeRemoved(0, size);

                                        binding.noMessagesLayout.setVisibility(View.VISIBLE);
                                    })
                                    .addOnFailureListener(e ->
                                            Log.e("Clear Notifications", "Error removing document", e));
                        }
                    } else {
                        Log.e("Clear Notifications", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void markAsRead() {
        if (notificationList.isEmpty()) return;

        db.collection("Notifications")
                .whereEqualTo("ownerId", currentUser.getUid())
                .whereEqualTo("read", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            NotificationModel notificationModel = document.toObject(NotificationModel.class);
                            if (!notificationModel.getType().contains("REVIEW")) {
                                db.collection("Notifications").document(document.getId())
                                        .update("read", true)
                                        .addOnSuccessListener(aVoid -> Log.d("Mark As Read", "Document successfully updated"))
                                        .addOnFailureListener(e -> Log.e("Mark As Read", "Error updating document", e));
                            }
                        }
                    } else {
                        Log.e("Mark As Read", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchNotificationsFromFirebase() {
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("Notifications")
                .whereEqualTo("ownerId", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            NotificationModel notification = document.toObject(NotificationModel.class);
                            notificationList.add(notification);
                            notificationAdapter.notifyItemInserted(notificationList.size());
                        }

                        boolean show = notificationList.isEmpty();
                        binding.noMessagesLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                        binding.progressBar.setVisibility(View.GONE);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("Fetch Notifications", "Error fetching notifications: " + e.getMessage());
                        }
                        binding.noMessagesLayout.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(requireActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.notification_sub_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.clearAll) {
                clearNotifications();
                return true;
            } else if (itemId == R.id.markAsRead) {
                markAsRead();
                return true;
            }
            return false;
        });

        popup.show();
    }
}
