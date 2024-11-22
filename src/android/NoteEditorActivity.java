/*this code works. but I need the functionality of it all to be similar to Samsung notes. so when the typing reaches the bottom, it should create a new page. each new page should have the ability to draw on separately as well. use this code as the base. important: both the texts and drawings should be persistent. package com.example.notesplugin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class NoteEditorActivity extends Activity {

    private DrawingView drawingView; // Custom view for drawing
    private EditText textOverlay; // Editable text overlay
    private boolean isDrawingMode = true; // Start in drawing mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the main container
        FrameLayout layout = new FrameLayout(this);

        // Initialize the drawing view
        drawingView = new DrawingView(this);
        FrameLayout.LayoutParams fullScreenParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        layout.addView(drawingView, fullScreenParams); // Add drawing view with full-screen layout

        // Initialize the text overlay
        textOverlay = new EditText(this);
        textOverlay.setBackgroundColor(Color.TRANSPARENT); // Transparent background
        textOverlay.setTextColor(Color.BLACK);            // Black text
        textOverlay.setTextSize(16);                      // Font size
        textOverlay.setSingleLine(false);                 // Multiline input
        textOverlay.setPadding(20, 20, 20, 20);
        textOverlay.setVisibility(View.GONE);             // Start hidden (drawing mode default)
        textOverlay.setLayoutParams(fullScreenParams);
        layout.addView(textOverlay);

        // Add toggle button
        Button toggleButton = new Button(this);
        toggleButton.setText("Toggle to Typing");
        toggleButton.setOnClickListener(v -> toggleMode(toggleButton));
        FrameLayout.LayoutParams toggleParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        toggleParams.setMargins(20, 20, 20, 0); // Top-left margin
        layout.addView(toggleButton, toggleParams);

        // Set the content view
        setContentView(layout);
    }

    // Toggle between drawing and typing
    private void toggleMode(Button toggleButton) {
        isDrawingMode = !isDrawingMode;

        if (isDrawingMode) {
            // Enable drawing
            textOverlay.clearFocus(); // Dismiss keyboard
            textOverlay.setVisibility(View.GONE);
            drawingView.setTouchEnabled(true);
            toggleButton.setText("Toggle to Typing");
        } else {
            // Enable typing
            textOverlay.requestFocus(); // Show keyboard
            textOverlay.setVisibility(View.VISIBLE);
            drawingView.setTouchEnabled(false);
            toggleButton.setText("Toggle to Drawing");
        }
    }

    // Custom View for Drawing
    private class DrawingView extends View {
        private Paint paint;
        private Canvas canvas;
        private Bitmap bitmap;
        private boolean touchEnabled = true; // Control touch interaction
        private float lastX, lastY;

        public DrawingView(Activity context) {
            super(context);

            // Initialize paint for drawing
            paint = new Paint();
            paint.setColor(Color.BLUE); // Drawing color
            paint.setStrokeWidth(8f);  // Line thickness
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);

            // Initialize a bitmap to store the drawing
            bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap); // Canvas to draw on
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            // Resize the bitmap to match the view size
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Draw the bitmap on the canvas
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!touchEnabled) return false;

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Start a new stroke
                    lastX = x;
                    lastY = y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    // Draw a line between the last position and the current position
                    canvas.drawLine(lastX, lastY, x, y, paint);
                    lastX = x;
                    lastY = y;

                    // Request a redraw to update the view
                    invalidate();
                    break;
            }
            return true;
        }

        public void setTouchEnabled(boolean enabled) {
            this.touchEnabled = enabled;
        }
    }
}*/






/*package com.example.notesplugin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.view.View;

public class NoteEditorActivity extends Activity {

    private LinearLayout pagesContainer; // Container for all pages
    private int screenHeight; // Screen height for 100% height calculation
    private EditText currentEditText; // Reference to the current active EditText
    private ScrollView scrollView; // ScrollView to manage scrolling

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get screen height dynamically
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Main ScrollView container
        scrollView = new ScrollView(this) {
            @Override
            protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                // Ensure we can scroll all the way to the top
                if (t == 0) {
                    scrollView.scrollTo(0, 0); // Reset to top if scrolled too far
                }
            }
        };

        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        // LinearLayout to hold pages vertically
        pagesContainer = new LinearLayout(this);
        pagesContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(pagesContainer);

        // Add the first page
        addNewPage();

        // Set the ScrollView as the main content
        setContentView(scrollView);
    }

    // Add a new page
    private void addNewPage() {
        // Create a separator for visual distinction
        if (pagesContainer.getChildCount() > 0) {
            View separator = new View(this);
            LinearLayout.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    4 // Height of the separator (e.g., 4px)
            );
            separator.setBackgroundColor(Color.LTGRAY); // Light gray for the separator
            separator.setLayoutParams(separatorParams);
            pagesContainer.addView(separator);
        }

        // Create a page container with 100% screen height
        FrameLayout pageLayout = new FrameLayout(this);
        pageLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                screenHeight // Full screen height
        ));
        pageLayout.setBackgroundColor(Color.WHITE); // White background for the page

        // Add an EditText for typing
        EditText pageEditText = new EditText(this);
        pageEditText.setBackgroundColor(Color.TRANSPARENT); // Transparent background
        pageEditText.setTextColor(Color.BLACK);             // Black text color
        pageEditText.setTextSize(16);                       // Font size
        pageEditText.setPadding(20, 20, 20, 20);            // Add padding inside the EditText
        pageEditText.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        pageEditText.setSingleLine(false);                  // Multiline text enabled
        pageEditText.setVerticalScrollBarEnabled(false);    // Disable EditText scrolling
        pageEditText.setGravity(android.view.Gravity.TOP);  // Start typing from the top

        // Set this page's EditText as the currentEditText
        currentEditText = pageEditText;

        // Add a listener to detect when a new page should be added
        pageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if text height exceeds the current page height
                int totalLinesHeight = pageEditText.getLineHeight() * pageEditText.getLineCount();
                if (totalLinesHeight >= screenHeight) {
                    pageEditText.removeTextChangedListener(this); // Remove listener to avoid recursion
                    addNewPage(); // Add a new page

                    // Automatically move focus to the new page's EditText
                    currentEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Focus listener to adjust scrolling
        pageEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Scroll to the EditText when it gains focus
                scrollView.post(() -> scrollView.smoothScrollTo(0, pageEditText.getTop()));
            }
        });

        // Add the EditText to the page
        pageLayout.addView(pageEditText);

        // Add the page to the pages container
        pagesContainer.addView(pageLayout);
    }
}*/



package com.example.notesplugin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

public class NoteEditorActivity extends Activity {

    private LinearLayout contentContainer; // Container for all content
    private CustomScrollView scrollView; // Custom scrollable container
    private boolean isDrawingMode = false; // Tracks whether drawing mode is active
    private ResizableSketchView sketchView; // Single sketch area
    private FrameLayout mainLayout; // Main layout to overlay cursor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the ScrollView
        scrollView = new CustomScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // LinearLayout for holding content
        contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setPadding(20, 20, 20, 20); // Add padding around the content
        contentContainer.setOnTouchListener((v, event) -> handleTouch(event)); // Detect touch
        scrollView.addView(contentContainer);

        // Create an initial text area
        addNewTextField();

        // Create a sketch area
        sketchView = new ResizableSketchView(this);
        sketchView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                500 // Initial height for the sketch area
        ));
        sketchView.setBackgroundColor(Color.TRANSPARENT);
        sketchView.setEnabled(false); // Initially disabled
        contentContainer.addView(sketchView);

        // Create a toolbar with a toggle button
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setGravity(Gravity.BOTTOM);
        toolbar.setBackgroundColor(Color.DKGRAY);
        FrameLayout.LayoutParams toolbarParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                150 // Fixed height for the toolbar
        );
        toolbarParams.gravity = Gravity.BOTTOM;
        toolbar.setLayoutParams(toolbarParams);

        // Add a toggle button for switching modes
        ImageButton toggleDrawButton = new ImageButton(this);
        toggleDrawButton.setImageResource(android.R.drawable.ic_menu_edit); // Initial icon for drawing
        toggleDrawButton.setBackgroundColor(Color.LTGRAY);
        toggleDrawButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        toggleDrawButton.setOnClickListener(v -> toggleDrawingMode(toggleDrawButton));

        toolbar.addView(toggleDrawButton);

        // Create a layout to hold the toolbar and the ScrollView
        mainLayout = new FrameLayout(this);
        mainLayout.addView(scrollView);
        mainLayout.addView(toolbar);

        // Set the main layout as the content view
        setContentView(mainLayout);
    }

    private boolean handleTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isDrawingMode) {
            float x = event.getX();
            float y = event.getY();
            showCursor(x, y); // Show red cursor at clicked position
            addNewTextFieldAtLocation((int) y); // Add a new text field
            return true;
        }
        return false;
    }

    private void showCursor(float x, float y) {
        // Create a small red circular view to act as the cursor
        View cursor = new View(this);
        cursor.setLayoutParams(new FrameLayout.LayoutParams(20, 20)); // Small circle
        cursor.setBackgroundColor(Color.RED);

        // Position the cursor
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) cursor.getLayoutParams();
        params.leftMargin = (int) x - 10; // Center the cursor horizontally
        params.topMargin = (int) y - 10;  // Center the cursor vertically
        cursor.setLayoutParams(params);

        // Add the cursor to the main layout
        mainLayout.addView(cursor);

        // Remove the cursor after 500ms
        new Handler().postDelayed(() -> mainLayout.removeView(cursor), 500);
    }

    private void toggleDrawingMode(ImageButton toggleButton) {
        isDrawingMode = !isDrawingMode; // Toggle mode

        if (isDrawingMode) {
            // Switch to drawing mode
            toggleButton.setImageResource(android.R.drawable.ic_menu_view); // Change icon to text
            scrollView.setScrollingEnabled(false); // Disable scrolling
            for (int i = 0; i < contentContainer.getChildCount(); i++) {
                View child = contentContainer.getChildAt(i);
                if (child instanceof EditText) {
                    child.setEnabled(false); // Disable all text areas
                }
            }
            sketchView.setEnabled(true); // Enable drawing
        } else {
            // Switch back to typing mode
            toggleButton.setImageResource(android.R.drawable.ic_menu_edit); // Change icon to draw
            scrollView.setScrollingEnabled(true); // Enable scrolling
            for (int i = 0; i < contentContainer.getChildCount(); i++) {
                View child = contentContainer.getChildAt(i);
                if (child instanceof EditText) {
                    child.setEnabled(true); // Enable all text areas
                }
            }
            sketchView.setEnabled(false); // Disable drawing
        }
    }

    private void addNewTextField() {
        EditText newText = new EditText(this);
        newText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        newText.setBackgroundColor(Color.TRANSPARENT);
        newText.setTextColor(Color.BLACK);
        newText.setTextSize(16);
        newText.setPadding(10, 10, 10, 10);
        newText.setGravity(Gravity.TOP);
        newText.setHorizontallyScrolling(false); // Disable horizontal scrolling
        newText.setSingleLine(false);
        contentContainer.addView(newText);
    }

    private void addNewTextFieldAtLocation(int y) {
        EditText newText = new EditText(this);
        newText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        newText.setBackgroundColor(Color.TRANSPARENT);
        newText.setTextColor(Color.BLACK);
        newText.setTextSize(16);
        newText.setPadding(10, 10, 10, 10);
        newText.setGravity(Gravity.TOP);
        newText.setHorizontallyScrolling(false); // Disable horizontal scrolling
        newText.setSingleLine(false);
        contentContainer.addView(newText);
        scrollView.post(() -> scrollView.smoothScrollTo(0, y)); // Scroll to the new text field
    }

    // Custom ScrollView to enable or disable scrolling
    private static class CustomScrollView extends ScrollView {

        private boolean isScrollable = true;

        public CustomScrollView(Activity context) {
            super(context);
        }

        public void setScrollingEnabled(boolean enabled) {
            isScrollable = enabled;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return isScrollable && super.onTouchEvent(ev);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return isScrollable && super.onInterceptTouchEvent(ev);
        }
    }

    // Custom View for the Sketch Area
    private static class ResizableSketchView extends View {

        private final Paint paint;
        private final Path path;

        public ResizableSketchView(@NonNull Activity context) {
            super(context);

            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);

            path = new Path();
        }

        @Override
        protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!isEnabled()) return false; // Ignore touch events when disabled

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    path.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }

            invalidate(); // Redraw the view
            return true;
        }
    }
}


