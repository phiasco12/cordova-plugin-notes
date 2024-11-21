package com.example.notesplugin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class NoteEditorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a FrameLayout as the main container
        FrameLayout layout = new FrameLayout(this);

        // Add a custom view for drawing (e.g., a Canvas)
        View drawingCanvas = new View(this);
        drawingCanvas.setBackgroundColor(Color.LTGRAY); // Example background
        layout.addView(drawingCanvas);

        // Add any other UI elements (e.g., text overlay)
        // (You can create an EditText programmatically if needed)

        // Set the layout as the content view
        setContentView(layout);
    }
}

