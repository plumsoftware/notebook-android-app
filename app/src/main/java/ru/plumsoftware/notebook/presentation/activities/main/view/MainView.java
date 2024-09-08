package ru.plumsoftware.notebook.presentation.activities.main.view;

import java.util.List;

import ru.plumsoftware.data.model.ui.Note;

public interface MainView {
    void showNotes(List<Note> noteList);

    void showLoading();

    void hideLoading();
}
