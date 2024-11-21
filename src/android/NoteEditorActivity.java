package com.example.notesplugin;

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
        textOverlay.setFocusable(false);                  // Disable typing initially
        textOverlay.setFocusableInTouchMode(false);
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
            textOverlay.setFocusable(false);
            textOverlay.setFocusableInTouchMode(false);
            drawingView.setTouchEnabled(true);
            toggleButton.setText("Toggle to Typing");
        } else {
            // Enable typing
            textOverlay.setFocusable(true);
            textOverlay.setFocusableInTouchMode(true);
            textOverlay.requestFocus(); // Show keyboard
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
}
