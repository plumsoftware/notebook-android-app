package ru.plumsoftware.notebook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.material.snackbar.Snackbar;
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

import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.adapters.NoteAdapter;
import ru.plumsoftware.notebook.data.items.Group;
import ru.plumsoftware.notebook.data.items.Note;
import ru.plumsoftware.notebook.databases.DatabaseConstants;
import ru.plumsoftware.notebook.databases.SQLiteDatabaseManager;
import ru.plumsoftware.notebook.dialogs.ProgressDialog;

public class MainActivity extends AppCompatActivity {
    public static SQLiteDatabase sqLiteDatabaseNotes;
    private static RecyclerView recyclerViewNotes; //recyclerViewGroups;
    private static boolean isList = true;
    private int
            color,
            opacityRes = R.drawable.ic_coffee;
    private Context context;
    private Activity activity;

    private ProgressDialog progressDialog;
    private AppOpenAd mAppOpenAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);

        MobileAds.initialize(this, () -> {

        });

//        FVI
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        recyclerViewNotes = (RecyclerView) findViewById(R.id.recyclerViewNotes);
        //recyclerViewGroups = (RecyclerView) findViewById(R.id.recyclerViewGroups);
        ImageView filterAsList = (ImageView) findViewById(R.id.filterAsList);
        ImageButton addNote = (ImageButton) findViewById(R.id.addNote);

//        Data
        context = MainActivity.this;
        activity = MainActivity.this;
        Handler handler = new Handler();
        progressDialog = new ProgressDialog(MainActivity.this, R.style.CustomProgressDialog);
        try (SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context)) {
            sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();
        }
        List<Note> notes = new ArrayList<>();

        notes = loadNotes(context);

//        load ad
        if (getIntent().getBooleanExtra("isLoadAppOpenAd", true)) {
            final AppOpenAdLoader appOpenAdLoader = new AppOpenAdLoader(this);
            final String AD_UNIT_ID = "R-M-1957919-3";
            final AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(AD_UNIT_ID).build();

            progressDialog.showDialog();

            AppOpenAdEventListener appOpenAdEventListener = new AppOpenAdEventListener() {
                @Override
                public void onAdShown() {
                    // Called when ad is shown.
                }

                @Override
                public void onAdFailedToShow(@NonNull final AdError adError) {
                    // Called when ad failed to show.
                    progressDialog.dismiss();
                }

                @Override
                public void onAdDismissed() {
                    // Called when ad is dismissed.
                    // Clean resources after dismiss and preload new ad.
                    clearAppOpenAd();
//                showAppOpenAd();
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
                    // The ad was loaded successfully. Now you can show loaded ad.
                    mAppOpenAd = appOpenAd;
                    mAppOpenAd.setAdEventListener(appOpenAdEventListener);
                    showAppOpenAd();
                    progressDialog.dismiss();
                }

                @Override
                public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                    progressDialog.dismiss();
                }
            };

            appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener);

            appOpenAdLoader.loadAd(adRequestConfiguration);
        }


        NoteAdapter noteAdapter = new NoteAdapter(context, activity, notes, 1);
        //GroupAdapter groupAdapter = new GroupAdapter(context, activity, groups, 1);

        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewNotes.setAdapter(noteAdapter);

        filterAsList.setImageResource(R.drawable.ic_baseline_filter_list);

        color = getResources().getColor(R.color.note_blue);

//        Clickers
        filterAsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isList) {
                    isList = false;

                    recyclerViewNotes.setVisibility(View.GONE);
                    NoteAdapter noteAdapter = new NoteAdapter(context, activity, loadNotes(context), 1);
                    recyclerViewNotes.setHasFixedSize(true);
                    recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    recyclerViewNotes.setAdapter(noteAdapter);
                    recyclerViewNotes.setVisibility(View.VISIBLE);

                    filterAsList.setImageResource(R.drawable.ic_table_rows);
                } else {
                    isList = true;

                    recyclerViewNotes.setVisibility(View.GONE);
                    NoteAdapter noteAdapter = new NoteAdapter(context, activity, loadNotes(context), 1);
                    recyclerViewNotes.setHasFixedSize(true);
                    recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
                    recyclerViewNotes.setAdapter(noteAdapter);
                    recyclerViewNotes.setVisibility(View.VISIBLE);

                    filterAsList.setImageResource(R.drawable.ic_baseline_filter_list);
                }
            }
        });

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("isLoadAppOpenAd", false);
                startActivity(intent);
                finish();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                NoteAdapter noteAdapter = new NoteAdapter(context, activity, loadNotesWithCondition(s), 1);
                recyclerViewNotes.setHasFixedSize(true);
                recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
                recyclerViewNotes.setAdapter(noteAdapter);
                return false;
            }
        });
    }

    public static List<Note> loadNotes(Context context) {
        List<Note> notes = new ArrayList<>();

        if (!sqLiteDatabaseNotes.isOpen()) {
            SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
            sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();
        }
//        Pinned notes
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
//            Read data
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
//            int count = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._COUNT));
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
            Log.d("TAG", note.toString());
            notes.add(note);
        }
        cursor.close();

//        Simple notes
        @SuppressLint("Recycle") Cursor cursor1 = sqLiteDatabaseNotes.query(
                DatabaseConstants._NOTES_TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DatabaseConstants._IS_PINNED + " = ?",              // The columns for the WHERE clause
                new String[]{"0"},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                DatabaseConstants._ADD_NOTE_TIME + " DESC"               // The sort order
        );

        while (cursor1.moveToNext()) {
//            Read data
            int id = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
//            int count = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._COUNT));
            int notePromoResId = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_PROMO));
            int isPinned = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_PINNED));
            int isLiked = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_LIKED));
            int colorRes = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_COLOR));
            String noteName = cursor1.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_NAME));
            String noteText = cursor1.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_TEXT));
            long addTime = cursor1.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants._ADD_NOTE_TIME));
            String notificationChannelId = cursor1.getString(cursor1.getColumnIndexOrThrow(DatabaseConstants._CHANNEL_ID));
            int isNotify = cursor1.getInt(cursor1.getColumnIndexOrThrow(DatabaseConstants._IS_NOTIFY));

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
        cursor1.close();

        return notes;
    }

    public static List<Note> loadNotesWithCondition(String condition) {
        List<Note> notes = new ArrayList<>();

//        Pinned notes
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabaseNotes.query(
                DatabaseConstants._NOTES_TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DatabaseConstants._IS_PINNED + " = ?",              // The columns for the WHERE clause
                new String[]{"1"},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                DatabaseConstants._ADD_NOTE_TIME + " DESC"                // The sort order
        );

        while (cursor.moveToNext()) {
//            Read data
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
//            int count = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._COUNT));
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

//        Simple notes
        @SuppressLint("Recycle") Cursor cursor1 = sqLiteDatabaseNotes.query(
                DatabaseConstants._NOTES_TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DatabaseConstants._IS_PINNED + " = ?",              // The columns for the WHERE clause
                new String[]{"0"},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                DatabaseConstants._ADD_NOTE_TIME + " DESC"                // The sort order
        );

        while (cursor1.moveToNext()) {
//            Read data
            int id = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
//            int count = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._COUNT));
            int notePromoResId = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_PROMO));
            int isPinned = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_PINNED));
            int isLiked = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._IS_LIKED));
            int colorRes = cursor1.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_COLOR));
            String noteName = cursor1.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_NAME));
            String noteText = cursor1.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_TEXT));
            long addTime = cursor1.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants._ADD_NOTE_TIME));
            String notificationChannelId = cursor1.getString(cursor1.getColumnIndexOrThrow(DatabaseConstants._CHANNEL_ID));
            int isNotify = cursor1.getInt(cursor1.getColumnIndexOrThrow(DatabaseConstants._IS_NOTIFY));

            if (noteName.contains(condition) || noteText.contains(condition) || new SimpleDateFormat("dd.MM.yyyy HH.mm", Locale.getDefault()).format(new Date(addTime)).contains(condition)) {
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
        }
        cursor1.close();

        return notes;
    }

    public void saveNote(String name, String text, int or, int c, long time) {
        if (name == null || name.isEmpty())
            name = "";
        if (text == null || text.isEmpty())
            text = "";

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants._NOTE_NAME, name);
        contentValues.put(DatabaseConstants._NOTE_TEXT, text);
        contentValues.put(DatabaseConstants._NOTE_PROMO, or);
        contentValues.put(DatabaseConstants._NOTE_COLOR, c);
        contentValues.put(DatabaseConstants._IS_LIKED, 0);
        contentValues.put(DatabaseConstants._IS_PINNED, 0);
        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, time);
        sqLiteDatabaseNotes.insert(DatabaseConstants._NOTES_TABLE_NAME, null, contentValues);

        Snackbar
                .make(context, (ConstraintLayout) findViewById(R.id.layout), "Данные сохранены✅", Snackbar.LENGTH_SHORT)
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.WHITE)
                .show();
    }

    public static void reloadRecyclerView(Context context, Activity activity) {
        recyclerViewNotes.setVisibility(View.GONE);
        List<Note> notes = loadNotes(context);
        //Collections.reverse(notes);
        NoteAdapter noteAdapter = new NoteAdapter(context, activity, notes, 1);
        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewNotes.setAdapter(noteAdapter);
        recyclerViewNotes.setVisibility(View.VISIBLE);
        isList = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabaseNotes.close();
    }

    private void clearAppOpenAd() {
        if (mAppOpenAd != null) {
            mAppOpenAd.setAdEventListener(null);
            mAppOpenAd = null;
        }
    }


    private void showAppOpenAd() {
        if (mAppOpenAd != null) {
            mAppOpenAd.show(activity);
        }
    }
}