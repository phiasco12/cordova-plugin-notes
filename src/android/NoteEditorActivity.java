package com.example.notesplugin;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

public class NoteEditorActivity extends Activity {

    private DrawingView drawingView;
    private EditText textOverlay;
    private boolean isDrawingMode = true; // Toggle between drawing and typing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the main container
        FrameLayout layout = new FrameLayout(this);

        // Initialize the drawing canvas
        drawingView = new DrawingView(this);
        layout.addView(drawingView);

        // Initialize the text overlay
        textOverlay = new EditText(this);
        textOverlay.setBackgroundColor(Color.TRANSPARENT); // Make the background transparent
        textOverlay.setTextColor(Color.BLACK);            // Set text color
        textOverlay.setTextSize(16);                      // Set text size
        textOverlay.setSingleLine(false);                 // Enable multiline input
        textOverlay.setPadding(10, 10, 10, 10);
        textOverlay.setVisibility(View.GONE);             // Start hidden since drawing mode is default
        layout.addView(textOverlay);

        // Set the content view
        setContentView(layout);

        // Toggle between drawing and typing when clicked
        layout.setOnClickListener(v -> toggleMode());
    }

    // Toggle between drawing and typing modes
    private void toggleMode() {
        isDrawingMode = !isDrawingMode;

        if (isDrawingMode) {
            textOverlay.setVisibility(View.GONE);
            drawingView.setVisibility(View.VISIBLE);
        } else {
            textOverlay.setVisibility(View.VISIBLE);
            drawingView.setVisibility(View.GONE);
        }
    }

    // Custom View for Drawing
    private class DrawingView extends View {
        private Paint paint;
        private Canvas canvas;
        private float lastX, lastY;

        public DrawingView(Activity context) {
            super(context);

            // Initialize the paint
            paint = new Paint();
            paint.setColor(Color.BLUE); // Drawing color
            paint.setStrokeWidth(8f);  // Line thickness
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            this.canvas = canvas;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = x;
                    lastY = y;
                    break;

                case MotionEvent.ACTION_MOVE:
                    canvas.drawLine(lastX, lastY, x, y, paint);
                    lastX = x;
                    lastY = y;
                    invalidate(); // Redraw the canvas
                    break;
            }
            return true;
        }
    }
}

