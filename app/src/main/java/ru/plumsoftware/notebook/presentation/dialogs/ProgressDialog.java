package ru.plumsoftware.notebook.presentation.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import ru.plumsoftware.notebook.R;

public class ProgressDialog {
    private Dialog dialog;
    private Context context;
    private int theme;

    public ProgressDialog(Context context) {
        this.context = context;
    }

    public ProgressDialog(Context context, int theme) {
        this.context = context;
        this.theme = theme;
    }

    public void showDialog() {
        try {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null, false);
            dialog = new Dialog(context, theme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(view);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }
}
