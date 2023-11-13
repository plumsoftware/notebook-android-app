package ru.plumsoftware.notebook.utilities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.activities.MainActivity;

public class NotificationUtils {

    private static String CHANNEL_ID = "ru.plumsoftware.notebook.notif_channel"; // Уникальный идентификатор канала уведомлений
    private static final String GROUP_KEY = "ru.plumsoftware.notebook.group_key"; // Идентификатор группы уведомлений

    @SuppressLint("MissingPermission")
    public static void createNotification(Context context, String title, String message, int color, String notificationChannelId) {
        //Id канала уведомления
        CHANNEL_ID = notificationChannelId;

        // Создание интента для перехода при клике на уведомление
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Создание канала уведомлений для Android 8.0 (Oreo) и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "plumsoftware.notebook.notif_name";
            String description = "plumsoftware.notebook.notif_channel_description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(color);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Создание уведомления
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColorized(true)
                .setColor(color)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setGroup(GROUP_KEY);

        // Отображение уведомления
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}
