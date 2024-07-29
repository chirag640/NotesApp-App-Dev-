package com.chirag.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chirag.notesapp.Database.RoomDb;
import com.chirag.notesapp.Interface.NotesClickListener;
import com.chirag.notesapp.Model.Notes;
import com.chirag.notesapp.adapter.NoteListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;
    RoomDb database;
    List<Notes> notes = new ArrayList<>();
    Notes selectedNotes;
    FloatingActionButton fabBtn;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.noteRv);
        fabBtn = findViewById(R.id.addBtn);
        searchView = findViewById(R.id.searchView);
        database = RoomDb.getInstance(this);
        notes = database.mainDAO().getAll();
        updateRecycler(notes);

        fabBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NoteTakeActivity.class);
            startActivityForResult(intent, 101);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Notes newNotes = (Notes) data.getSerializableExtra("note");
            if (newNotes != null) {
                if (requestCode == 101) {
                    database.mainDAO().insert(newNotes);
                } else if (requestCode == 102) {
                    database.mainDAO().update(newNotes.getId(), newNotes.getTitle(), newNotes.getNote());
                }
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteListAdapter = new NoteListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(noteListAdapter);
    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase()) || singleNote.getNote().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        noteListAdapter.filterList(filteredList);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NoteTakeActivity.class);
            intent.putExtra("old_data", notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNotes = notes;
            showPop(cardView);
        }
    };

    private void showPop(CardView cardView) {
        PopupMenu popUpMenu = new PopupMenu(this, cardView);
        popUpMenu.setOnMenuItemClickListener(this);
        popUpMenu.inflate(R.menu.popup_menu);
        popUpMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.pin) {
            if (selectedNotes.isPinned()) {
                database.mainDAO().pin(selectedNotes.getId(), false);
                Toast.makeText(this, "Unpinned", Toast.LENGTH_SHORT).show();
            } else {
                database.mainDAO().pin(selectedNotes.getId(), true);
                Toast.makeText(this, "Pinned", Toast.LENGTH_SHORT).show();
            }
            notes.clear();
            notes.addAll(database.mainDAO().getAll());
            noteListAdapter.notifyDataSetChanged();
            return true;
        } else if (item.getItemId() == R.id.delete) {
            database.mainDAO().delete(selectedNotes);
            notes.remove(selectedNotes);
            noteListAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            return true;

        }

        return false;
    }
}
