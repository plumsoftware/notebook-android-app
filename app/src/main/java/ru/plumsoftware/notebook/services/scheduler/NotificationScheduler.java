package ru.plumsoftware.notebook.services.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ru.plumsoftware.notebook.services.receive.NotificationReceiver;

public class NotificationScheduler {
    public static void scheduleNotification(Context context, long timeInMillis, String message, int color, String notificationChannelId) {
        // Создание намерения для запуска BroadcastReceiver
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("message", message);
        notificationIntent.putExtra("color", color);
        notificationIntent.putExtra("notificationChannelId", notificationChannelId);

        // Создание PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Получение AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Установка уведомления на указанное время
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }
}
