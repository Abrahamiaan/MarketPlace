package com.example.marketplace.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Fragment.NotificationFragment;
import com.example.marketplace.Model.NotificationModel;
import com.example.marketplace.R;
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    List<NotificationModel> notificationList;
    NotificationFragment fragment;

    public NotificationAdapter(List<NotificationModel> notificationList, NotificationFragment fragment) {
        this.notificationList = notificationList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);
        holder.notificationParentLayout.setOnClickListener(v -> {
            fragment.markAsRead(notification.getOwnerId(), notification.getNotificationId());
        });
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationContent;
        TextView notificationTitle;
        TextView timestamp;
        MaterialCardView notificationParentLayout;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            notificationContent = itemView.findViewById(R.id.notificationText);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            timestamp = itemView.findViewById(R.id.timestamp);
            notificationParentLayout = itemView.findViewById(R.id.notificationParentLayout);
        }

        public void bind(NotificationModel notification) {
            notificationContent.setText(notification.getMessage());
            notificationTitle.setText(notification.getTitle());

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.US);

            try {
                Date date = inputFormat.parse(notification.getTimestamp());
                String formattedDate = outputFormat.format(date);
                timestamp.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
