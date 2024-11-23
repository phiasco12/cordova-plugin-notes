package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
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

import android.view.ViewGroup;

public class NoteEditActivity extends Activity {

    private LinearLayout pagesContainer;
    private CustomScrollView scrollView;
    private LinearLayout bottomToolbar;
    private int pageHeight, pageWidth;
    private Page activePage;
    private String noteFileName;
    private boolean isLoading = false; // Flag to differentiate loading vs typing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize scrollView and pagesContainer
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
                pageHeight = scrollView.getHeight() - 150; // Account for toolbar height
                pageWidth = scrollView.getWidth() - 80; // Account for padding
                loadSavedNote();
            }
        });

        // Bottom toolbar for save and toggle buttons
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

        // Save button
        ImageButton saveButton = new ImageButton(this);
        saveButton.setImageResource(android.R.drawable.ic_menu_save);
        saveButton.setBackgroundColor(Color.LTGRAY);
        saveButton.setOnClickListener(v -> saveNote());
        bottomToolbar.addView(saveButton);

        // Toggle button for switching between text and sketch modes
        ImageButton toggleButton = new ImageButton(this);
        toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
        toggleButton.setBackgroundColor(Color.LTGRAY);
        toggleButton.setOnClickListener(v -> toggleDrawingMode(toggleButton));
        bottomToolbar.addView(toggleButton);
    }

    private void loadSavedNote() {
        isLoading = true; // Set flag to prevent pagination during loading
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
                page.editText.setText(text);
                page.sketchView.loadSketchPaths(sketchPaths);
                pagesContainer.addView(page.getPageLayout());

                if (i == 0) activePage = page; // Set the first page as active
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isLoading = false; // Reset flag after loading completes
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
                Page page = (Page) pagesContainer.getChildAt(i).getTag();
                JSONObject pageJson = new JSONObject();
                pageJson.put("text", page.editText.getText().toString());
                pageJson.put("sketch", page.sketchView.getSketchPaths());
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

    private void toggleDrawingMode(ImageButton toggleButton) {
        if (activePage != null) {
            boolean isDrawingMode = activePage.toggleDrawingMode(toggleButton);
            scrollView.setScrollingEnabled(!isDrawingMode); // Disable scrolling in drawing mode
            if (isDrawingMode) hideKeyboard();
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class Page {
        private final FrameLayout pageLayout;
        private final EditText editText;
        private final ResizableSketchView sketchView;
        private boolean isDrawingMode = false;

        public Page(Activity context, int width, int height) {
            pageLayout = new FrameLayout(context);
            FrameLayout.LayoutParams pageParams = new FrameLayout.LayoutParams(
                    width,
                    height
            );
            pageParams.setMargins(20, 20, 20, 20);
            pageLayout.setLayoutParams(pageParams);

            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.WHITE);
            background.setCornerRadius(30);
            background.setStroke(5, Color.LTGRAY);
            pageLayout.setBackground(background);

            editText = new EditText(context);
            editText.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            editText.setBackgroundColor(Color.TRANSPARENT);
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(16);
            editText.setPadding(10, 10, 10, 10);
            editText.setGravity(Gravity.TOP);

            sketchView = new ResizableSketchView(context, width, height);
            sketchView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            sketchView.setBackgroundColor(Color.TRANSPARENT);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isLoading) editText.post(() -> checkForOverflow());
                }
            });

            pageLayout.addView(editText);
            pageLayout.addView(sketchView);
            pageLayout.setTag(this);
        }

        private void checkForOverflow() {
            Layout layout = editText.getLayout();
            if (layout != null && layout.getHeight() > editText.getHeight()) {
                String overflowText = editText.getText().toString();
                int lastVisibleLine = layout.getLineForVertical(editText.getHeight());
                int overflowStart = layout.getLineStart(lastVisibleLine);
                String visibleText = overflowText.substring(0, overflowStart);
                String remainingText = overflowText.substring(overflowStart);

                editText.setText(visibleText);

                Page newPage = new Page(NoteEditActivity.this, pageWidth, pageHeight);
                newPage.editText.setText(remainingText);
                pagesContainer.addView(newPage.getPageLayout());

                newPage.editText.requestFocus();
                activePage = newPage;
            }
        }

        public boolean toggleDrawingMode(ImageButton toggleButton) {
            isDrawingMode = !isDrawingMode;
            toggleButton.setImageResource(isDrawingMode ? android.R.drawable.ic_menu_view : android.R.drawable.ic_menu_edit);
            sketchView.setClickable(isDrawingMode);
            editText.setEnabled(!isDrawingMode);
            return isDrawingMode;
        }
    }

    private static class ResizableSketchView extends View {
        private final Paint paint;
        private final Path path;

        public ResizableSketchView(Activity context, int width, int height) {
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
            // Serialize paths into JSON
            return pathsArray;
        }

        public void loadSketchPaths(JSONArray sketchPaths) {
            // Deserialize JSON to reconstruct paths
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!isClickable()) return false;

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

            invalidate();
            return true;
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
}
