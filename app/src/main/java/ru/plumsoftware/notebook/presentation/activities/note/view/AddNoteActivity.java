package ru.plumsoftware.notebook.presentation.activities.note.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ru.plumsoftware.data.model.ui.Colors;
import ru.plumsoftware.data.model.ui.Note;
import ru.plumsoftware.data.model.ui.Shape;
import ru.plumsoftware.notebook.presentation.activities.note.presenter.AddNotePresenter;
import ru.plumsoftware.notebook.presentation.activities.note.presenter.AddNotePresenterImpl;
import ru.plumsoftware.notebook.presentation.dialogs.ProgressDialog;
import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.presentation.adapters.ColorAdapter;
import ru.plumsoftware.notebook.presentation.adapters.OpacityAdapter;
import ru.plumsoftware.notebook.services.scheduler.NotificationScheduler;
import ru.plumsoftware.notebook.manager.unique.UniqueIdGenerator;

public class AddNoteActivity extends AppCompatActivity implements AddNoteView {

    private int REQUEST_CODE = 100;
    private String notificationChannelId = "";

    private int
            color,
            opacityRes = R.drawable.ic_coffee;

    private boolean isLoadIntAds = true;
    private SQLiteDatabase sqLiteDatabaseNotes;
    @Nullable
    private InterstitialAd mInterstitialAd = null;
    @Nullable
    private InterstitialAdLoader mInterstitialAdLoader = null;
    private ProgressDialog progressDialog;

    private Calendar dateAndTime = Calendar.getInstance();

    private long noteTime = 0L;

    private Toolbar toolbar;
    private CardView cardViewBtnDone;
    private CheckBox checkBox;
    private TextView textViewOnButton;
    private EditText tvTitle;
    private EditText tvText;

    private AddNotePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Context context = AddNoteActivity.this;
        Activity activity = AddNoteActivity.this;
        presenter = new AddNotePresenterImpl(this, context, activity);
        color = getResources().getColor(R.color.note_green);

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        tvTitle = findViewById(R.id.Title);
        tvText = findViewById(R.id.Text);
        checkBox = findViewById(R.id.checkBox);
        cardViewBtnDone = findViewById(R.id.cardBtnDoneUltra);
        textViewOnButton = findViewById(R.id.textView5);
        progressDialog = new ProgressDialog(context, R.style.CustomProgressDialog);

        presenter.initMobileSdk();
        presenter.initNote();

        mInterstitialAdLoader = new InterstitialAdLoader(this);
        mInterstitialAdLoader.setAdLoadListener(new InterstitialAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                progressDialog.dismiss();
                if (mInterstitialAd != null) {
                    mInterstitialAd.setAdEventListener(new InterstitialAdEventListener() {
                        @Override
                        public void onAdShown() {

                        }

                        @Override
                        public void onAdFailedToShow(@NonNull AdError adError) {

                        }

                        @Override
                        public void onAdDismissed() {
                            finish();
                            overridePendingTransition(0, 0);
                        }

                        @Override
                        public void onAdClicked() {
                            finish();
                            overridePendingTransition(0, 0);
                        }

                        @Override
                        public void onAdImpression(@Nullable ImpressionData impressionData) {

                        }
                    });
                    mInterstitialAd.show(AddNoteActivity.this);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                Toast.makeText(AddNoteActivity.this, adRequestError.getDescription(), Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
            }
        });
        cardViewBtnDone.setOnClickListener(view -> {
            String noteTitle = Objects.requireNonNull(tvTitle).getText().toString();
            String text = Objects.requireNonNull(tvText).getText().toString();

            presenter.putNote(
                    noteTitle,
                    text,
                    opacityRes,
                    color,
                    noteTime,
                    checkBox.isChecked()
            );
            onBackPressed();

            if (checkBox.isChecked()) {
                if (!notificationChannelId.isEmpty()) {
                    setAlarmManager(noteTime, noteTitle, color, notificationChannelId);
                } else {
                    notificationChannelId = UniqueIdGenerator.generateUniqueId();
                    setAlarmManager(noteTime, noteTitle, color, notificationChannelId);
                }
            }
        });
        checkBox.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(AddNoteActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions((Activity) AddNoteActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
                }
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (mInterstitialAdLoader != null) {
            progressDialog.showDialog();
            final AdRequestConfiguration adRequestConfiguration =
                    new AdRequestConfiguration.Builder("R-M-1957919-2").build();
            mInterstitialAdLoader.loadAd(adRequestConfiguration);
        }
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

    //Alarm
    @SuppressLint("ScheduleExactAlarm")
    private void setAlarmManager(Long timeInMillis, String message, int color, String notificationChannelId) {
        NotificationScheduler.scheduleNotification(AddNoteActivity.this, timeInMillis, message, color, notificationChannelId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initNote(@NonNull Note note) {
        opacityRes = note.getOpacity();
        notificationChannelId = note.getNotificationChannelId();
        checkBox.setChecked(note.getIsNotify() == 1);

        cardViewBtnDone.setCardBackgroundColor(note.getColor());
        tvTitle.setText(note.getNoteName());
        tvText.setText(note.getNoteText());
    }

    @Override
    public void initToolbarTitle(String title, String textOnButton, String time) {
        toolbar.setTitle(title);
        toolbar.setSubtitle(time);
        textViewOnButton.setText(textOnButton);
    }

    @Override
    public void showSnackBar() {
        Snackbar
                .make(AddNoteActivity.this, findViewById(R.id.layout), "Данные сохранены✅", Snackbar.LENGTH_SHORT)
                .setTextColor(Color.parseColor("#000000"))
                .setBackgroundTint(Color.WHITE)
                .show();
    }
}