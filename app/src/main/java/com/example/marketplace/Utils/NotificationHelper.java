package com.example.marketplace.Utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.marketplace.Model.NotificationModel;
import com.example.marketplace.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
                    .addHeader("Authorization", "Bearer " + R.string.FCM_SERVER_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    } else {
                        String responseData = response.body().string();
                        System.out.println("Response from FCM: " + responseData);
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }
}
