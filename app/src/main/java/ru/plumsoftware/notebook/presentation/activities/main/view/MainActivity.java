package ru.plumsoftware.notebook.presentation.activities.main.view;

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

import ru.plumsoftware.data.database.SQLiteDatabaseManager;
import ru.plumsoftware.data.model.database.DatabaseConstants;
import ru.plumsoftware.data.model.ui.Note;
import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.presentation.activities.AddNoteActivity;
import ru.plumsoftware.notebook.presentation.activities.main.presenter.MainPresenterImpl;
import ru.plumsoftware.notebook.presentation.adapters.NoteAdapter;
import ru.plumsoftware.notebook.presentation.dialogs.ProgressDialog;
import ru.plumsoftware.notebook.presentation.presenters.main.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainView {

    private MainPresenter presenter;

    private Context context;
    private Activity activity;

    private static RecyclerView recyclerViewNotes;
    private ProgressDialog progressDialog;

    public static SQLiteDatabase sqLiteDatabaseNotes;
    private static boolean isList = true;
    private int
            color,
            opacityRes = R.drawable.ic_coffee;

    private AppOpenAd mAppOpenAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);

//        Base variables
        context = MainActivity.this;
        activity = MainActivity.this;

        presenter = new MainPresenterImpl(this, context, activity);

//        Find views by id
        SearchView searchView = findViewById(R.id.searchView);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        ImageView filterAsList = findViewById(R.id.filterAsList);
        ImageButton addNote = findViewById(R.id.addNote);
        progressDialog = new ProgressDialog(context, R.style.CustomProgressDialog);


        presenter.initMobileSdk();
        presenter.initNotes();

//        load ad
        if (getIntent().getBooleanExtra("isLoadAppOpenAd", true)) {
            presenter.initOpenAds();
        }

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
                intent.putExtra("LoadInterstitialAd", getIntent().getBooleanExtra("LoadInterstitialAd", true));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void clearAppOpenAd() {
        if (mAppOpenAd != null) {
            mAppOpenAd.setAdEventListener(null);
            mAppOpenAd = null;
        }
    }

    @Override
    public void showNotes(List<Note> noteList) {
        NoteAdapter noteAdapter = new NoteAdapter(context, activity, noteList, 1);

        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewNotes.setAdapter(noteAdapter);
    }

    @Override
    public void showLoading() {
        progressDialog.showDialog();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }
}