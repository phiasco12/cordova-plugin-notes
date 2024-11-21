package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

public class NotesListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        // Setup the list view for notes
        ListView notesListView = findViewById(R.id.notesListView);

        // Load saved notes into the list
        // (Implementation for loading notes goes here)

        // Setup button to create a new note
        Button createNoteButton = findViewById(R.id.createNoteButton);
        createNoteButton.setOnClickListener(view -> {
            Intent intent = new Intent(NotesListActivity.this, NoteEditorActivity.class);
            startActivity(intent);
        });
    }
}
