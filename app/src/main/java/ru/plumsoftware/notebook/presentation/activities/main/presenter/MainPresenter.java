package ru.plumsoftware.notebook.presentation.activities.main.presenter;

public interface MainPresenter {
    void initMobileSdk();
    void initNotes(Conditions conditions);
    void initOpenAds();

    void changeListStyle();
    void openAddNoteActivity();
}
