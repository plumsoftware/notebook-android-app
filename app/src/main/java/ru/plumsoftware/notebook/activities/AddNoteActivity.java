package ru.plumsoftware.notebook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ru.plumsoftware.notebook.dialogs.ProgressDialog;
import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.adapters.ColorAdapter;
import ru.plumsoftware.notebook.adapters.GroupAdapter;
import ru.plumsoftware.notebook.adapters.OpacityAdapter;
import ru.plumsoftware.notebook.data.items.Colors;
import ru.plumsoftware.notebook.data.items.Group;
import ru.plumsoftware.notebook.data.items.Note;
import ru.plumsoftware.notebook.data.items.Shape;
import ru.plumsoftware.notebook.databases.DatabaseConstants;
import ru.plumsoftware.notebook.databases.SQLiteDatabaseManager;

public class AddNoteActivity extends AppCompatActivity {

    private int
            color = 0xFFFFFF,
            opacityRes = R.drawable.ic_coffee;
    private long noteTime = 0L;
    private CardView cardViewBtnDone;
    private SQLiteDatabase sqLiteDatabaseNotes;
    private InterstitialAd interstitialAd;
    private AdRequest adRequest;
    private ProgressDialog progressDialog;

    private Calendar dateAndTime = Calendar.getInstance();
    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        EditText tvTitle = (EditText) findViewById(R.id.Title);
        EditText tvText = (EditText) findViewById(R.id.Text);
        cardViewBtnDone = (CardView) findViewById(R.id.cardBtnDoneUltra);
        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(this);
        sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();
        noteTime = System.currentTimeMillis();
        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);

        Note note = getIntent().getParcelableExtra("note");
        boolean isUpdate = getIntent().getBooleanExtra("update", false);

        if (isUpdate) {
            toolbar.setTitle("Редактировать заметку");
            toolbar.setSubtitle(new SimpleDateFormat("dd.MM.yyyy HH.mm", Locale.getDefault()).format(new Date(note.getAddNoteTime())));

//            Setup note data
            color = note.getColor();
            opacityRes = note.getOpacity();

            cardViewBtnDone.setCardBackgroundColor(color);
            tvTitle.setText(note.getNoteName());
            tvText.setText(note.getNoteText());
        } else {
            toolbar.setTitle("Добавить заметку");
            toolbar.setSubtitle(new SimpleDateFormat("dd.MM.yyyy HH.mm", Locale.getDefault()).format(new Date(noteTime)));
        }

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("R-M-1957919-2");
//        interstitialAd.setAdUnitId("R-M-1737730-2");
        adRequest = new AdRequest.Builder().build();
        interstitialAd.setInterstitialAdEventListener(new InterstitialAdEventListener() {
            @Override
            public void onAdLoaded() {
                progressDialog.dismiss();
                interstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
                progressDialog.dismiss();
                Toast.makeText(AddNoteActivity.this, adRequestError.getDescription(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddNoteActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onAdShown() {

            }

            @Override
            public void onAdDismissed() {
                startActivity(new Intent(AddNoteActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onLeftApplication() {

            }

            @Override
            public void onReturnedToApplication() {

            }

            @Override
            public void onImpression(@Nullable ImpressionData impressionData) {

            }
        });

        cardViewBtnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteTitle = Objects.requireNonNull(tvTitle).getText().toString();
                String text = Objects.requireNonNull(tvText).getText().toString();

//                SAVE
                if (isUpdate) {
                    note.setNoteName(noteTitle);
                    note.setNoteText(text);
                    note.setColor(color);
                    note.setOpacity(opacityRes);
                    note.setAddNoteTime(noteTime);
                    updateNote(note);
                    onBackPressed();
                } else {
                    saveNote(noteTitle, text, opacityRes, color, noteTime);
                    onBackPressed();
                }

                List<Group> groupList = GroupAdapter.addedGroups;

                for (int i = 0; i < groupList.size(); i++) {
                    String name = groupList.get(i).getName();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseConstants._GROUP_NAME, name);
                    contentValues.put(DatabaseConstants._NOTE_COLOR, color);
                    contentValues.put(DatabaseConstants._ADD_NOTE_TIME, noteTime);
                    contentValues.put(DatabaseConstants._ADD_GROUP_TIME, System.currentTimeMillis());
                    sqLiteDatabaseNotes.update(DatabaseConstants._GROUPS_TABLE_NAME, contentValues, DatabaseConstants._GROUP_NAME + " = ?", new String[]{name});
//                    try {
//                        NotepadActivity.reloadRecyclerView(AddNoteActivity.this, AddNoteActivity.this);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
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
                .make(this, (LinearLayout) findViewById(R.id.layout), "Данные сохранены✅", Snackbar.LENGTH_SHORT)
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.WHITE)
                .show();
    }

    private void updateNote(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants._NOTE_NAME, note.getNoteName());
        contentValues.put(DatabaseConstants._NOTE_TEXT, note.getNoteText());
        contentValues.put(DatabaseConstants._NOTE_PROMO, note.getNotePromoResId());
        contentValues.put(DatabaseConstants._NOTE_COLOR, note.getColor());
        contentValues.put(DatabaseConstants._IS_LIKED, note.getIsLiked());
        contentValues.put(DatabaseConstants._IS_PINNED, note.getIsPinned());
        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, note.getAddNoteTime());
        sqLiteDatabaseNotes.update(DatabaseConstants._NOTES_TABLE_NAME, contentValues, DatabaseConstants._ADD_NOTE_TIME + " = ?", new String[]{Long.toString(note.getAddNoteTime())});
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(this, R.style.BottomSheetTheme);
        bottomSheetDialog1.setContentView(R.layout.color_picker);
        bottomSheetDialog1.setCancelable(true);
        bottomSheetDialog1.setDismissWithAnimation(true);
        GridView colorGridView = (GridView) bottomSheetDialog1.findViewById(R.id.colorGridView);

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.pick_time:

                new DatePickerDialog(AddNoteActivity.this, d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();

                return true;
            case R.id.small_icon:
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

                OpacityAdapter shapeAdapter = new OpacityAdapter(this, 0, shapes);
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
                return true;
            case R.id.small_color:
                ArrayList<Colors> colors = new ArrayList<>();
                colors.add(new Colors(getResources().getColor(R.color.note_blue)));
                colors.add(new Colors(getResources().getColor(R.color.note_green)));
                colors.add(new Colors(getResources().getColor(R.color.note_orange)));
                colors.add(new Colors(getResources().getColor(R.color.note_pink)));
                colors.add(new Colors(getResources().getColor(R.color.note_purple)));
                colors.add(new Colors(getResources().getColor(R.color.note_red)));
                colors.add(new Colors(getResources().getColor(R.color.note_yellow)));

                ColorAdapter colorAdapter = new ColorAdapter(this, 0, colors);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        progressDialog.showDialog();
        interstitialAd.loadAd(adRequest);
    }

    private void setDateAndTime() {
        noteTime = dateAndTime.getTimeInMillis();
        toolbar.setSubtitle(new SimpleDateFormat("dd.MM.yyyy HH.mm", Locale.getDefault()).format(new Date(noteTime)));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setDateAndTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDateAndTime();

            new TimePickerDialog(AddNoteActivity.this, t,
                    dateAndTime.get(Calendar.HOUR_OF_DAY),
                    dateAndTime.get(Calendar.MINUTE), true)
                    .show();
        }
    };
}