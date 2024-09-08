package ru.plumsoftware.notebook.presentation.presenters.main;

public interface MainPresenter {
    void initMobileSdk();
    void initNotes();
    void initOpenAds();

    void loadData();

    void detachView();
}
