package ru.plumsoftware.notebook.manager.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.presentation.activities.MainActivity;

public class NotificationManager {

    @SuppressLint("MissingPermission")
    public static void createNotification(Context context, String title, String message, int color, String notificationChannelId) {
        LocalNotificationConstants.CHANNEL_ID = notificationChannelId;

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "plumsoftware.notebook.notif_name_" + LocalNotificationConstants.CHANNEL_ID;
            String description = "plumsoftware.notebook.notif_channel_description";
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(LocalNotificationConstants.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(color);
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = buildStandardNotification(context, title, message, pendingIntent, color);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Calendar.getInstance().get(Calendar.MILLISECOND), builder.build());
    }

    private static NotificationCompat.Builder buildStandardNotification(Context context, String title, String message, PendingIntent pendingIntent, int color) {
        return new NotificationCompat.Builder(context, LocalNotificationConstants.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColorized(true)
                .setColor(color)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setGroup(LocalNotificationConstants.GROUP_KEY);
    }
}
