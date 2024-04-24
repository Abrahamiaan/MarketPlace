package com.example.marketplace.Utils;

import android.content.Context;
import android.util.Log;

import com.example.marketplace.Model.NotificationModel;
import com.example.marketplace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class NotificationHelper {
    final String COLLECTION_NAME = "Notifications";
    Context context;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public NotificationHelper(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }
    public void makeNotification(NotificationModel notificationModel) {
        saveNotificationInFirebase(notificationModel);
        getToken(notificationModel);
    }
    private void getToken(NotificationModel notificationModel) {
        db.collection("UserMetaData")
                .document(notificationModel.getOwnerId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String fcmToken = document.getString("FCMToken");
                            sendNotification(notificationModel, fcmToken);
                        }
                    } else {
                        Log.e("NotificationHelper: ", Arrays.toString(task.getException().getStackTrace()));
                    }
                });
    }
    private void saveNotificationInFirebase(NotificationModel notificationModel) {
        String notificationId = db.collection(COLLECTION_NAME).document().getId();
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
    private void sendNotification(final NotificationModel notificationModel, final String receiverToken) {
        new Thread(() -> {
            String title = notificationModel.getTitle();
            String body = notificationModel.getMessage();

            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonNotification = new JSONObject();
            JSONObject whole0bj = new JSONObject();
            try {
                jsonNotification.put("title", title);
                jsonNotification.put("body", body);
                whole0bj.put("to", receiverToken);
                whole0bj.put("notification", jsonNotification);
            } catch (JSONException e) {
                Log.d("Send Notification: ", e.toString());
            }

            RequestBody rBody = RequestBody.create(mediaType, whole0bj.toString());
            Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                    .post(rBody)
                    .addHeader("Authorization", "key=" + R.string.FCM_SERVER_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response != null && response.body() != null) {
                    String responseBody = response.body().string();
                    System.out.println("Response: " + responseBody);
                }
            } catch (IOException e) {
                Log.e("Send Notification", e.getMessage());
                e.getStackTrace();
            }
        }).start();
    }
}
