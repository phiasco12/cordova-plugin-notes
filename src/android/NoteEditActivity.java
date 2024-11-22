package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class NoteEditActivity extends Activity {

    private LinearLayout pagesContainer;
    private CustomScrollView scrollView;
    private LinearLayout bottomToolbar;
    private int pageHeight;
    private int pageWidth;
    private Page activePage;
    private String noteFileName; // Filename of the saved note being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scrollView = new CustomScrollView(this);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        pagesContainer = new LinearLayout(this);
        pagesContainer.setOrientation(LinearLayout.VERTICAL);
        pagesContainer.setPadding(20, 20, 20, 20);
        scrollView.addView(pagesContainer);

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (pageHeight == 0 || pageWidth == 0) {
                pageHeight = scrollView.getHeight() - 150; 
                pageWidth = scrollView.getWidth() - 80; 
                loadSavedNote(); // Load the saved note after dimensions are calculated
            }
        });

        setupBottomToolbar();

        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.addView(scrollView);
        mainLayout.addView(bottomToolbar);

        setContentView(mainLayout);
    }

    private void setupBottomToolbar() {
        bottomToolbar = new LinearLayout(this);
        bottomToolbar.setOrientation(LinearLayout.HORIZONTAL);
        bottomToolbar.setGravity(Gravity.CENTER_VERTICAL);
        bottomToolbar.setBackgroundColor(Color.DKGRAY);
        bottomToolbar.setPadding(20, 20, 20, 20);

        FrameLayout.LayoutParams toolbarParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150
        );
        toolbarParams.gravity = Gravity.BOTTOM;
        bottomToolbar.setLayoutParams(toolbarParams);

        ImageButton toggleButton = new ImageButton(this);
        toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
        toggleButton.setBackgroundColor(Color.LTGRAY);
        toggleButton.setOnClickListener(v -> toggleDrawingMode(toggleButton));
        bottomToolbar.addView(toggleButton);

        ImageButton saveButton = new ImageButton(this);
        saveButton.setImageResource(android.R.drawable.ic_menu_save);
        saveButton.setBackgroundColor(Color.LTGRAY);
        saveButton.setOnClickListener(v -> saveNote());
        bottomToolbar.addView(saveButton);
    }

    private void toggleDrawingMode(ImageButton toggleButton) {
        if (activePage != null) {
            boolean isDrawingMode = activePage.toggleDrawingMode(toggleButton);
            scrollView.setScrollingEnabled(!isDrawingMode);
            if (isDrawingMode) {
                hideKeyboard();
            }
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void saveNote() {
        try {
            File notesDir = new File(getFilesDir(), "saved_notes");
            if (!notesDir.exists()) notesDir.mkdirs();

            File jsonFile = new File(notesDir, noteFileName + ".json");
            JSONObject noteJson = new JSONObject();
            JSONArray pagesArray = new JSONArray();

            for (int i = 0; i < pagesContainer.getChildCount(); i++) {
                Page page = (Page) pagesContainer.getChildAt(i);
                JSONObject pageJson = new JSONObject();
                pageJson.put("text", page.getEditText().getText().toString());
                pageJson.put("drawingPath", page.getSketchView().getPathData());
                pagesArray.put(pageJson);
            }

            noteJson.put("pages", pagesArray);

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

    private void loadSavedNote() {
        noteFileName = getIntent().getStringExtra("noteFileName");

        if (noteFileName == null) {
            finish(); // No note to edit, close activity
            return;
        }

        File notesDir = new File(getFilesDir(), "saved_notes");
        File jsonFile = new File(notesDir, noteFileName + ".json");

        if (!jsonFile.exists()) {
            finish(); // File does not exist, close activity
            return;
        }

        try (FileInputStream fis = new FileInputStream(jsonFile)) {
            byte[] buffer = new byte[(int) jsonFile.length()];
            fis.read(buffer);

            String jsonData = new String(buffer);
            JSONObject noteJson = new JSONObject(jsonData);
            JSONArray pagesArray = noteJson.getJSONArray("pages");

            for (int i = 0; i < pagesArray.length(); i++) {
                JSONObject pageJson = pagesArray.getJSONObject(i);
                String text = pageJson.getString("text");
                String drawingPath = pageJson.getString("drawingPath");

                Page page = new Page(this, pageWidth, pageHeight);
                page.getEditText().setText(text);
                page.getSketchView().loadPathData(drawingPath);
                pagesContainer.addView(page.getPageLayout());
            }

            if (pagesContainer.getChildCount() > 0) {
                activePage = (Page) pagesContainer.getChildAt(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CustomScrollView extends ScrollView {
        private boolean isScrollingEnabled = true;

        public CustomScrollView(Activity context) {
            super(context);
        }

        public void setScrollingEnabled(boolean enabled) {
            isScrollingEnabled = enabled;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return isScrollingEnabled && super.onTouchEvent(ev);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return isScrollingEnabled && super.onInterceptTouchEvent(ev);
        }
    }

    private class Page {
        private final FrameLayout pageLayout;
        private final EditText editText;
        private final ResizableSketchView sketchView;

        public Page(Activity context, int width, int height) {
            pageLayout = new FrameLayout(context);
            pageLayout.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            editText = new EditText(context);
            editText.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));

            sketchView = new ResizableSketchView(context, width, height);
            sketchView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
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

        public boolean toggleDrawingMode(ImageButton toggleButton) {
            // Toggle drawing/text mode here
        }
    }

    private static class ResizableSketchView extends View {
        private final Paint paint;
        private final Path path;

        public ResizableSketchView(Activity context, int width, int height) {
            super(context);
            // Paint setup
        }

        // Methods for drawing, saving, and restoring paths
    }
}
