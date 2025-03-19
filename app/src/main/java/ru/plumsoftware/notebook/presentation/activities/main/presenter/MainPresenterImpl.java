package ru.plumsoftware.notebook.presentation.activities.main.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.data.database.SQLiteDatabaseManager;
import ru.plumsoftware.data.model.database.DatabaseConstants;
import ru.plumsoftware.data.model.ui.Note;
import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.manager.ads.AdsIds;
import ru.plumsoftware.notebook.manager.extra.ExtraNames;
import ru.plumsoftware.notebook.presentation.activities.note.view.AddNoteActivity;
import ru.plumsoftware.notebook.presentation.activities.main.view.MainView;
import ru.plumsoftware.notebook.presentation.dialogs.ProgressDialog;

public class MainPresenterImpl implements MainPresenter {

    private final MainView mainView;

    private final Context context;
    private final Activity activity;
    private SQLiteDatabase sqLiteDatabaseNotes;

    private AppOpenAd mainAppOpenAd = null;

    private boolean isList = true;
    private List<Note> notes;
    private final List<Note> filteredNotes;

    private ProgressDialog progressDialog;

    public MainPresenterImpl(Context context, @NonNull Activity activity, MainView mainView) {
        this.context = context;
        this.activity = activity;
        this.mainView = mainView;
        filteredNotes = new ArrayList<>();
        notes = new ArrayList<>();
        progressDialog = new ProgressDialog(activity, R.style.CustomProgressDialog);
    }

    @Override
    public void changeListStyle() {
        if (isList) {
            mainView.initRecyclerView(notes, new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            mainView.changeFilterButtonImage(R.drawable.ic_table_rows);
        } else {
            mainView.initRecyclerView(notes, new LinearLayoutManager(context));
            mainView.changeFilterButtonImage(R.drawable.ic_baseline_filter_list);
        }
        isList = !isList;
    }

    @Override
    public void openAddNoteActivity() {
        Intent intent = new Intent(activity, AddNoteActivity.class);
        intent.putExtra(ExtraNames.MainActivity.isLoadAppOpenAd, false);
        intent.putExtra(ExtraNames.MainActivity.LoadInterstitialAd, activity.getIntent().getBooleanExtra(ExtraNames.MainActivity.LoadInterstitialAd, true));
        activity.startActivity(intent);
    }

    @Override
    public void initMobileSdk() {
        MobileAds.initialize(context, () -> {
        });
    }

    private void showProgressDialog() {
        progressDialog.showDialog();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }


    @Override
    public void initNotes(Conditions conditions) {
        new Thread(() -> {
            if (conditions instanceof Conditions.Search) {
                filteredNotes.clear();
                String query = ((Conditions.Search) conditions).getQuery();

                for (Note note : notes) {
                    if (note.getNoteName().contains(query) ||
                            note.getNoteText().contains(query) ||
                            new SimpleDateFormat("dd.MM.yyyy HH.mm", Locale.getDefault()).format(new Date(note.getAddNoteTime())).contains(query)
                    ) {
                        filteredNotes.add(note);
                    }
                }
                RecyclerView.LayoutManager layoutManager;
                if (isList) {
                    layoutManager = new LinearLayoutManager(context);
                } else {
                    layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                }

                mainView.initRecyclerView(filteredNotes, layoutManager);
            }
            else if (conditions instanceof Conditions.All) {
                notes.clear();
                SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
                sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();
                notes = loadNotes();

                isList = true;

                activity.runOnUiThread(() -> {
                    mainView.changeFilterButtonImage(R.drawable.ic_baseline_filter_list);
                    mainView.initRecyclerView(notes, new LinearLayoutManager(context));
                });
            }
        }).start();
    }

    @Override
    public void initOpenAds() {
        activity.runOnUiThread(this::showProgressDialog);
        final AppOpenAdLoader appOpenAdLoader = new AppOpenAdLoader(context);
        final AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(AdsIds.OPEN_AD_UNIT_ID).build();

        AppOpenAdEventListener appOpenAdEventListener = new AppOpenAdEventListener() {
            @Override
            public void onAdShown() {
                activity.runOnUiThread(() -> dismissProgressDialog());
            }

            @Override
            public void onAdFailedToShow(@NonNull final AdError adError) {
                activity.runOnUiThread(() -> dismissProgressDialog());
            }

            @Override
            public void onAdDismissed() {
                clearAppOpenAd();
                activity.runOnUiThread(() -> dismissProgressDialog());
            }

            @Override
            public void onAdClicked() {
                activity.runOnUiThread(() -> dismissProgressDialog());
            }

            @Override
            public void onAdImpression(@Nullable final ImpressionData impressionData) {
                activity.runOnUiThread(() -> dismissProgressDialog());
            }
        };
        AppOpenAdLoadListener appOpenAdLoadListener = new AppOpenAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final AppOpenAd appOpenAd) {
                dismissProgressDialog();
                mainAppOpenAd = appOpenAd;
                activity.runOnUiThread(() -> {
                    mainAppOpenAd.setAdEventListener(appOpenAdEventListener);
                    mainAppOpenAd.show(activity);
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                activity.runOnUiThread(() -> dismissProgressDialog());
            }
        };

        appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener);
        appOpenAdLoader.loadAd(adRequestConfiguration);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            activity.runOnUiThread(this::dismissProgressDialog);
        });
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
