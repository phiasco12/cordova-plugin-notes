/*package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NotesListActivity extends Activity {

    private LinearLayout notesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Main layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // Create note button
        Button createNoteButton = new Button(this);
        createNoteButton.setText("Create Note");
        createNoteButton.setOnClickListener(v -> openNoteEditor());
        mainLayout.addView(createNoteButton);

        // Notes container
        notesContainer = new LinearLayout(this);
        notesContainer.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(notesContainer);

        // Set the layout as the content view
        setContentView(mainLayout);
    }

    // Open the NoteEditorActivity
    private void openNoteEditor() {
        Intent intent = new Intent(NotesListActivity.this, NoteEditorActivity.class);
        startActivityForResult(intent, 1);
    }

    // Handle saved notes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            byte[] noteImageBytes = data.getByteArrayExtra("noteImage");
            Bitmap noteBitmap = BitmapFactory.decodeByteArray(noteImageBytes, 0, noteImageBytes.length);
            displayNote(noteBitmap);
        }
    }

    // Display saved notes
    private void displayNote(Bitmap noteBitmap) {
        ImageView noteView = new ImageView(this);
        noteView.setImageBitmap(noteBitmap);
        noteView.setAdjustViewBounds(true);
        notesContainer.addView(noteView);
    }
}*/





package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;

public class NotesListActivity extends Activity {

    private GridLayout notesGrid; // Container for notes in a grid layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Main layout
        GridLayout mainLayout = new GridLayout(this);
        mainLayout.setColumnCount(1); // Single column for the create note button

        // Create note button
        Button createNoteButton = new Button(this);
        createNoteButton.setText("Create Note");
        createNoteButton.setOnClickListener(v -> openNoteEditor());
        mainLayout.addView(createNoteButton);

        // Notes grid layout
        notesGrid = new GridLayout(this);
        notesGrid.setColumnCount(2); // 2 columns in the grid
        notesGrid.setRowCount(GridLayout.UNDEFINED); // Allow unlimited rows
        notesGrid.setPadding(20, 20, 20, 20); // Padding for the grid
        notesGrid.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        mainLayout.addView(notesGrid); // Add the notes grid to the main layout

        // Set the layout as the content view
        setContentView(mainLayout);
    }

    // Open the NoteEditorActivity
    private void openNoteEditor() {
        Intent intent = new Intent(NotesListActivity.this, NoteEditorActivity.class);
        startActivityForResult(intent, 1);
    }

    // Handle saved notes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            byte[] noteImageBytes = data.getByteArrayExtra("noteImage");
            Bitmap noteBitmap = BitmapFactory.decodeByteArray(noteImageBytes, 0, noteImageBytes.length);
            addNoteToGrid(noteBitmap);
        }
    }

    // Add a note to the grid layout
    private void addNoteToGrid(Bitmap noteBitmap) {
        ImageView noteView = new ImageView(this);
        noteView.setImageBitmap(noteBitmap);
        noteView.setAdjustViewBounds(true);

        // Set rounded corners for the note view
        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(Color.BLACK); // Background color for the note
        roundedBackground.setCornerRadius(50); // Rounded corners
        noteView.setBackground(roundedBackground);

        // Set margins for the note view
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(20, 20, 20, 20); // Margins between notes
        params.width = 400; // Fixed width for each note
        params.height = 400; // Fixed height for each note
        noteView.setLayoutParams(params);

        // Add the note view to the grid
        notesGrid.addView(noteView);
    }
}

