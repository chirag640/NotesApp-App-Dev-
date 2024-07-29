package com.chirag.notesapp.Interface;

import androidx.cardview.widget.CardView;

import com.chirag.notesapp.Model.Notes;

public interface NotesClickListener  {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
