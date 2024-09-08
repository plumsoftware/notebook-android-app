package ru.plumsoftware.notebook.presentation.activities.main.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;

import java.util.ArrayList;
import java.util.List;

import ru.plumsoftware.data.database.SQLiteDatabaseManager;
import ru.plumsoftware.data.model.database.DatabaseConstants;
import ru.plumsoftware.data.model.ui.Note;
import ru.plumsoftware.notebook.manager.ads.AdsIds;
import ru.plumsoftware.notebook.presentation.activities.main.view.MainView;
import ru.plumsoftware.notebook.presentation.presenters.main.MainPresenter;

public class MainPresenterImpl implements MainPresenter {

    private MainView view;
    private final Context context;
    private final Activity activity;
    private SQLiteDatabase sqLiteDatabaseNotes;
    private AppOpenAd mainAppOpenAd = null;

    public MainPresenterImpl(MainView view, Context context, Activity activity) {
        this.view = view;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void initMobileSdk() {
        MobileAds.initialize(context, () -> {
        });
    }


    @Override
    public void initNotes() {
        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
        sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();

        List<Note> notes = loadNotes();
        view.showNotes(notes);
    }

    @Override
    public void initOpenAds() {
        final AppOpenAdLoader appOpenAdLoader = new AppOpenAdLoader(context);
        final AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(AdsIds.OPEN_AD_UNIT_ID).build();
        view.showLoading();
        AppOpenAdEventListener appOpenAdEventListener = new AppOpenAdEventListener() {
            @Override
            public void onAdShown() {
                view.hideLoading();
            }

            @Override
            public void onAdFailedToShow(@NonNull final AdError adError) {
                view.hideLoading();
            }

            @Override
            public void onAdDismissed() {
                clearAppOpenAd();
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
            }

            @Override
            public void onAdImpression(@Nullable final ImpressionData impressionData) {
                // Called when an impression is recorded for an ad.
            }
        };

        AppOpenAdLoadListener appOpenAdLoadListener = new AppOpenAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final AppOpenAd appOpenAd) {
                mainAppOpenAd = appOpenAd;
                appOpenAd.setAdEventListener(appOpenAdEventListener);
                mainAppOpenAd.show(activity);
                view.hideLoading();
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                view.hideLoading();
            }
        };

        appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener);
        appOpenAdLoader.loadAd(adRequestConfiguration);
    }

    @Override
    public void loadData() {
        view.showLoading();
        view.hideLoading();
    }

    @Override
    public void detachView() {
        view = null;
    }

    @NonNull
    private List<Note> loadNotes() {
        List<Note> notes = new ArrayList<>();
        List<Note> pinnedNotes = loadPinnedNotes();
        List<Note> simpleNotes = loadSimpleNotes();
        notes.addAll(pinnedNotes);
        notes.addAll(simpleNotes);
        return notes;
    }

    @NonNull
    private List<Note> loadPinnedNotes() {
        List<Note> notes = new ArrayList<>();

        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabaseNotes.query(
                DatabaseConstants._NOTES_TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DatabaseConstants._IS_PINNED + " = ?",              // The columns for the WHERE clause
                new String[]{"1"},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                //"DATE_FORMAT("+new SimpleDateFormat("")+", '%m%d')"               // The sort order
                DatabaseConstants._ADD_NOTE_TIME + " DESC"
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
            int notePromoResId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_PROMO));
            int isPinned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_PINNED));
            int isLiked = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_LIKED));
            int colorRes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_COLOR));
            String noteName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_NAME));
            String noteText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_TEXT));
            long addTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants._ADD_NOTE_TIME));
            String notificationChannelId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._CHANNEL_ID));
            int isNotify = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_NOTIFY));

            Note note = new Note(
                    id,
                    0,
                    notePromoResId,
                    isPinned,
                    isLiked,
                    colorRes,
                    noteName,
                    noteText,
                    addTime,
                    0,
                    notificationChannelId,
                    isNotify

            );
            notes.add(note);
        }
        cursor.close();
        return notes;
    }

    @NonNull
    private List<Note> loadSimpleNotes() {
        List<Note> notes = new ArrayList<>();

        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabaseNotes.query(
                DatabaseConstants._NOTES_TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DatabaseConstants._IS_PINNED + " = ?",              // The columns for the WHERE clause
                new String[]{"0"},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                DatabaseConstants._ADD_NOTE_TIME + " DESC"               // The sort order
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
            int notePromoResId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_PROMO));
            int isPinned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_PINNED));
            int isLiked = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_LIKED));
            int colorRes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_COLOR));
            String noteName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_NAME));
            String noteText = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_TEXT));
            long addTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants._ADD_NOTE_TIME));
            String notificationChannelId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._CHANNEL_ID));
            int isNotify = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_NOTIFY));

            Note note = new Note(
                    id,
                    0,
                    notePromoResId,
                    isPinned,
                    isLiked,
                    colorRes,
                    noteName,
                    noteText,
                    addTime,
                    0,
                    notificationChannelId,
                    isNotify
            );
            notes.add(note);
        }
        cursor.close();
        return notes;
    }

    private void clearAppOpenAd() {
        if (mainAppOpenAd != null) {
            mainAppOpenAd.setAdEventListener(null);
            mainAppOpenAd = null;
        }
    }
}
