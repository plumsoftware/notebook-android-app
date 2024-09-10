package ru.plumsoftware.notebook.presentation.activities.main.view;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.plumsoftware.data.model.ui.Note;

public interface MainView {
    void changeFilterButtonImage(@DrawableRes int res);
    void initRecyclerView(List<Note> notes, RecyclerView.LayoutManager layoutManager);

    void showProgressDialog();
    void dismissProgressDialog();
}
