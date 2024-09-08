package ru.plumsoftware.notebook.manager.extra;

import kotlin.Pair;

public class ExtraNames {
    public static class LocalNotificationExtra {
        public static String message = "message";
        public static String notificationChannelId = "notificationChannelId";
        public static Pair<String, Integer> colorPair = new Pair<>("color", 0);
    }

    public static class MainActivity {
        public static String isLoadAppOpenAd = "isLoadAppOpenAd";
    }
}
