package com.example.notesplugin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class NoteEditorActivity extends Activity {

    private DrawingView drawingView; // Custom view for drawing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a main container
        FrameLayout layout = new FrameLayout(this);

        // Initialize the drawing view
        drawingView = new DrawingView(this);
        FrameLayout.LayoutParams fullScreenParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        layout.addView(drawingView, fullScreenParams);

        // Set the content view
        setContentView(layout);
    }

    // Custom View for Drawing
    private class DrawingView extends View {
        private Paint paint;
        private Canvas canvas;
        private Bitmap bitmap;
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
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Draw the bitmap on the canvas
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
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
    }
}

