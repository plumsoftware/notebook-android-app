package ru.plumsoftware.notebook.presentation.activities.note.model;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader;

import ru.plumsoftware.data.model.ui.Note;

public class AddNoteModel {
    private Mode mode;
    private final Activity activity;
    private final Context context;
    private Note note;

    private InterstitialAd mInterstitialAd;
    private InterstitialAdLoader mInterstitialAdLoader;

    public AddNoteModel(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;

        mode = Mode.New;
    }

    public InterstitialAd getmInterstitialAd() {
        return mInterstitialAd;
    }

    public InterstitialAdLoader getmInterstitialAdLoader() {
        return mInterstitialAdLoader;
    }

    public void setmInterstitialAd(InterstitialAd mInterstitialAd) {
        this.mInterstitialAd = mInterstitialAd;
    }

    public void setmInterstitialAdLoader(InterstitialAdLoader mInterstitialAdLoader) {
        this.mInterstitialAdLoader = mInterstitialAdLoader;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public Activity getActivity() {
        return activity;
    }

    public Context getContext() {
        return context;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }
}
