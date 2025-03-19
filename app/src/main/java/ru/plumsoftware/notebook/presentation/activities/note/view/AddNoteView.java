package ru.plumsoftware.notebook.presentation.activities.note.view;

import ru.plumsoftware.data.model.ui.Note;

public interface AddNoteView {
    void initNote(Note note);
    void initToolbarTitle(String title, String textOnButton, String time);
    void showSnackBar();
    void showProgressDialog();
    void dismissProgressDialog();
}
