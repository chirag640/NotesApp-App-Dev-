package com.chirag.notesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.chirag.notesapp.Model.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteTakeActivity extends AppCompatActivity {

    EditText titleEdt, noteEdt;
    ImageView saveBtn;
    Notes notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_note_take);

        titleEdt = findViewById(R.id.titleEdt);
        noteEdt = findViewById(R.id.noteEdt);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(view -> {
            notes = new Notes();
            String title = titleEdt.getText().toString();
            String description = noteEdt.getText().toString();
            if (description.isEmpty()) {
                Toast.makeText(NoteTakeActivity.this, "Please Enter The Description", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
            Date date = new Date();

            notes.setTitle(title);
            notes.setNote(description);
            notes.setDate(format.format(date));

            Intent intent = new Intent();
            intent.putExtra("note", notes);
            setResult(RESULT_OK, intent);
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
