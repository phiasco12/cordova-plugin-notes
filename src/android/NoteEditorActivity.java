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



////Important////
/*package com.example.notesplugin;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView; // Added import for TextView

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import java.lang.reflect.Field;

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
            moveCursorToClickedPosition(x, y); // Move cursor to the clicked position
            return true;
        }
        return false;
    }

    private void moveCursorToClickedPosition(float x, float y) {
        for (int i = 0; i < contentContainer.getChildCount(); i++) {
            View child = contentContainer.getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;

                // Check if the click is within this EditText's bounds
                int[] location = new int[2];
                child.getLocationOnScreen(location);

                int left = location[0];
                int top = location[1];
                int right = left + child.getWidth();
                int bottom = top + child.getHeight();

                if (x >= left && x <= right && y >= top && y <= bottom) {
                    // Focus the EditText
                    editText.requestFocus();

                    // Calculate the cursor position based on click
                    Layout layout = editText.getLayout();
                    if (layout != null) {
                        int line = layout.getLineForVertical((int) (y - top));
                        int offset = layout.getOffsetForHorizontal(line, x - left);
                        editText.setSelection(offset);
                    }
                    return;
                }
            }
        }

        // If no existing EditText was clicked, create a new one at the bottom
        addNewTextFieldAtLocation((int) y);
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
        setCursorDrawableColor(newText, Color.RED); // Change cursor color to red
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
        setCursorDrawableColor(newText, Color.RED); // Change cursor color to red
        contentContainer.addView(newText);
        scrollView.post(() -> scrollView.smoothScrollTo(0, y)); // Scroll to the new text field
    }

    // Utility method to set the cursor color
    private static void setCursorDrawableColor(EditText editText, int color) {
        try {
            Field cursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorDrawableRes.setAccessible(true);
            int drawableResId = cursorDrawableRes.getInt(editText);

            Drawable cursorDrawable = AppCompatResources.getDrawable(editText.getContext(), drawableResId);
            if (cursorDrawable != null) {
                cursorDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
                Drawable[] drawables = {cursorDrawable, cursorDrawable};

                Field editorField = TextView.class.getDeclaredField("mEditor");
                editorField.setAccessible(true);
                Object editor = editorField.get(editText);

                Field cursorField = editor.getClass().getDeclaredField("mCursorDrawable");
                cursorField.setAccessible(true);
                cursorField.set(editor, drawables);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        protected void onDraw(Canvas canvas) {
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
}*/













///////VERY IMPORTANT////
/*package com.example.notesplugin;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

public class NoteEditorActivity extends Activity {

    private LinearLayout pagesContainer; // The container for all pages
    private CustomScrollView scrollView; // Custom ScrollView to toggle scrolling
    private LinearLayout bottomToolbar; // Bottom toolbar for buttons
    private int pageHeight; // Fixed height for each page
    private int pageWidth; // Fixed width for each page
    private Page activePage; // Track the currently active page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the CustomScrollView
        scrollView = new CustomScrollView(this);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Initialize the pages container
        pagesContainer = new LinearLayout(this);
        pagesContainer.setOrientation(LinearLayout.VERTICAL);
        pagesContainer.setPadding(20, 20, 20, 20); // Padding between the container edges and pages
        scrollView.addView(pagesContainer);

        // Dynamically calculate page dimensions based on screen size
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (pageHeight == 0 || pageWidth == 0) {
                pageHeight = scrollView.getHeight() - 150; // Account for toolbar height
                pageWidth = scrollView.getWidth() - 80; // Screen width minus padding/margin
                addNewPage(); // Add the first page
            }
        });

        // Create the bottom toolbar
        setupBottomToolbar();

        // Main layout
        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.addView(scrollView);
        mainLayout.addView(bottomToolbar); // Add toolbar to the main layout

        setContentView(mainLayout);
    }

    private void addNewPage() {
        // Create a new page
        Page page = new Page(this, pageWidth, pageHeight);
        pagesContainer.addView(page.getPageLayout());
        activePage = page; // Set the first page as active
    }

    private void setupBottomToolbar() {
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

        // Add the toggle button for sketch/typing mode
        ImageButton toggleButton = new ImageButton(this);
        toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
        toggleButton.setBackgroundColor(Color.LTGRAY);
        toggleButton.setOnClickListener(v -> toggleDrawingMode(toggleButton));
        bottomToolbar.addView(toggleButton);
    }

private void toggleDrawingMode(ImageButton toggleButton) {
    if (activePage != null) {
        boolean isDrawingMode = activePage.toggleDrawingMode(toggleButton);
        scrollView.setScrollingEnabled(!isDrawingMode); // Disable scrolling in drawing mode

        if (isDrawingMode) {
            // Hide the keyboard when toggling to drawing mode
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


    private class Page {
        private final FrameLayout pageLayout; // Page container (frame to overlay text and drawing)
        private final EditText editText; // Text input for the page
        private final ResizableSketchView sketchView; // Sketch area for the page
        private boolean isDrawingMode = false; // Track text/drawing mode
        private boolean isTextWatcherActive = true; // Prevent feedback loop in TextWatcher

        public Page(Activity context, int width, int height) {
            // Create the page layout
            pageLayout = new FrameLayout(context);
            FrameLayout.LayoutParams pageParams = new FrameLayout.LayoutParams(
                    width,
                    height
            );
            pageParams.setMargins(20, 20, 20, 20); // Margins between pages
            pageLayout.setLayoutParams(pageParams);

            // Background with rounded corners
            GradientDrawable pageBackground = createPageBackground();
            pageLayout.setBackground(pageBackground);

            // Create the EditText for typing
            editText = new EditText(context);
            editText.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            editText.setBackgroundColor(Color.TRANSPARENT);
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(16);
            editText.setPadding(10, 10, 10, 10);
            editText.setGravity(Gravity.TOP);
            editText.setHorizontallyScrolling(false);
            editText.setSingleLine(false);

            // Set focus listener to track the active page
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    activePage = this;
                }
            });

            // Monitor text changes to handle overflow and create new pages
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isTextWatcherActive) return; // Avoid feedback loop
                    editText.post(() -> checkForOverflow());
                }
            });

            // Create the sketch view for drawing
            sketchView = new ResizableSketchView(context, width, height);
            sketchView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            sketchView.setBackgroundColor(Color.TRANSPARENT);

            // Add EditText and SketchView to the page layout (SketchView overlays EditText)
            pageLayout.addView(editText);
            pageLayout.addView(sketchView);
        }

        public FrameLayout getPageLayout() {
            return pageLayout;
        }

        public boolean toggleDrawingMode(ImageButton toggleButton) {
            isDrawingMode = !isDrawingMode;
            if (isDrawingMode) {
                toggleButton.setImageResource(android.R.drawable.ic_menu_view);
                sketchView.setClickable(true); // Enable drawing
            } else {
                toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
                sketchView.setClickable(false); // Make sketch click-through
            }
            return isDrawingMode; // Return the current mode
        }

        private void checkForOverflow() {
            Layout layout = editText.getLayout();
            if (layout != null && layout.getHeight() > editText.getHeight()) {
                // Disable the TextWatcher temporarily
                isTextWatcherActive = false;

                // If the text exceeds the current page, create a new page
                String overflowText = editText.getText().toString();
                int lastVisibleLine = layout.getLineForVertical(editText.getHeight());
                int overflowStart = layout.getLineStart(lastVisibleLine);
                String visibleText = overflowText.substring(0, overflowStart);
                String remainingText = overflowText.substring(overflowStart);

                // Update the current page with visible text
                editText.setText(visibleText);

                // Create a new page and set the remaining text
                Page newPage = new Page(NoteEditorActivity.this, pageWidth, pageHeight);
                newPage.editText.setText(remainingText);
                pagesContainer.addView(newPage.getPageLayout());

                // Set focus on the new page's EditText
                newPage.editText.requestFocus();

                // Re-enable the TextWatcher
                isTextWatcherActive = true;
            }
        }

        private GradientDrawable createPageBackground() {
            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.WHITE); // Set the page background color
            background.setCornerRadius(30); // Rounded corners
            background.setStroke(5, Color.LTGRAY); // Add a border
            return background;
        }
    }

    private static class ResizableSketchView extends View {

        private final Paint paint;
        private final Path path;

        public ResizableSketchView(@NonNull Activity context, int width, int height) {
            super(context);

            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);

            path = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!isClickable()) return false; // Make the sketch click-through in typing mode

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
}*/


















/*package com.example.notesplugin;

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

public class NoteEditorActivity extends Activity {

    private LinearLayout pagesContainer; // The container for all pages
    private CustomScrollView scrollView; // Custom ScrollView to toggle scrolling
    private LinearLayout bottomToolbar; // Bottom toolbar for buttons
    private int pageHeight; // Fixed height for each page
    private int pageWidth; // Fixed width for each page
    private Page activePage; // Track the currently active page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the CustomScrollView
        scrollView = new CustomScrollView(this);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Initialize the pages container
        pagesContainer = new LinearLayout(this);
        pagesContainer.setOrientation(LinearLayout.VERTICAL);
        pagesContainer.setPadding(20, 20, 20, 20); // Padding between the container edges and pages
        scrollView.addView(pagesContainer);

        // Dynamically calculate page dimensions based on screen size
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (pageHeight == 0 || pageWidth == 0) {
                pageHeight = scrollView.getHeight() - 150; // Account for toolbar height
                pageWidth = scrollView.getWidth() - 80; // Screen width minus padding/margin
                addNewPage(); // Add the first page
            }
        });

        // Create the bottom toolbar
        setupBottomToolbar();

        // Main layout
        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.addView(scrollView);
        mainLayout.addView(bottomToolbar); // Add toolbar to the main layout

        setContentView(mainLayout);
    }

    private void addNewPage() {
        // Create a new page
        Page page = new Page(this, pageWidth, pageHeight);
        pagesContainer.addView(page.getPageLayout());
        activePage = page; // Set the first page as active
    }

    private void setupBottomToolbar() {
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

        // Add the toggle button for sketch/typing mode
        ImageButton toggleButton = new ImageButton(this);
        toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
        toggleButton.setBackgroundColor(Color.LTGRAY);
        toggleButton.setOnClickListener(v -> toggleDrawingMode(toggleButton));
        bottomToolbar.addView(toggleButton);

        // Add the save button to save and return the note
        ImageButton saveButton = new ImageButton(this);
        saveButton.setImageResource(android.R.drawable.ic_menu_save);
        saveButton.setBackgroundColor(Color.LTGRAY);
        saveButton.setOnClickListener(v -> saveAndReturn());
        bottomToolbar.addView(saveButton);
    }

    private void toggleDrawingMode(ImageButton toggleButton) {
        if (activePage != null) {
            boolean isDrawingMode = activePage.toggleDrawingMode(toggleButton);
            scrollView.setScrollingEnabled(!isDrawingMode); // Disable scrolling in drawing mode
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

    //private void saveAndReturn() {
        //if (pagesContainer.getChildCount() > 0) {
            // Capture only the first page
           // View firstPage = pagesContainer.getChildAt(0);
           // Bitmap firstPageBitmap = createBitmapFromView(firstPage);

            // Compress the bitmap and send it back
           // ByteArrayOutputStream stream = new ByteArrayOutputStream();
           // firstPageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
           // byte[] byteArray = stream.toByteArray();

           // Intent resultIntent = new Intent();
           // resultIntent.putExtra("noteImage", byteArray);
          //  setResult(RESULT_OK, resultIntent);
           // finish(); // Close the NoteEditorActivity
       // }
   // }



    private void saveAndReturn() {
    if (pagesContainer.getChildCount() > 0) {
        // Capture only the first page
        View firstPage = pagesContainer.getChildAt(0);
        Bitmap firstPageBitmap = createBitmapFromView(firstPage);

        // Save the bitmap to a file
        String noteFileName = getIntent().getStringExtra("noteFileName");
        if (noteFileName == null) {
            noteFileName = "note_" + System.currentTimeMillis() + ".png"; // Generate unique filename
        }

        File notesDir = new File(getFilesDir(), "saved_notes");
        if (!notesDir.exists()) {
            notesDir.mkdirs();
        }

        File noteFile = new File(notesDir, noteFileName);
        try (FileOutputStream fos = new FileOutputStream(noteFile)) {
            firstPageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the saved note filename
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noteFileName", noteFileName);
        setResult(RESULT_OK, resultIntent);
        finish(); // Close the NoteEditorActivity
    }
}


    private Bitmap createBitmapFromView(View view) {
        // Create a bitmap for the view's dimensions
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas); // Render the view onto the canvas
        return bitmap;
    }

    private class Page {
        private final FrameLayout pageLayout; // Page container (frame to overlay text and drawing)
        private final EditText editText; // Text input for the page
        private final ResizableSketchView sketchView; // Sketch area for the page
        private boolean isDrawingMode = false; // Track text/drawing mode
        private boolean isTextWatcherActive = true; // Prevent feedback loop in TextWatcher

        public Page(Activity context, int width, int height) {
            // Create the page layout
            pageLayout = new FrameLayout(context);
            FrameLayout.LayoutParams pageParams = new FrameLayout.LayoutParams(
                    width,
                    height
            );
            pageParams.setMargins(20, 20, 20, 20); // Margins between pages
            pageLayout.setLayoutParams(pageParams);

            // Background with rounded corners
            GradientDrawable pageBackground = createPageBackground();
            pageLayout.setBackground(pageBackground);

            // Create the EditText for typing
            editText = new EditText(context);
            editText.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            editText.setBackgroundColor(Color.TRANSPARENT);
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(16);
            editText.setPadding(10, 10, 10, 10);
            editText.setGravity(Gravity.TOP);
            editText.setHorizontallyScrolling(false);
            editText.setSingleLine(false);

            // Set focus listener to track the active page
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    activePage = this;
                }
            });

            // Monitor text changes to handle overflow and create new pages
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isTextWatcherActive) return; // Avoid feedback loop
                    editText.post(() -> checkForOverflow());
                }
            });

            // Create the sketch view for drawing
            sketchView = new ResizableSketchView(context, width, height);
            sketchView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            sketchView.setBackgroundColor(Color.TRANSPARENT);

            // Add EditText and SketchView to the page layout (SketchView overlays EditText)
            pageLayout.addView(editText);
            pageLayout.addView(sketchView);
        }

        public FrameLayout getPageLayout() {
            return pageLayout;
        }

        public boolean toggleDrawingMode(ImageButton toggleButton) {
            isDrawingMode = !isDrawingMode;
            if (isDrawingMode) {
                toggleButton.setImageResource(android.R.drawable.ic_menu_view);
                sketchView.setClickable(true); // Enable drawing
            } else {
                toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
                sketchView.setClickable(false); // Make sketch click-through
            }
            return isDrawingMode; // Return the current mode
        }

        private void checkForOverflow() {
            Layout layout = editText.getLayout();
            if (layout != null && layout.getHeight() > editText.getHeight()) {
                // Disable the TextWatcher temporarily
                isTextWatcherActive = false;

                // If the text exceeds the current page, create a new page
                String overflowText = editText.getText().toString();
                int lastVisibleLine = layout.getLineForVertical(editText.getHeight());
                int overflowStart = layout.getLineStart(lastVisibleLine);
                String visibleText = overflowText.substring(0, overflowStart);
                String remainingText = overflowText.substring(overflowStart);

                // Update the current page with visible text
                editText.setText(visibleText);

                // Create a new page and set the remaining text
                Page newPage = new Page(NoteEditorActivity.this, pageWidth, pageHeight);
                newPage.editText.setText(remainingText);
                pagesContainer.addView(newPage.getPageLayout());

                // Set focus on the new page's EditText
                newPage.editText.requestFocus();

                // Re-enable the TextWatcher
                isTextWatcherActive = true;
            }
        }

        private GradientDrawable createPageBackground() {
            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.WHITE); // Set the page background color
            background.setCornerRadius(30); // Rounded corners
            background.setStroke(5, Color.LTGRAY); // Add a border
            return background;
        }
    }

    private static class ResizableSketchView extends View {

        private final Paint paint;
        private final Path path;

        public ResizableSketchView(@NonNull Activity context, int width, int height) {
            super(context);

            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);

            path = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!isClickable()) return false; // Make the sketch click-through in typing mode

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
}*/















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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

public class NoteEditorActivity extends Activity {

    private LinearLayout pagesContainer; // The container for all pages
    private CustomScrollView scrollView; // Custom ScrollView to toggle scrolling
    private LinearLayout bottomToolbar; // Bottom toolbar for buttons
    private int pageHeight; // Fixed height for each page
    private int pageWidth; // Fixed width for each page
    private Page activePage; // Track the currently active page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the CustomScrollView
        scrollView = new CustomScrollView(this);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // Initialize the pages container
        pagesContainer = new LinearLayout(this);
        pagesContainer.setOrientation(LinearLayout.VERTICAL);
        pagesContainer.setPadding(20, 20, 20, 20); // Padding between the container edges and pages
        scrollView.addView(pagesContainer);

        // Dynamically calculate page dimensions based on screen size
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (pageHeight == 0 || pageWidth == 0) {
                pageHeight = scrollView.getHeight() - 150; // Account for toolbar height
                pageWidth = scrollView.getWidth() - 80; // Screen width minus padding/margin
                addNewPage(); // Add the first page
            }
        });

        // Create the bottom toolbar
        setupBottomToolbar();

        // Main layout
        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.addView(scrollView);
        mainLayout.addView(bottomToolbar); // Add toolbar to the main layout

        setContentView(mainLayout);
    }

    private void addNewPage() {
        // Create a new page
        Page page = new Page(this, pageWidth, pageHeight);
        pagesContainer.addView(page.getPageLayout());
        activePage = page; // Set the first page as active
    }

    private void setupBottomToolbar() {
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

        // Add the toggle button for sketch/typing mode
        ImageButton toggleButton = new ImageButton(this);
        toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
        toggleButton.setBackgroundColor(Color.LTGRAY);
        toggleButton.setOnClickListener(v -> toggleDrawingMode(toggleButton));
        bottomToolbar.addView(toggleButton);

        // Add the save button to save and return the note
        ImageButton saveButton = new ImageButton(this);
        saveButton.setImageResource(android.R.drawable.ic_menu_save);
        saveButton.setBackgroundColor(Color.LTGRAY);
        saveButton.setOnClickListener(v -> saveAndReturn());
        bottomToolbar.addView(saveButton);
    }

    private void toggleDrawingMode(ImageButton toggleButton) {
        if (activePage != null) {
            boolean isDrawingMode = activePage.toggleDrawingMode(toggleButton);
            scrollView.setScrollingEnabled(!isDrawingMode); // Disable scrolling in drawing mode
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

    /*private void saveAndReturn() {
        if (pagesContainer.getChildCount() > 0) {
            // Capture only the first page
            View firstPage = pagesContainer.getChildAt(0);
            Bitmap firstPageBitmap = createBitmapFromView(firstPage);

            // Compress the bitmap and send it back
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            firstPageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("noteImage", byteArray);
            setResult(RESULT_OK, resultIntent);
            finish(); // Close the NoteEditorActivity
        }
    }*/



private void saveAndReturn() {
    if (pagesContainer.getChildCount() > 0) {
        // Capture only the first page
        View firstPage = pagesContainer.getChildAt(0);
        Bitmap firstPageBitmap = createBitmapFromView(firstPage);

        // Save the bitmap to a file
        String noteFileName = getIntent().getStringExtra("noteFileName");
        if (noteFileName == null) {
            noteFileName = "note_" + System.currentTimeMillis() + ".png"; // Generate unique filename
        }

        File notesDir = new File(getFilesDir(), "saved_notes");
        if (!notesDir.exists()) {
            notesDir.mkdirs();
        }

        File noteFile = new File(notesDir, noteFileName);
        try (FileOutputStream fos = new FileOutputStream(noteFile)) {
            firstPageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Save the JSON file
        saveAllPagesDataAsJSON(noteFileName);

        // Return the saved note filename
        Intent resultIntent = new Intent();
        resultIntent.putExtra("noteFileName", noteFileName);
        setResult(RESULT_OK, resultIntent);
        finish(); // Close the NoteEditorActivity
    }
}


    //new chatGPT//
private void saveAllPagesDataAsJSON(String bitmapFileName) {
    try {
        // Create the JSON filename by replacing ".png" with ".json"
        String jsonFileName = bitmapFileName.replace(".png", ".json");

        // Directory to save notes
        File notesDir = new File(getFilesDir(), "saved_notes");
        if (!notesDir.exists()) {
            notesDir.mkdirs();
        }

        // JSON to hold all page data
        JSONArray pagesArray = new JSONArray();

        // Loop through all pages in the container
        for (int i = 0; i < pagesContainer.getChildCount(); i++) {
            Page currentPage = (Page) pagesContainer.getChildAt(i).getTag(); // Get Page object
            JSONObject pageObject = new JSONObject();

            // Save text content
            pageObject.put("text", currentPage.editText.getText().toString());

            // Save sketch paths
            JSONArray sketchPaths = currentPage.sketchView.getSketchPaths();
            pageObject.put("sketch", sketchPaths);

            pagesArray.put(pageObject);
        }

        // Create the final JSON object
        JSONObject notesObject = new JSONObject();
        notesObject.put("pages", pagesArray);

        // Save the JSON file
        File jsonFile = new File(notesDir, jsonFileName);
        try (FileWriter fileWriter = new FileWriter(jsonFile)) {
            fileWriter.write(notesObject.toString());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}


//new chatGPT//
    public JSONArray getSketchPaths() {
    JSONArray pathsArray = new JSONArray();

    // For each path, extract the points and store them in the array
    PathMeasure pathMeasure = new PathMeasure(path, false);
    float[] point = new float[2];

    // Traverse the path and save points
    for (float distance = 0; distance < pathMeasure.getLength(); distance += 10) {
        pathMeasure.getPosTan(distance, point, null);

        // Save the point as a JSON object
        JSONObject pointObject = new JSONObject();
        try {
            pointObject.put("x", point[0]);
            pointObject.put("y", point[1]);
            pathsArray.put(pointObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    return pathsArray;
}




    private Bitmap createBitmapFromView(View view) {
        // Create a bitmap for the view's dimensions
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas); // Render the view onto the canvas
        return bitmap;
    }

    private class Page {
        private final FrameLayout pageLayout; // Page container (frame to overlay text and drawing)
        private final EditText editText; // Text input for the page
        private final ResizableSketchView sketchView; // Sketch area for the page
        private boolean isDrawingMode = false; // Track text/drawing mode
        private boolean isTextWatcherActive = true; // Prevent feedback loop in TextWatcher

        public Page(Activity context, int width, int height) {
            // Create the page layout
            pageLayout = new FrameLayout(context);
            FrameLayout.LayoutParams pageParams = new FrameLayout.LayoutParams(
                    width,
                    height
            );
            pageParams.setMargins(20, 20, 20, 20); // Margins between pages
            pageLayout.setLayoutParams(pageParams);

            // Background with rounded corners
            GradientDrawable pageBackground = createPageBackground();
            pageLayout.setBackground(pageBackground);

            // Create the EditText for typing
            editText = new EditText(context);
            editText.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            editText.setBackgroundColor(Color.TRANSPARENT);
            editText.setTextColor(Color.BLACK);
            editText.setTextSize(16);
            editText.setPadding(10, 10, 10, 10);
            editText.setGravity(Gravity.TOP);
            editText.setHorizontallyScrolling(false);
            editText.setSingleLine(false);

            // Set focus listener to track the active page
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    activePage = this;
                }
            });

            // Monitor text changes to handle overflow and create new pages
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isTextWatcherActive) return; // Avoid feedback loop
                    editText.post(() -> checkForOverflow());
                }
            });

            // Create the sketch view for drawing
            sketchView = new ResizableSketchView(context, width, height);
            sketchView.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            sketchView.setBackgroundColor(Color.TRANSPARENT);

            // Add EditText and SketchView to the page layout (SketchView overlays EditText)
            pageLayout.addView(editText);
            pageLayout.addView(sketchView);
        }

        public FrameLayout getPageLayout() {
            return pageLayout;
        }

        public boolean toggleDrawingMode(ImageButton toggleButton) {
            isDrawingMode = !isDrawingMode;
            if (isDrawingMode) {
                toggleButton.setImageResource(android.R.drawable.ic_menu_view);
                sketchView.setClickable(true); // Enable drawing
            } else {
                toggleButton.setImageResource(android.R.drawable.ic_menu_edit);
                sketchView.setClickable(false); // Make sketch click-through
            }
            return isDrawingMode; // Return the current mode
        }

        private void checkForOverflow() {
            Layout layout = editText.getLayout();
            if (layout != null && layout.getHeight() > editText.getHeight()) {
                // Disable the TextWatcher temporarily
                isTextWatcherActive = false;

                // If the text exceeds the current page, create a new page
                String overflowText = editText.getText().toString();
                int lastVisibleLine = layout.getLineForVertical(editText.getHeight());
                int overflowStart = layout.getLineStart(lastVisibleLine);
                String visibleText = overflowText.substring(0, overflowStart);
                String remainingText = overflowText.substring(overflowStart);

                // Update the current page with visible text
                editText.setText(visibleText);

                // Create a new page and set the remaining text
                Page newPage = new Page(NoteEditorActivity.this, pageWidth, pageHeight);
                newPage.editText.setText(remainingText);
                pagesContainer.addView(newPage.getPageLayout());

                // Set focus on the new page's EditText
                newPage.editText.requestFocus();

                // Re-enable the TextWatcher
                isTextWatcherActive = true;
            }
        }

        private GradientDrawable createPageBackground() {
            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.WHITE); // Set the page background color
            background.setCornerRadius(30); // Rounded corners
            background.setStroke(5, Color.LTGRAY); // Add a border
            return background;
        }
    }

    private static class ResizableSketchView extends View {

        private final Paint paint;
        private final Path path;

        public ResizableSketchView(@NonNull Activity context, int width, int height) {
            super(context);

            paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(8);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);

            path = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!isClickable()) return false; // Make the sketch click-through in typing mode

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

