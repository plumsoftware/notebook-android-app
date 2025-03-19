package ru.plumsoftware.notebook.services.push;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "TAG_1";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
