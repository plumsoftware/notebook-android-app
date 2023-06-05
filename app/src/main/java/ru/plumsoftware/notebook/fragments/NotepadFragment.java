package ru.plumsoftware.notebook.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.adapters.ColorAdapter;
import ru.plumsoftware.notebook.adapters.NoteAdapter;
import ru.plumsoftware.notebook.adapters.OpacityAdapter;
import ru.plumsoftware.notebook.data.items.Colors;
import ru.plumsoftware.notebook.data.items.Note;
import ru.plumsoftware.notebook.data.items.Shape;
import ru.plumsoftware.notebook.databases.DatabaseConstants;
import ru.plumsoftware.notebook.databases.SQLiteDatabaseManager;

public class NotepadFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static SQLiteDatabase sqLiteDatabaseNotes;
    private static RecyclerView recyclerViewNotes;
    private static boolean isList = true;
    private int
            color,
            opacityRes = R.drawable.ic_coffee;

    public NotepadFragment() {
    }

    public static NotepadFragment newInstance(String param1, String param2) {
        NotepadFragment fragment = new NotepadFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notepad, container, false);

//        FVI
        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        recyclerViewNotes = (RecyclerView) view.findViewById(R.id.recyclerViewNotes);
        ImageView filterAsList = (ImageView) view.findViewById(R.id.filterAsList);
        ImageButton addNote = (ImageButton) view.findViewById(R.id.addNote);
        ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.layout);

//        Blur

//        Data
        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(getContext());
        sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();
        NoteAdapter noteAdapter = new NoteAdapter(getContext(), getActivity(), loadNotes(getContext()), 1);
        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(getContext()));
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
                    NoteAdapter noteAdapter = new NoteAdapter(getContext(), getActivity(), loadNotes(getContext()), 1);
                    recyclerViewNotes.setHasFixedSize(true);
                    recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    recyclerViewNotes.setAdapter(noteAdapter);
                    recyclerViewNotes.setVisibility(View.VISIBLE);

                    filterAsList.setImageResource(R.drawable.ic_table_rows);
                } else {
                    isList = true;

                    recyclerViewNotes.setVisibility(View.GONE);
                    NoteAdapter noteAdapter = new NoteAdapter(getContext(), getActivity(), loadNotes(getContext()), 1);
                    recyclerViewNotes.setHasFixedSize(true);
                    recyclerViewNotes.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerViewNotes.setAdapter(noteAdapter);
                    recyclerViewNotes.setVisibility(View.VISIBLE);

                    filterAsList.setImageResource(R.drawable.ic_baseline_filter_list);
                }
            }
        });

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetTheme);
                bottomSheetDialog.setContentView(R.layout.add_note_layout);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setDismissWithAnimation(true);

                ImageButton addOpacity = (ImageButton) bottomSheetDialog.findViewById(R.id.noteOpacity);
                ImageButton addColor = (ImageButton) bottomSheetDialog.findViewById(R.id.noteColor);
                ImageButton btnDone = (ImageButton) bottomSheetDialog.findViewById(R.id.btnDone1);
                EditText tvTitle = (EditText) bottomSheetDialog.findViewById(R.id.Title);
                EditText tvText = (EditText) bottomSheetDialog.findViewById(R.id.Text);
                CardView cardViewBtnDone = (CardView) bottomSheetDialog.findViewById(R.id.cardBtnDone1);

                Objects.requireNonNull(cardViewBtnDone).setCardBackgroundColor(color);

                bottomSheetDialog.show();

                //Clickers

                //Color
                Objects.requireNonNull(addColor).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(requireContext(), R.style.BottomSheetTheme);
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

                        ColorAdapter colorAdapter = new ColorAdapter(getContext(), 0, colors);
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
                        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(requireContext(), R.style.BottomSheetTheme);
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

                        OpacityAdapter shapeAdapter = new OpacityAdapter(getContext(), 0, shapes);
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

                //Done
                Objects.requireNonNull(btnDone).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String note = Objects.requireNonNull(tvTitle).getText().toString();
                        String text = Objects.requireNonNull(tvText).getText().toString();

//                        SAVE
                        saveNote(note, text, opacityRes, color, view);
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        UPDATE
                        reloadRecyclerView(getContext(), getActivity());
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
                NoteAdapter noteAdapter = new NoteAdapter(getContext(), getActivity(), loadNotesWithCondition(s), 1);
                recyclerViewNotes.setHasFixedSize(true);
                recyclerViewNotes.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewNotes.setAdapter(noteAdapter);
                return false;
            }
        });
        return view;
    }

    public static List<Note> loadNotes(Context context) {
        List<Note> notes = new ArrayList<>();

//        Pinned notes
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabaseNotes.query(
                DatabaseConstants._NOTES_TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DatabaseConstants._IS_PINNED + " = ?",              // The columns for the WHERE clause
                new String[]{"1"},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
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
                null               // The sort order
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
                null               // The sort order
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
                null               // The sort order
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

    public void saveNote(String name, String text, int or, int c, View view) {
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
        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, System.currentTimeMillis());
        sqLiteDatabaseNotes.insert(DatabaseConstants._NOTES_TABLE_NAME, null, contentValues);

        Snackbar
                .make(requireContext(), (ConstraintLayout) view.findViewById(R.id.layout), "Данные сохранены✅", Snackbar.LENGTH_SHORT)
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.WHITE)
                .show();

        reloadRecyclerView(getContext(), getActivity());
    }

    public static void saveNote(String name, String text, int or, int c, int isLiked) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants._NOTE_NAME, name);
        contentValues.put(DatabaseConstants._NOTE_TEXT, text);
        contentValues.put(DatabaseConstants._NOTE_PROMO, or);
        contentValues.put(DatabaseConstants._NOTE_COLOR, c);
        contentValues.put(DatabaseConstants._IS_LIKED, isLiked);
        contentValues.put(DatabaseConstants._IS_PINNED, 0);
        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, System.currentTimeMillis());
        sqLiteDatabaseNotes.insert(DatabaseConstants._NOTES_TABLE_NAME, null, contentValues);
    }

    public static void saveNote(String name, String text, int or, int c, int isLiked, int isPinned) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants._NOTE_NAME, name);
        contentValues.put(DatabaseConstants._NOTE_TEXT, text);
        contentValues.put(DatabaseConstants._NOTE_PROMO, or);
        contentValues.put(DatabaseConstants._NOTE_COLOR, c);
        contentValues.put(DatabaseConstants._IS_LIKED, isLiked);
        contentValues.put(DatabaseConstants._IS_PINNED, isPinned);
        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, System.currentTimeMillis());
        sqLiteDatabaseNotes.insert(DatabaseConstants._NOTES_TABLE_NAME, null, contentValues);
    }

    public static void reloadRecyclerView(Context context, Activity activity) {
        recyclerViewNotes.setVisibility(View.GONE);
        NoteAdapter noteAdapter = new NoteAdapter(context, activity, loadNotes(context), 1);
        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewNotes.setAdapter(noteAdapter);
        recyclerViewNotes.setVisibility(View.VISIBLE);
        isList = true;
    }
}