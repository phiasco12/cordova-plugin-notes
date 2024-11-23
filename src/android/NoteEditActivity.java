package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class NoteEditActivity extends Activity {

    private LinearLayout pagesContainer;
    private ScrollView scrollView;
    private LinearLayout bottomToolbar;
    private int pageHeight, pageWidth;
    private ArrayList<Page> pages = new ArrayList<>(); // Store Page objects
    private String noteFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scrollView = new ScrollView(this);
        pagesContainer = new LinearLayout(this);
        pagesContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(pagesContainer);

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (pageHeight == 0 || pageWidth == 0) {
                pageHeight = scrollView.getHeight() - 150;
                pageWidth = scrollView.getWidth() - 80;
                loadSavedNote();
            }
        });

        setupBottomToolbar();

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(scrollView);
        mainLayout.addView(bottomToolbar);
        setContentView(mainLayout);
    }

    private void setupBottomToolbar() {
        bottomToolbar = new LinearLayout(this);
        bottomToolbar.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton saveButton = new ImageButton(this);
        saveButton.setImageResource(android.R.drawable.ic_menu_save);
        saveButton.setOnClickListener(v -> saveNote());
        bottomToolbar.addView(saveButton);
    }

    private void loadSavedNote() {
        noteFileName = getIntent().getStringExtra("noteFileName");
        if (noteFileName == null) return;

        File notesDir = new File(getFilesDir(), "saved_notes");
        File jsonFile = new File(notesDir, noteFileName + ".json");

        if (!jsonFile.exists()) return;

        try (FileInputStream fis = new FileInputStream(jsonFile)) {
            byte[] buffer = new byte[(int) jsonFile.length()];
            fis.read(buffer);

            String jsonData = new String(buffer);
            JSONObject noteJson = new JSONObject(jsonData);
            JSONArray pagesArray = noteJson.getJSONArray("pages");

            for (int i = 0; i < pagesArray.length(); i++) {
                JSONObject pageJson = pagesArray.getJSONObject(i);
                String text = pageJson.getString("text");
                JSONArray sketchPaths = pageJson.getJSONArray("sketch");

                Page page = new Page(this, pageWidth, pageHeight);
                page.getEditText().setText(text);
                page.getSketchView().loadSketchPaths(sketchPaths); // Load sketch paths
                pages.add(page); // Store Page object
                pagesContainer.addView(page.getPageLayout());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNote() {
        try {
            File notesDir = new File(getFilesDir(), "saved_notes");
            if (!notesDir.exists()) notesDir.mkdirs();

            File jsonFile = new File(notesDir, noteFileName + ".json");
            JSONObject noteJson = new JSONObject();
            JSONArray pagesArray = new JSONArray();

            for (Page page : pages) {
                JSONObject pageJson = new JSONObject();
                pageJson.put("text", page.getEditText().getText().toString());
                pageJson.put("sketch", page.getSketchView().getSketchPaths());
                pagesArray.put(pageJson);
            }

            noteJson.put("pages", pagesArray);

            FileOutputStream fos = new FileOutputStream(jsonFile);
            fos.write(noteJson.toString().getBytes());
            fos.close();

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Page {
        private final FrameLayout pageLayout;
        private final EditText editText;
        private final ResizableSketchView sketchView;

        public Page(Activity context, int width, int height) {
            pageLayout = new FrameLayout(context);
            editText = new EditText(context);
            sketchView = new ResizableSketchView(context);

            editText.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            sketchView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));

            pageLayout.addView(editText);
            pageLayout.addView(sketchView);
        }

        public FrameLayout getPageLayout() {
            return pageLayout;
        }

        public EditText getEditText() {
            return editText;
        }

        public ResizableSketchView getSketchView() {
            return sketchView;
        }
    }

    private static class ResizableSketchView extends View {
        private final Paint paint;
        private final Path path;

        public ResizableSketchView(Activity context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);

            path = new Path();
        }

        public JSONArray getSketchPaths() {
            JSONArray pathsArray = new JSONArray();
            // Serialize the path into points (implement logic here)
            return pathsArray;
        }

        public void loadSketchPaths(JSONArray sketchPaths) {
            // Deserialize JSON array into path (implement logic here)
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    break;
            }

            invalidate(); // Redraw the view
            return true;
        }
    }
}
