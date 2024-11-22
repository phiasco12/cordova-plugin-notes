package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class NoteEditActivity extends Activity {

    private LinearLayout noteLayout;
    private EditText editText;
    private String noteFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        noteLayout = new LinearLayout(this);
        noteLayout.setOrientation(LinearLayout.VERTICAL);
        noteLayout.setBackgroundColor(Color.WHITE);

        editText = new EditText(this);
        editText.setLayoutParams(new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        editText.setTextSize(16);
        editText.setTextColor(Color.BLACK);

        noteLayout.addView(editText);

        noteFileName = getIntent().getStringExtra("noteFileName");
        if (noteFileName != null) {
            loadNoteData(noteFileName);
        }

        setContentView(noteLayout);
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

                String text = noteJson.getString("text");
                editText.setText(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        saveNoteData();
        super.onBackPressed();
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

            FileOutputStream fos = new FileOutputStream(jsonFile);
            fos.write(noteJson.toString().getBytes());
            fos.close();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("noteFileName", noteFileName);
            setResult(RESULT_OK, resultIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
