package ru.plumsoftware.notebook.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.plumsoftware.notebook.utilities.NotificationUtils;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Получение сообщения из интента
        String message = intent.getStringExtra("message");
        String notificationChannelId = intent.getStringExtra("notificationChannelId");
        int color = intent.getIntExtra("color", 0);

        // Показ уведомления
        NotificationUtils.createNotification(context, "\uD83D\uDD14 Напоминание о событии ", message, color, notificationChannelId);
    }
}
