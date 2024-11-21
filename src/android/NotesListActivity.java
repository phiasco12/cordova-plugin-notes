package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class NotesListActivity extends Activity {

    private static final int CREATE_NOTE_REQUEST = 1;
    private LinearLayout notesContainer; // Container to display saved notes
    private ArrayList<Bitmap> savedNotes; // List to store notes as Bitmaps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the main layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // Create a button to create a new note
        Button createNoteButton = new Button(this);
        createNoteButton.setText("Create Note");
        createNoteButton.setOnClickListener(v -> openNoteEditor());
        mainLayout.addView(createNoteButton);

        // Create a container to display saved notes
        notesContainer = new LinearLayout(this);
        notesContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mainLayout.addView(notesContainer, containerParams);

        // Set the main layout as the content view
        setContentView(mainLayout);

        // Initialize the list of saved notes
        savedNotes = new ArrayList<>();
    }

    // Open the NoteEditorActivity to create a new note
    private void openNoteEditor() {
        Intent intent = new Intent(NotesListActivity.this, NoteEditorActivity.class);
        startActivityForResult(intent, CREATE_NOTE_REQUEST);
    }

    // Handle the result from NoteEditorActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_NOTE_REQUEST && resultCode == RESULT_OK) {
            // Retrieve the note image as a byte array
            byte[] noteImageBytes = data.getByteArrayExtra("noteImage");

            // Convert the byte array to a Bitmap
            Bitmap noteImage = BitmapFactory.decodeByteArray(noteImageBytes, 0, noteImageBytes.length);

            // Add the note to the saved notes list and display it
            addNoteToList(noteImage);
        }
    }

    // Add a note to the list and display it
    private void addNoteToList(Bitmap noteImage) {
        savedNotes.add(noteImage);

        // Create an ImageView to display the note
        ImageView noteView = new ImageView(this);
        noteView.setImageBitmap(noteImage);
        noteView.setAdjustViewBounds(true); // Maintain aspect ratio
        noteView.setPadding(10, 10, 10, 10);

        // Add the ImageView to the notes container
        notesContainer.addView(noteView);
    }
}


