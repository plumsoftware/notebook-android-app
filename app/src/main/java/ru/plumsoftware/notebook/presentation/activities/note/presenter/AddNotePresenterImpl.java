package ru.plumsoftware.notebook.presentation.activities.note.presenter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.plumsoftware.data.database.SQLiteDatabaseManager;
import ru.plumsoftware.data.model.database.DatabaseConstants;
import ru.plumsoftware.data.model.ui.Note;
import ru.plumsoftware.notebook.manager.ads.AdsIds;
import ru.plumsoftware.notebook.manager.extra.ExtraNames;
import ru.plumsoftware.notebook.manager.unique.UniqueIdGenerator;
import ru.plumsoftware.notebook.presentation.activities.note.model.AddNoteModel;
import ru.plumsoftware.notebook.presentation.activities.note.model.Mode;
import ru.plumsoftware.notebook.presentation.activities.note.view.AddNoteView;

public class AddNotePresenterImpl implements AddNotePresenter {

    private final AddNoteView view;
    private final AddNoteModel addNoteModel;
    private SQLiteDatabase sqLiteDatabaseNotes;

    public AddNotePresenterImpl(AddNoteView view, Context context, Activity activity) {
        this.view = view;

        addNoteModel = new AddNoteModel(
                activity,
                context
        );
    }

    @Override
    public void initNote() {

        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(addNoteModel.getContext());
        sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();

        String title;
        String textOnButton;
        String time;

        if (addNoteModel.getActivity().getIntent().getBooleanExtra(ExtraNames.AddNoteActivity.update, false)) {
            addNoteModel.setMode(Mode.Edit);
            title = "Редактировать заметку";
            textOnButton = "РЕДАКТИРОВАТЬ";
            time = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(addNoteModel.getNote().getAddNoteTime()));
        } else {
            addNoteModel.setMode(Mode.New);
            title = "Добавить заметку";
            textOnButton = "СОХРАНИТЬ";
            time = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
        }
        view.initToolbarTitle(title, textOnButton, time);

        addNoteModel.setNote(addNoteModel.getActivity().getIntent().getParcelableExtra(ExtraNames.AddNoteActivity.note));
        if (addNoteModel.getNote() != null) {
            view.initNote(addNoteModel.getNote());
        }
    }

    @Override
    public void initMobileSdk() {
        MobileAds.initialize(addNoteModel.getContext(), () -> {
        });
    }

    @Override
    public void putNote(String name, String text, int or, int c, long time, boolean isNotify) {
        if (addNoteModel.getMode() == Mode.New) {
            saveNote(name, text, or, c, time, isNotify);
        } else if (addNoteModel.getMode() == Mode.Edit) {
            updateNote(name, text, or, c, time, isNotify);
        }
    }

    @Override
    public void initInterstitialAds() {
        InterstitialAdLoader mInterstitialAdLoader = new InterstitialAdLoader(addNoteModel.getContext());
        mInterstitialAdLoader.setAdLoadListener(new InterstitialAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final InterstitialAd interstitialAd) {
                view.dismissProgressDialog();
                interstitialAd.setAdEventListener(new InterstitialAdEventListener() {
                    @Override
                    public void onAdShown() {

                    }

                    @Override
                    public void onAdFailedToShow(@NonNull AdError adError) {

                    }

                    @Override
                    public void onAdDismissed() {
                        addNoteModel.getActivity().finish();
                        addNoteModel.getActivity().overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onAdClicked() {
                        addNoteModel.getActivity().finish();
                        addNoteModel.getActivity().overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onAdImpression(@Nullable ImpressionData impressionData) {

                    }
                });
                addNoteModel.setmInterstitialAd(interstitialAd);
                interstitialAd.show(addNoteModel.getActivity());
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                Toast.makeText(addNoteModel.getContext(), adRequestError.getDescription(), Toast.LENGTH_SHORT).show();
                addNoteModel.getActivity().finish();
                addNoteModel.getActivity().overridePendingTransition(0, 0);
            }
        });

        addNoteModel.setmInterstitialAdLoader(mInterstitialAdLoader);
    }

    @Override
    public void showInterstitialAd() {
        view.showProgressDialog();
        final AdRequestConfiguration adRequestConfiguration =
                new AdRequestConfiguration.Builder(AdsIds.INTERSTITIAL_AD_UNIT_ID).build();
        addNoteModel.getmInterstitialAdLoader().loadAd(adRequestConfiguration);
    }

    private void updateNote(String name, String text, int or, int c, long time, boolean isNotify) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants._NOTE_NAME, name);
        contentValues.put(DatabaseConstants._NOTE_TEXT, text);
        contentValues.put(DatabaseConstants._NOTE_PROMO, or);
        contentValues.put(DatabaseConstants._NOTE_COLOR, c);
        contentValues.put(DatabaseConstants._IS_LIKED, 0);
        contentValues.put(DatabaseConstants._IS_PINNED, 0);
        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, time);
        contentValues.put(DatabaseConstants._IS_NOTIFY, isNotify);
        contentValues.put(DatabaseConstants._CHANNEL_ID, addNoteModel.getNote().getNotificationChannelId());
        sqLiteDatabaseNotes.update(DatabaseConstants._NOTES_TABLE_NAME, contentValues, DatabaseConstants._ID + " = ?", new String[]{String.valueOf(addNoteModel.getNote().getId())});
    }

    private void saveNote(String name, String text, int or, int c, long time, boolean isNotify) {
        if (name == null || name.isEmpty())
            name = "";
        if (text == null || text.isEmpty())
            text = "";

        String notificationChannelId = UniqueIdGenerator.generateUniqueId();

        int isNotifyInt;
        if (isNotify) {
            isNotifyInt = 1;
        } else {
            isNotifyInt = 0;
        }

        Note note = new Note(
                0,
                0,
                or,
                0,
                0,
                c,
                name,
                text,
                time,
                0,
                notificationChannelId,
                isNotifyInt
        );

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants._NOTE_NAME, name);
        contentValues.put(DatabaseConstants._NOTE_TEXT, text);
        contentValues.put(DatabaseConstants._NOTE_PROMO, or);
        contentValues.put(DatabaseConstants._NOTE_COLOR, c);
        contentValues.put(DatabaseConstants._IS_LIKED, 0);
        contentValues.put(DatabaseConstants._IS_PINNED, 0);
        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, time);
        contentValues.put(DatabaseConstants._IS_NOTIFY, isNotifyInt);
        contentValues.put(DatabaseConstants._CHANNEL_ID, notificationChannelId);
        sqLiteDatabaseNotes.insert(DatabaseConstants._NOTES_TABLE_NAME, null, contentValues);
        view.showSnackBar();
    }
}
