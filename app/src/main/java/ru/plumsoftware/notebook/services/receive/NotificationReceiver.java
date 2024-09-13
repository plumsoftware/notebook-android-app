package ru.plumsoftware.notebook.services.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import ru.plumsoftware.notebook.manager.extra.ExtraNames;
import ru.plumsoftware.notebook.manager.notification.NotificationManager;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        String message = intent.getStringExtra(ExtraNames.LocalNotificationExtra.message);
        String notificationChannelId = intent.getStringExtra(ExtraNames.LocalNotificationExtra.notificationChannelId);
        int color = intent.getIntExtra(ExtraNames.LocalNotificationExtra.colorPair.getFirst(), ExtraNames.LocalNotificationExtra.colorPair.getSecond());

        NotificationManager.createNotification(context, "\uD83D\uDD14 Напоминание о событии ", message, color, notificationChannelId);
    }
}
