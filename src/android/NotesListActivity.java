package com.example.notesplugin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class NotesListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a vertical LinearLayout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create a ListView for displaying notes
        ListView notesListView = new ListView(this);
        notesListView.setId(1); // Assign an ID to the ListView
        LinearLayout.LayoutParams listViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1 // Weight to occupy remaining space
        );
        layout.addView(notesListView, listViewParams);

        // Create a Button for creating a new note
        Button createNoteButton = new Button(this);
        createNoteButton.setText("Create Note");
        createNoteButton.setOnClickListener(view -> {
            // Start NoteEditorActivity
            startActivity(new android.content.Intent(NotesListActivity.this, NoteEditorActivity.class));
        });
        layout.addView(createNoteButton);

        // Set the layout as the content view
        setContentView(layout);
    }
}

