package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class NoteEditActivity extends Activity {

    private LinearLayout noteContainer;
    private EditText editText;
    private ResizableSketchView sketchView;
    private String noteFileName; // Name of the file being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteContainer = new LinearLayout(this);
        noteContainer.setOrientation(LinearLayout.VERTICAL);
        noteContainer.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        noteContainer.setBackgroundColor(Color.WHITE);

        // Initialize text and sketch views
        setupEditText();
        setupSketchView();

        // Add text and sketch views to the container
        noteContainer.addView(editText);
        noteContainer.addView(sketchView);

        // Load the saved note data if editing an existing note
        noteFileName = getIntent().getStringExtra("noteFileName");
        if (noteFileName != null) {
            loadNoteData(noteFileName);
        }

        // Add save button
        setupSaveButton();

        setContentView(noteContainer);
    }

    private void setupEditText() {
        editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 1 // Weight of 1 to share space with sketchView
        ));
        editText.setTextSize(18);
        editText.setTextColor(Color.BLACK);
        editText.setBackgroundColor(Color.TRANSPARENT);
        editText.setPadding(20, 20, 20, 20);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSketchView() {
        sketchView = new ResizableSketchView(this);
        sketchView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0, 2 // Weight of 2 to give more space to the sketch area
        ));
        sketchView.setBackgroundColor(Color.LTGRAY);
    }

    private void setupSaveButton() {
        ImageButton saveButton = new ImageButton(this);
        saveButton.setImageResource(android.R.drawable.ic_menu_save);
        saveButton.setBackgroundColor(Color.DKGRAY);
        saveButton.setOnClickListener(v -> saveNoteData());
        noteContainer.addView(saveButton);
    }

    private void loadNoteData(String fileName) {
        try {
            File notesDir = new File(getFilesDir(), "saved_notes");
            File jsonFile = new File(notesDir, fileName + ".json");

            if (jsonFile.exists()) {
                FileInputStream fis = new FileInputStream(jsonFile);
                byte[] buffer = new byte[(int) jsonFile.length()];
                fis.read(buffer);
                fis.close();

                String jsonData = new String(buffer);
                JSONObject noteJson = new JSONObject(jsonData);

                // Load text and sketch data
                String text = noteJson.getString("text");
                String drawingPath = noteJson.getString("drawingPath");

                editText.setText(text);
                sketchView.loadPathData(drawingPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNoteData() {
        try {
            File notesDir = new File(getFilesDir(), "saved_notes");
            if (!notesDir.exists()) notesDir.mkdirs();

            if (noteFileName == null) {
                noteFileName = "note_" + System.currentTimeMillis();
            }

            File jsonFile = new File(notesDir, noteFileName + ".json");

            JSONObject noteJson = new JSONObject();
            noteJson.put("text", editText.getText().toString());
            noteJson.put("drawingPath", sketchView.getPathData());

            FileOutputStream fos = new FileOutputStream(jsonFile);
            fos.write(noteJson.toString().getBytes());
            fos.close();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("noteFileName", noteFileName);
            setResult(RESULT_OK, resultIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

