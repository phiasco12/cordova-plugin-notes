package com.example.notesplugin;

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
}
