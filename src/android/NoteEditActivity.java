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
import android.util.Log;
import android.util.Pair;
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
import java.util.ArrayList;

import android.view.ViewGroup;


import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;


public class NoteEditActivity extends Activity {

    private static final String TAG = "NoteEditActivity";

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

    /*private void setupBottomToolbar() {
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
    }*/






// Inside the class
/*private void setupBottomToolbar() {
    bottomToolbar = new LinearLayout(this);
    bottomToolbar.setOrientation(LinearLayout.HORIZONTAL);
    bottomToolbar.setGravity(Gravity.CENTER_VERTICAL);
    bottomToolbar.setBackgroundColor(Color.DKGRAY);
    bottomToolbar.setPadding(20, 20, 20, 20);

    FrameLayout.LayoutParams toolbarParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            150 // Fixed height for the toolbar
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

    // Font size adjustment button
    ImageButton fontSizeButton = new ImageButton(this);
    fontSizeButton.setImageResource(android.R.drawable.ic_menu_zoom); // Placeholder icon for font size
    fontSizeButton.setBackgroundColor(Color.LTGRAY);
    fontSizeButton.setOnClickListener(v -> adjustFontSize());
    bottomToolbar.addView(fontSizeButton);
}*/












private void setupBottomToolbar() {
    bottomToolbar = new LinearLayout(this);
    bottomToolbar.setOrientation(LinearLayout.HORIZONTAL);
    bottomToolbar.setGravity(Gravity.CENTER);
    bottomToolbar.setBackgroundColor(Color.DKGRAY);

    FrameLayout.LayoutParams toolbarParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            150 // Fixed height for the toolbar
    );
    toolbarParams.gravity = Gravity.BOTTOM;
    bottomToolbar.setLayoutParams(toolbarParams);

    // Common layout parameters for evenly spaced buttons
    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
            0, // Width is 0 to distribute evenly
            ViewGroup.LayoutParams.MATCH_PARENT,
            1.0f // Weight to distribute evenly
    );

    // Save button
    ImageButton saveButton = new ImageButton(this);
    saveButton.setImageResource(android.R.drawable.ic_menu_save);
    saveButton.setBackgroundColor(Color.LTGRAY);
    saveButton.setOnClickListener(v -> saveNote());
    saveButton.setLayoutParams(buttonParams);

    // Toggle Sketch Mode button
    ImageButton toggleButton = new ImageButton(this);
    toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
    toggleButton.setBackgroundColor(Color.LTGRAY);
    toggleButton.setOnClickListener(v -> toggleDrawingMode(toggleButton));
    toggleButton.setLayoutParams(buttonParams);

    // Font size adjustment button
    ImageButton fontSizeButton = new ImageButton(this);
    fontSizeButton.setImageResource(android.R.drawable.ic_menu_zoom); // Placeholder icon for font size
    fontSizeButton.setBackgroundColor(Color.LTGRAY);
    fontSizeButton.setOnClickListener(v -> adjustFontSize());
    fontSizeButton.setLayoutParams(buttonParams);

    // Add buttons to the toolbar
    bottomToolbar.addView(saveButton);
    bottomToolbar.addView(toggleButton);
    bottomToolbar.addView(fontSizeButton);
}












    

// Method to adjust font size
private void adjustFontSize() {
    if (activePage != null) {
        EditText editText = activePage.editText;
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();

        if (start < 0 || end <= start) {
            // No selection, or invalid range, do nothing
            return;
        }

        Editable editable = editText.getText();
        SpannableStringBuilder spannable = new SpannableStringBuilder(editable);

        // Get existing spans within the range
        RelativeSizeSpan[] spans = spannable.getSpans(start, end, RelativeSizeSpan.class);

        float currentSize = 1.0f; // Default size multiplier
        if (spans.length > 0) {
            currentSize = spans[0].getSizeChange(); // Get the first span's size
        }

        // Cycle through sizes (1.0f -> 1.5f -> 2.0f -> back to 1.0f)
        float nextSize = currentSize == 1.0f ? 1.5f : (currentSize == 1.5f ? 2.0f : 1.0f);

        // Remove existing spans
        for (RelativeSizeSpan span : spans) {
            spannable.removeSpan(span);
        }

        // Apply the new size span
        spannable.setSpan(new RelativeSizeSpan(nextSize), start, end, 0);
        editText.setText(spannable);
        editText.setSelection(start, end); // Retain selection after applying span
    }
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
        Log.d(TAG, "Loaded JSON Data: " + jsonData); // Log the JSON data

        JSONObject noteJson = new JSONObject(jsonData);
        JSONArray pagesArray = noteJson.getJSONArray("pages");

        for (int i = 0; i < pagesArray.length(); i++) {
            JSONObject pageJson = pagesArray.getJSONObject(i);
            String text = pageJson.getString("text");
            JSONArray sketchPaths = pageJson.getJSONArray("sketch");

            // Retrieve font size spans if present
            JSONArray fontSizesArray = pageJson.optJSONArray("fontSizes");

            Page page = new Page(this, pageWidth, pageHeight);
            page.editText.setText(text);

            if (fontSizesArray != null) {
                SpannableStringBuilder spannable = new SpannableStringBuilder(text);
                for (int j = 0; j < fontSizesArray.length(); j++) {
                    JSONObject spanObject = fontSizesArray.getJSONObject(j);
                    int start = spanObject.getInt("start");
                    int end = spanObject.getInt("end");
                    float size = (float) spanObject.getDouble("size");
                    spannable.setSpan(new RelativeSizeSpan(size), start, end, 0);
                }
                page.editText.setText(spannable);
            }

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

            // Save font sizes
            JSONArray fontSizesArray = new JSONArray();
            SpannableStringBuilder spannable = new SpannableStringBuilder(page.editText.getText());
            RelativeSizeSpan[] spans = spannable.getSpans(0, spannable.length(), RelativeSizeSpan.class);
            for (RelativeSizeSpan span : spans) {
                int start = spannable.getSpanStart(span);
                int end = spannable.getSpanEnd(span);
                JSONObject spanObject = new JSONObject();
                spanObject.put("start", start);
                spanObject.put("end", end);
                spanObject.put("size", span.getSizeChange());
                fontSizesArray.put(spanObject);
            }
            pageJson.put("fontSizes", fontSizesArray);

            pageJson.put("sketch", page.sketchView.getSketchPaths());
            pagesArray.put(pageJson);
        }

        noteJson.put("pages", pagesArray);

        FileOutputStream fos = new FileOutputStream(jsonFile);
        fos.write(noteJson.toString().getBytes());
        fos.close();

        Log.d(TAG, "Saved JSON Data: " + noteJson.toString()); // Log the saved JSON data

        setResult(RESULT_OK);
        finish();
    } catch (Exception e) {
        e.printStackTrace();
    }
}




    

   /* private void loadSavedNote() {
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
            Log.d(TAG, "Loaded JSON Data: " + jsonData); // Log the JSON data

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

            Log.d(TAG, "Saved JSON Data: " + noteJson.toString()); // Log the saved JSON data

            setResult(RESULT_OK);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

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
        private boolean isTextWatcherActive = true;

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
            editText.setHorizontallyScrolling(false);
            editText.setSingleLine(false);

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
                    if (!isTextWatcherActive || isLoading) return; // Avoid feedback loop
                    editText.post(() -> checkForOverflow());
                }
            });

            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) activePage = this;
            });

            pageLayout.addView(editText);
            pageLayout.addView(sketchView);
            pageLayout.setTag(this);
        }

        public FrameLayout getPageLayout() {
            return pageLayout;
        }

        private void checkForOverflow() {
            Layout layout = editText.getLayout();
            if (layout != null && layout.getHeight() > editText.getHeight()) {
                isTextWatcherActive = false;

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

                isTextWatcherActive = true;
            }
        }

        public boolean toggleDrawingMode(ImageButton toggleButton) {
            isDrawingMode = !isDrawingMode;
            toggleButton.setImageResource(isDrawingMode ? android.R.drawable.ic_menu_view : android.R.drawable.ic_menu_edit);
            sketchView.setClickable(isDrawingMode);
            editText.setEnabled(!isDrawingMode);
            if (isDrawingMode) hideKeyboard();
            return isDrawingMode;
        }
    }

    private static class ResizableSketchView extends View {
        private final Paint paint;
        private final Path path;
        private final ArrayList<ArrayList<Pair<Float, Float>>> strokes = new ArrayList<>();
        private ArrayList<Pair<Float, Float>> currentStroke;

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
            JSONArray strokesArray = new JSONArray();
            try {
                for (ArrayList<Pair<Float, Float>> stroke : strokes) {
                    JSONArray strokeArray = new JSONArray();
                    for (Pair<Float, Float> point : stroke) {
                        JSONObject pointObject = new JSONObject();
                        pointObject.put("x", point.first);
                        pointObject.put("y", point.second);
                        strokeArray.put(pointObject);
                    }
                    strokesArray.put(strokeArray);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return strokesArray;
        }

        public void loadSketchPaths(JSONArray sketchPaths) {
            path.reset();
            strokes.clear();
            try {
                for (int i = 0; i < sketchPaths.length(); i++) {
                    JSONArray strokeArray = sketchPaths.getJSONArray(i);
                    ArrayList<Pair<Float, Float>> stroke = new ArrayList<>();
                    for (int j = 0; j < strokeArray.length(); j++) {
                        JSONObject pointObject = strokeArray.getJSONObject(j);
                        float x = (float) pointObject.getDouble("x");
                        float y = (float) pointObject.getDouble("y");
                        stroke.add(new Pair<>(x, y));
                    }
                    strokes.add(stroke);
                }
                redrawPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void redrawPath() {
            path.reset();
            for (ArrayList<Pair<Float, Float>> stroke : strokes) {
                if (stroke.size() > 0) {
                    Pair<Float, Float> firstPoint = stroke.get(0);
                    path.moveTo(firstPoint.first, firstPoint.second);
                    for (int i = 1; i < stroke.size(); i++) {
                        Pair<Float, Float> point = stroke.get(i);
                        path.lineTo(point.first, point.second);
                    }
                }
            }
            invalidate();
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
                    currentStroke = new ArrayList<>();
                    currentStroke.add(new Pair<>(x, y));
                    strokes.add(currentStroke);
                    path.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    currentStroke.add(new Pair<>(x, y));
                    path.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    currentStroke.add(new Pair<>(x, y));
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
