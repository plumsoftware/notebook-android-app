package ru.plumsoftware.notebook.presentation.activities.note.model;

import android.app.Activity;
import android.content.Context;

import ru.plumsoftware.data.model.ui.Note;

public class AddNoteModel {
    private Mode mode;
    private final Activity activity;
    private final Context context;
    private Note note;

    public AddNoteModel(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;

        mode = Mode.New;
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
