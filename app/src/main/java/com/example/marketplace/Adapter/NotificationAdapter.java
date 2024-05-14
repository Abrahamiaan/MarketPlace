package com.example.marketplace.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace.Fragment.NotificationFragment;
import com.example.marketplace.Model.NotificationModel;
import com.example.marketplace.Model.ReviewModel;
import com.example.marketplace.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    Context context;
    List<NotificationModel> notificationList;
    NotificationFragment fragment;

    public NotificationAdapter(Context context, List<NotificationModel> notificationList, NotificationFragment fragment) {
        this.notificationList = notificationList;
        this.fragment = fragment;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);
        if (notification.getType().contains("REVIEW") && !notification.isRead()) {
            String[] type = notification.getType().split("-");
            holder.notificationParentLayout.setOnClickListener(v -> openSheet(type[1], notification));
        }
        holder.bind(notification);
    }

    private void openSheet(String subjectId, NotificationModel notification) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        @SuppressLint("InflateParams") View v = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_add_review, null);
        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.show();

        AppCompatEditText editText = v.findViewById(R.id.review);
        RatingBar ratingBar = v.findViewById(R.id.rating);
        AppCompatButton appCompatButton = v.findViewById(R.id.sendReview);

        appCompatButton.setOnClickListener(view -> {
            String reviewTxt = editText.getText().toString();
            float rating = ratingBar.getRating();
            if (!reviewTxt.isEmpty() && rating >= 0) {
                ReviewModel reviewModel = new ReviewModel();
                reviewModel.setReviewerId(currentUser.getUid());
                reviewModel.setReviewerName(currentUser.getDisplayName());
                reviewModel.setSubjectId(subjectId);
                reviewModel.setComment(reviewTxt);
                reviewModel.setRating((int)rating);
                reviewModel.setCreatedAt(new Date());

                addReview(bottomSheetDialog, reviewModel);
                markAsRead(notification);
            } else {
                Toast.makeText(context, context.getString(R.string.you_must_write_review_and_add_rating), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addReview(BottomSheetDialog dialog, ReviewModel reviewModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Reviews")
                .add(reviewModel)
                .addOnSuccessListener(documentReference ->  {
                    Log.d("ADD REVIEW", "Review added with ID: " + documentReference.getId());
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Log.e("ADD REVIEW", "Error adding review", e));
    }

    public void markAsRead(NotificationModel notificationModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Notifications")
                .document(notificationModel.getNotificationId())
                .update("read", true)
                .addOnSuccessListener(aVoid -> Log.d("Mark As Read", "Document successfully updated"))
                .addOnFailureListener(e -> Log.e("Mark As Read", "Error updating document", e));
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
