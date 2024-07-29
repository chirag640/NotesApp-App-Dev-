package com.chirag.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;
    RoomDb database;
    List<Notes> notes = new ArrayList<>();
   FloatingActionButton fabBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

         recyclerView = findViewById(R.id.noteRv);
         fabBtn = findViewById(R.id.addBtn);
        database = RoomDb.getInstance(this);
        notes = database.mainDAO().getAll();
        updateRecycler(notes);

        fabBtn.setOnClickListener(view -> {
Intent intent = new Intent(MainActivity.this, NoteTakeActivity.class);
startActivityForResult(intent , 101);
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
        if(requestCode == 101 && resultCode == RESULT_OK){
            Notes new_notes = (Notes) data.getSerializableExtra("note");
            if (new_notes != null) {
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                noteListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Notes> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteListAdapter = new NoteListAdapter(MainActivity.this, notes ,notesClickListener);
        recyclerView.setAdapter(noteListAdapter);
    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {

        }
    };
 }