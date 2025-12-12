package ru.plumsoftware.notebook.presentation.activities.main.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.List;

import ru.plumsoftware.data.model.ui.Note;
import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.presentation.activities.main.presenter.Conditions;
import ru.plumsoftware.notebook.presentation.activities.main.presenter.MainPresenterImpl;
import ru.plumsoftware.notebook.presentation.adapters.NoteAdapter;
import ru.plumsoftware.notebook.presentation.activities.main.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainView {

    private MainPresenter presenter;

    private Context context;
    private Activity activity;

    private ImageView filterAsList;
    private RecyclerView recyclerViewNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Base variables
        context = MainActivity.this;
        activity = MainActivity.this;

        presenter = new MainPresenterImpl(context, activity, this);

//        Find views by id
        SearchView searchView = findViewById(R.id.searchView);
        filterAsList = findViewById(R.id.filterAsList);
        recyclerViewNotes = activity.findViewById(R.id.recyclerViewNotes);
        ImageButton addNote = findViewById(R.id.addNote);

//        Clickers
        filterAsList.setOnClickListener(view -> presenter.changeListStyle());
        addNote.setOnClickListener(view -> presenter.openAddNoteActivity());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!s.isEmpty())
                    presenter.initNotes(new Conditions.Search(s));
                else
                    presenter.initNotes(new Conditions.All());
                return false;
            }
        });

//        load ad
        presenter.initMobileSdk();
        presenter.initOpenAds();

//        Load notes
        presenter.initNotes(new Conditions.All());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void changeFilterButtonImage(int res) {
        filterAsList.setImageResource(res);
    }

    @Override
    public void initRecyclerView(List<Note> notes, RecyclerView.LayoutManager layoutManager) {
        NoteAdapter noteAdapter = new NoteAdapter(context, activity, notes, 1);

        recyclerViewNotes.setVisibility(View.GONE);
        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(layoutManager);
        recyclerViewNotes.setAdapter(noteAdapter);
        recyclerViewNotes.setVisibility(View.VISIBLE);
    }
}