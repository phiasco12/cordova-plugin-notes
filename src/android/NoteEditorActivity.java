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
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    private LinearLayout pagesContainer; // Container for all pages
    private ScrollView scrollView; // Main scrollable container
    private int screenHeight; // Screen height for creating full-page layouts
    private LinearLayout currentActivePage; // Tracks the currently active (focused) page
    private EditText currentActiveEditText; // Tracks the currently focused EditText
    private boolean isDrawingMode = false; // Tracks if the app is in drawing mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get screen height dynamically
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Initialize the ScrollView
        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // LinearLayout for holding pages
        pagesContainer = new LinearLayout(this);
        pagesContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(pagesContainer);

        // Add the first page
        createNewPage();

        // Add a toolbar with toggle button
        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.addView(scrollView);
        mainLayout.addView(createToolbar());

        // Set the main layout as the content view
        setContentView(mainLayout);
    }

    private View createToolbar() {
        // Create a bottom toolbar
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setBackgroundColor(Color.DKGRAY);
        toolbar.setPadding(10, 10, 10, 10);

        // Create a toggle button for drawing mode
        ImageButton toggleButton = new ImageButton(this);
        toggleButton.setImageResource(android.R.drawable.ic_menu_edit); // Default to drawing icon
        toggleButton.setBackgroundColor(Color.TRANSPARENT);
        toggleButton.setOnClickListener(v -> toggleDrawingMode(toggleButton));
        toolbar.addView(toggleButton, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Position the toolbar at the bottom
        FrameLayout.LayoutParams toolbarParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        toolbarParams.setMargins(0, 0, 0, 0);
        toolbarParams.gravity = android.view.Gravity.BOTTOM;
        toolbar.setLayoutParams(toolbarParams);

        return toolbar;
    }

    private void toggleDrawingMode(ImageButton toggleButton) {
        isDrawingMode = !isDrawingMode;

        if (isDrawingMode) {
            // Enter drawing mode
            toggleButton.setImageResource(android.R.drawable.ic_menu_add); // Change icon to "T"
            if (currentActiveEditText != null) {
                currentActiveEditText.clearFocus(); // Remove focus from EditText
                currentActiveEditText.setEnabled(false); // Disable typing
            }
            scrollView.setScrollingEnabled(false); // Disable scrolling
        } else {
            // Exit drawing mode
            toggleButton.setImageResource(android.R.drawable.ic_menu_edit); // Change icon to drawing
            if (currentActiveEditText != null) {
                currentActiveEditText.setEnabled(true); // Re-enable typing
            }
            scrollView.setScrollingEnabled(true); // Re-enable scrolling
        }
    }

    private void createNewPage() {
        // Create a container for a single page
        LinearLayout pageLayout = new LinearLayout(this);
        pageLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams pageLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                screenHeight - 100 // Slightly smaller than the screen to leave space for margin
        );
        pageLayoutParams.setMargins(20, 20, 20, 20); // Add margins around each page
        pageLayout.setLayoutParams(pageLayoutParams);
        pageLayout.setBackgroundColor(Color.WHITE);
        pageLayout.setPadding(30, 30, 30, 30); // Add padding inside the page

        // Create an EditText for writing
        EditText pageEditText = new EditText(this);
        pageEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1 // Weight to use remaining space for the EditText
        ));
        pageEditText.setBackgroundColor(Color.TRANSPARENT);
        pageEditText.setTextColor(Color.BLACK);
        pageEditText.setTextSize(16);
        pageEditText.setPadding(10, 10, 10, 10);
        pageEditText.setSingleLine(false);
        pageEditText.setGravity(android.view.Gravity.TOP);
        pageEditText.setVerticalScrollBarEnabled(false);

        // Add a listener to track the current active page
        pageEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                currentActivePage = pageLayout;
                currentActiveEditText = pageEditText;
            }
        });

        // Add a listener for detecting when to add a new page
        pageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Calculate text height dynamically
                int textHeight = pageEditText.getLineHeight() * pageEditText.getLineCount();
                if (textHeight >= screenHeight - 200) { // Adjust for padding and margin
                    pageEditText.removeTextChangedListener(this); // Prevent recursion
                    createNewPage(); // Add a new page
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add the EditText to the page
        pageLayout.addView(pageEditText);

        // Add the new page to the container
        pagesContainer.addView(pageLayout);

        // Set the new page as the active page and focus its EditText
        currentActivePage = pageLayout;
        currentActiveEditText = pageEditText;

        // Scroll to the new page and request focus
        scrollView.post(() -> {
            scrollView.smoothScrollTo(0, pageLayout.getTop());
            pageEditText.requestFocus();
        });
    }

    private static class SketchView extends View {

        private final Paint paint;
        private final Path path;

        public SketchView(@NonNull Activity context) {
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
