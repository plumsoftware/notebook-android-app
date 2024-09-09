package ru.plumsoftware.notebook.presentation.activities.note.presenter;

public interface AddNotePresenter {
    void initNote();
    void initMobileSdk();
    void putNote(String name, String text, int or, int c, long time, boolean isNotify);
}
