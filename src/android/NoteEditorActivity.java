package com.example.notesplugin;

import android.app.Activity;
import android.content.Intent;
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

import java.io.ByteArrayOutputStream;

public class NoteEditorActivity extends Activity {

    private DrawingView drawingView;
    private EditText textOverlay;
    private boolean isDrawingMode = true; // Start in drawing mode
    private Bitmap savedBitmap; // For saving drawing content

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
        textOverlay.setBackgroundColor(Color.TRANSPARENT); // Transparent background
        textOverlay.setTextColor(Color.BLACK);            // Black text
        textOverlay.setTextSize(16);                      // Font size
        textOverlay.setSingleLine(false);                 // Multiline input
        textOverlay.setPadding(20, 20, 20, 20);
        textOverlay.setVisibility(View.GONE);             // Start hidden (drawing mode default)
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

        // Add save button
        Button saveButton = new Button(this);
        saveButton.setText("Save");
        saveButton.setOnClickListener(v -> saveContent());
        FrameLayout.LayoutParams saveParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        saveParams.setMargins(20, 100, 20, 0); // Below the toggle button
        layout.addView(saveButton, saveParams);

        // Set the content view
        setContentView(layout);
    }

    // Toggle between drawing and typing modes
    private void toggleMode(Button toggleButton) {
        isDrawingMode = !isDrawingMode;

        if (isDrawingMode) {
            // Switch to drawing mode
            textOverlay.setVisibility(View.GONE);
            drawingView.setVisibility(View.VISIBLE);
            toggleButton.setText("Toggle to Typing");
        } else {
            // Switch to typing mode
            textOverlay.setVisibility(View.VISIBLE);
            textOverlay.requestFocus();
            drawingView.setVisibility(View.GONE);
            toggleButton.setText("Toggle to Drawing");
        }
    }

    // Save the current drawing and text content
    private void saveContent() {
        // Save the drawing as a Bitmap
        savedBitmap = Bitmap.createBitmap(drawingView.getWidth(), drawingView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(savedBitmap);
        drawingView.draw(canvas);

        // Get the text content
        String textContent = textOverlay.getText().toString();

        // Convert the bitmap to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        savedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapBytes = stream.toByteArray();

        // Return the data to the previous activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("drawing", bitmapBytes);
        resultIntent.putExtra("text", textContent);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // Custom View for Drawing
    private class DrawingView extends View {
        private Paint paint;
        private Canvas canvas;
        private Bitmap bitmap;
        private float lastX, lastY;

        public DrawingView(Activity context) {
            super(context);

            // Initialize the paint
            paint = new Paint();
            paint.setColor(Color.BLUE); // Drawing color
            paint.setStrokeWidth(8f);  // Line thickness
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);

            // Initialize the bitmap
            bitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(bitmap, 0, 0, null);
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
