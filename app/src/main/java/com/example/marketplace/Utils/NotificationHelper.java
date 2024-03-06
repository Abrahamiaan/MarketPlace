package com.example.marketplace.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.marketplace.Activity.DetailActivity;
import com.example.marketplace.Model.NotificationModel;
import com.example.marketplace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class NotificationHelper {
    final String CHANNEL_ID = "CHANNEL_ID_NOTIFICATION";
    final String COLLECTION_NAME = "Notifications";
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    public NotificationHelper(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void makeNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.vector_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(context.getApplicationContext(), DetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "Some value to be passed here");

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel =
                notificationManager.getNotificationChannel(CHANNEL_ID);
        if (notificationChannel == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "Some description", importance);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        saveNotificationInFirebase(title, message);
        notificationManager.notify(0, builder.build());
    }
    private void saveNotificationInFirebase(String title, String message) {
        String notificationId = db.collection(COLLECTION_NAME).document().getId();
        NotificationModel notificationModel = new NotificationModel(message, title, currentUser.getUid());

        notificationModel.setNotificationId(notificationId);

        db.collection(COLLECTION_NAME)
                .document(notificationId)
                .set(notificationModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("Save Notification", "Notification Saved");
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("Save Notification", Arrays.toString(e.getStackTrace()));
                        }
                    }
                });
    }
}
