package com.example.cukier.shemboard;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.inputmethodservice.InputMethodService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ShemboardService extends InputMethodService
{
    private KeyMap.Map map;
    private LinearLayout inputView;
    private boolean[] currentlyPressedButtons = new boolean[5];
    private boolean[] previouslyPressedButtons = new boolean[5];
    private int specialMode;
    private ImageView imageView6;
    private Canvas canvas6;
    private Bitmap bitmap6;
    private List<Point> fingerPoints;
    
    public ShemboardService()
    {
        map = KeyMap.Map.ABDHP;
        resetButtonPresses();
        fingerPoints = new ArrayList<>();
    }
    
    private void resetButtonPresses()
    {
        for (int i = 0; i < 5; i++)
        {
            currentlyPressedButtons[i] = false;
            previouslyPressedButtons[i] = false;
        }
        
        specialMode = 0;
    }
    
    @Override
    public View onCreateInputView()
    {
        inputView = (LinearLayout) getLayoutInflater().inflate(
                R.layout.input, null);
        this.setupLayout();
        return inputView;
    }
    
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting)
    {
        super.onStartInputView(info, restarting);
        
        
        //Set size to maximum screen
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        
        ViewGroup.LayoutParams params = inputView.getLayoutParams();
        params.height = height;
        inputView.setLayoutParams(params);
        
        //Image view
        imageView6 = inputView.findViewById(R.id.imageView6);
        //create a new canvas with a transparent background
        bitmap6 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        canvas6 = new Canvas(bitmap6);
        updateCanvas();
    }
    
    void setupLayout()
    {
        inputView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return ShemboardService.this.onTouch(view, motionEvent);
            }
        });
        
    }
    
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        Point touchPlace = new Point();
        touchPlace.set((int) motionEvent.getX(), (int) motionEvent.getY());
        Paint pointPaint = new Paint();
        pointPaint.setARGB(128, 60, 200, 0);
        canvas6.drawCircle(touchPlace.x, touchPlace.y, 10, pointPaint);
        updateCanvas();
        return false;
    }
    
    private void updateCanvas()
    {
        imageView6.setImageBitmap(bitmap6);
    }
    
    private void onAllButtonsReleased()
    {
        
        char character = '&';
        int binary = 0;
        for (int i = 0; i < 5; i++)
            if (previouslyPressedButtons[i])
                binary += 1 << i; // 2 to the power of i
        
        
        if (binary >= 32)
            Log.d("Binary", "ERROR: binary number too large: " + binary);
        else
            character = map.decode(binary);
        
        if (specialMode == 0) //no special char
        {
            if (character == '#') //None
            {
                getCurrentInputConnection().commitText("#", 1);
            }
            else if (character == '-') //Backspace
            {
                getCurrentInputConnection().deleteSurroundingText(-1, 0);
            }
            else if (character == '$') //Special
            {
            
            }
            else //Normal character
            {
                getCurrentInputConnection().commitText("" + character, 1);
            }
        }
        
        resetButtonPresses();
    }
    
}
