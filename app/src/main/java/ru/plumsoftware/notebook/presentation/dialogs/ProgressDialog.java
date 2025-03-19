package ru.plumsoftware.notebook.presentation.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import ru.plumsoftware.notebook.R;

public class ProgressDialog {
    private Dialog dialog;
    private Context context;
    private int theme;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public ProgressDialog(Context context) {
        this.context = context;
    }

    public ProgressDialog(Context context, int theme) {
        this.context = context;
        this.theme = theme;
    }

    public void showDialog() {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {

                return;
            }
        }

        mainThreadHandler.post(() -> {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog,null, true);
            dialog = new Dialog(context, theme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(view);
            try {
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void dismiss() {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return;
            }
        }

        mainThreadHandler.post(() -> {
            if (dialog != null && dialog.isShowing()) {
                try {
                    dialog.dismiss();
                } catch (final IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
