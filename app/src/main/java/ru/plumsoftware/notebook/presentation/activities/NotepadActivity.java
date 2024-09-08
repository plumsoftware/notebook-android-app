package ru.plumsoftware.notebook.presentation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import ru.plumsoftware.data.database.SQLiteDatabaseManager;
import ru.plumsoftware.data.model.database.DatabaseConstants;
import ru.plumsoftware.data.model.ui.Colors;
import ru.plumsoftware.data.model.ui.Note;
import ru.plumsoftware.data.model.ui.Shape;
import ru.plumsoftware.notebook.presentation.dialogs.ProgressDialog;
import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.presentation.adapters.ColorAdapter;
import ru.plumsoftware.notebook.presentation.adapters.NoteAdapter;
import ru.plumsoftware.notebook.presentation.adapters.OpacityAdapter;

public class NotepadActivity extends AppCompatActivity {
    public static SQLiteDatabase sqLiteDatabaseNotes;
    private static RecyclerView recyclerViewNotes; //recyclerViewGroups;
    private static boolean isList = true;
    private int
            color,
            opacityRes = R.drawable.ic_coffee;
    private Context context;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);

//        FVI
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        recyclerViewNotes = (RecyclerView) findViewById(R.id.recyclerViewNotes);
        //recyclerViewGroups = (RecyclerView) findViewById(R.id.recyclerViewGroups);
        ImageView filterAsList = (ImageView) findViewById(R.id.filterAsList);
        ImageButton addNote = (ImageButton) findViewById(R.id.addNote);

//        Data
        context = NotepadActivity.this;
        activity = NotepadActivity.this;
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.showDialog();
        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
        sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();
        List<Note> notes = new ArrayList<>();

        String groupName;

        if (getIntent().getStringExtra("group") != null) {
            groupName = getIntent().getStringExtra("group");

            List<Long> addTimeList = new ArrayList<>();

//        Pinned notes
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabaseNotes.query(
                    DatabaseConstants._GROUPS_TABLE_NAME,   // The table to query
                    null,             // The array of columns to return (pass null to get all)
                    DatabaseConstants._GROUP_NAME + " = ?",              // The columns for the WHERE clause
                    new String[]{groupName},          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    //"DATE_FORMAT("+new SimpleDateFormat("")+", '%m%d')"               // The sort order
                    DatabaseConstants._ADD_GROUP_TIME + " DESC"
            );

            while (cursor.moveToNext()) {
//            Read data
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
                int color = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_COLOR));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._GROUP_NAME));
                long addNoteTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants._ADD_NOTE_TIME));
                long addGroupTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants._ADD_GROUP_TIME));

                //Group group = new Group(name, id, color, addGroupTime, addNoteTime);
                addTimeList.add(addNoteTime);
                //groups.add(group);
            }
            cursor.close();

            for (int i = 0; i < addTimeList.size(); i++) {
                @SuppressLint("Recycle") Cursor cursor1 = sqLiteDatabaseNotes.query(
                        DatabaseConstants._NOTES_TABLE_NAME,   // The table to query
                        null,             // The array of columns to return (pass null to get all)
                        DatabaseConstants._ADD_NOTE_TIME + " = ?",              // The columns for the WHERE clause
                        new String[]{Long.toString(addTimeList.get(i))},          // The values for the WHERE clause
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
                            0
                    );
                    notes.add(note);
                }
                cursor1.close();
            }
            Toast.makeText(context, new SimpleDateFormat("dd.MM.yyyy HH.mm", Locale.getDefault()).format(new Date(addTimeList.get(0))), Toast.LENGTH_SHORT).show();
            //Toast.makeText(context, Integer.toString(notes.size()), Toast.LENGTH_SHORT).show();
        } else {
            notes = loadNotes(context);
            //groups = loadGroups(context);
        }

        NoteAdapter noteAdapter = new NoteAdapter(context, activity, notes, 1);
        //GroupAdapter groupAdapter = new GroupAdapter(context, activity, groups, 1);

        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewNotes.setAdapter(noteAdapter);
//        recyclerViewGroups.setHasFixedSize(true);
//        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//        recyclerViewGroups.setAdapter(groupAdapter);
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
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(NotepadActivity.this, R.style.BottomSheetTheme);
                bottomSheetDialog.setContentView(R.layout.add_note_layout);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setDismissWithAnimation(true);

                ImageButton addOpacity = (ImageButton) bottomSheetDialog.findViewById(R.id.noteOpacity);
                ImageButton addColor = (ImageButton) bottomSheetDialog.findViewById(R.id.noteColor);
                ImageButton btnDone = (ImageButton) bottomSheetDialog.findViewById(R.id.btnDone1);
                EditText tvTitle = (EditText) bottomSheetDialog.findViewById(R.id.Title);
                EditText tvText = (EditText) bottomSheetDialog.findViewById(R.id.Text);
                CardView cardViewBtnDone = (CardView) bottomSheetDialog.findViewById(R.id.cardBtnDone1);
                //RecyclerView recyclerGroups = (RecyclerView) bottomSheetDialog.findViewById(R.id.recyclerGroups);

                //List<Group> groups = loadGroups(context);
//                GroupAdapter groupAdapter = new GroupAdapter(context, NotepadActivity.this, groups, 0);
//                assert recyclerGroups != null;
//                recyclerGroups.setHasFixedSize(true);
//                recyclerGroups.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                recyclerGroups.setAdapter(groupAdapter);

                Objects.requireNonNull(cardViewBtnDone).setCardBackgroundColor(color);

                bottomSheetDialog.show();

                //Clickers

                //Color
                Objects.requireNonNull(addColor).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(NotepadActivity.this, R.style.BottomSheetTheme);
                        bottomSheetDialog1.setContentView(R.layout.color_picker);
                        bottomSheetDialog1.setCancelable(true);
                        bottomSheetDialog1.setDismissWithAnimation(true);

                        GridView colorGridView = (GridView) bottomSheetDialog1.findViewById(R.id.colorGridView);

                        ArrayList<Colors> colors = new ArrayList<>();
                        colors.add(new Colors(getResources().getColor(R.color.note_blue)));
                        colors.add(new Colors(getResources().getColor(R.color.note_green)));
                        colors.add(new Colors(getResources().getColor(R.color.note_orange)));
                        colors.add(new Colors(getResources().getColor(R.color.note_pink)));
                        colors.add(new Colors(getResources().getColor(R.color.note_purple)));
                        colors.add(new Colors(getResources().getColor(R.color.note_red)));
                        colors.add(new Colors(getResources().getColor(R.color.note_yellow)));

                        ColorAdapter colorAdapter = new ColorAdapter(NotepadActivity.this, 0, colors);
                        Objects.requireNonNull(colorGridView).setAdapter(colorAdapter);

                        bottomSheetDialog1.show();

                        //Clicker
                        colorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                color = colors.get(position).getColorRes();
                                bottomSheetDialog1.dismiss();

                                cardViewBtnDone.setCardBackgroundColor(color);
                            }
                        });
                    }
                });

                //Opacity
                Objects.requireNonNull(addOpacity).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(NotepadActivity.this, R.style.BottomSheetTheme);
                        bottomSheetDialog1.setContentView(R.layout.color_picker);
                        bottomSheetDialog1.setCancelable(true);
                        bottomSheetDialog1.setDismissWithAnimation(true);

                        GridView colorGridView = (GridView) bottomSheetDialog1.findViewById(R.id.colorGridView);

                        ArrayList<Shape> shapes = new ArrayList<>();
                        shapes.add(new Shape(R.drawable.ic_coffee));
                        shapes.add(new Shape(R.drawable.ic_child_care));
                        shapes.add(new Shape(R.drawable.ic_fitness_center));
                        shapes.add(new Shape(R.drawable.ic_headphones));
                        shapes.add(new Shape(R.drawable.ic_hotel));
                        shapes.add(new Shape(R.drawable.ic_local_shipping));
                        shapes.add(new Shape(R.drawable.ic_perm_phone_msg));
                        shapes.add(new Shape(R.drawable.ic_phishing));
                        shapes.add(new Shape(R.drawable.ic_work));
                        shapes.add(new Shape(R.drawable.ic_work_outline));
                        shapes.add(new Shape(R.drawable.ic_receipt_long));
                        shapes.add(new Shape(R.drawable.ic_rocket_launch));
                        shapes.add(new Shape(R.drawable.ic_school));
                        shapes.add(new Shape(R.drawable.ic_shopping_basket));
                        shapes.add(new Shape(R.drawable.ic_spa));
                        shapes.add(new Shape(R.drawable.ic_square_foot));
                        shapes.add(new Shape(R.drawable.ic_grade));

                        OpacityAdapter shapeAdapter = new OpacityAdapter(NotepadActivity.this, 0, shapes);
                        Objects.requireNonNull(colorGridView).setAdapter(shapeAdapter);

                        bottomSheetDialog1.show();

                        //Clicker
                        colorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                opacityRes = shapes.get(position).getShapeRes();
                                bottomSheetDialog1.dismiss();
                            }
                        });
                    }
                });

                ProgressDialog progressDialog = new ProgressDialog(NotepadActivity.this);

                //Done
                Objects.requireNonNull(btnDone).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.showDialog();
                        String note = Objects.requireNonNull(tvTitle).getText().toString();
                        String text = Objects.requireNonNull(tvText).getText().toString();

//                        SAVE
                        long time = System.currentTimeMillis();
                        saveNote(note, text, opacityRes, color, time);
                        progressDialog.dismiss();
                        bottomSheetDialog.dismiss();
                        //sqLiteDatabaseNotes.close();
                    }
                });

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        UPDATE
                        //reloadRecyclerView(context, activity);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(new Intent(NotepadActivity.this, NotepadActivity.class));
                    }
                });
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

        dialog.dismiss();
    }

    public static List<Note> loadNotes(Context context) {
        List<Note> notes = new ArrayList<>();

        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
        sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();

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
                    0
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
                    0
            );
            notes.add(note);
        }
        cursor1.close();

        return notes;
    }

//    public static List<Group> loadGroups(Context context) {
//        List<Group> groups = new ArrayList<>();
//
////        Pinned notes
//        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabaseNotes.query(
//                DatabaseConstants._GROUPS_TABLE_NAME,   // The table to query
//                null,             // The array of columns to return (pass null to get all)
//                null,              // The columns for the WHERE clause
//                null,          // The values for the WHERE clause
//                null,                   // don't group the rows
//                null,                   // don't filter by row groups
//                //"DATE_FORMAT("+new SimpleDateFormat("")+", '%m%d')"               // The sort order
//                DatabaseConstants._ADD_GROUP_TIME + " DESC"
//        );
//
//        while (cursor.moveToNext()) {
////            Read data
//            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
//            int color = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._NOTE_COLOR));
//            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants._GROUP_NAME));
//            long addNoteTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants._ADD_NOTE_TIME));
//            long addGroupTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants._ADD_GROUP_TIME));
//
//            Group group = new Group(name, id, color, addGroupTime, addNoteTime);
//            groups.add(group);
//        }
//        cursor.close();
//
//        return groups;
//    }

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
                    0
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
                        0
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
                .make(NotepadActivity.this, (ConstraintLayout) findViewById(R.id.layout), "Данные сохранены✅", Snackbar.LENGTH_SHORT)
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.WHITE)
                .show();
    }

//    public void saveGroup(String name, int c) {
//        if (name == null || name.isEmpty())
//            name = "";
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DatabaseConstants._GROUP_NAME, name);
//        contentValues.put(DatabaseConstants._NOTE_COLOR, c);
//        contentValues.put(DatabaseConstants._ADD_GROUP_TIME, System.currentTimeMillis());
//        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, 0);
//        sqLiteDatabaseNotes.insert(DatabaseConstants._GROUPS_TABLE_NAME, null, contentValues);
//
//        Snackbar
//                .make(NotepadActivity.this, (ConstraintLayout) findViewById(R.id.layout), "Данные сохранены✅", Snackbar.LENGTH_SHORT)
//                .setTextColor(Color.parseColor("#000000"))
//                .setBackgroundTint(Color.WHITE)
//                .show();
//    }

    public static void reloadRecyclerView(Context context, Activity activity) {
        //recyclerViewNotes.setVisibility(View.GONE);
        List<Note> notes = loadNotes(context);
        //Collections.reverse(notes);
        NoteAdapter noteAdapter = new NoteAdapter(context, activity, notes, 1);
        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewNotes.setAdapter(noteAdapter);
        recyclerViewNotes.setVisibility(View.VISIBLE);
        isList = true;
    }

//    public static void reloadRecyclerViewGroups(Context context, Activity activity) {
//        recyclerViewNotes.setVisibility(View.GONE);
//        List<Group> groups = loadGroups(context);
//        //Collections.reverse(notes);
//        GroupAdapter groupAdapter = new GroupAdapter(context, activity, groups, 1);
//        recyclerViewGroups.setHasFixedSize(true);
//        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//        recyclerViewGroups.setAdapter(groupAdapter);
//        recyclerViewGroups.setVisibility(View.VISIBLE);
//        isList = true;
//    }
}