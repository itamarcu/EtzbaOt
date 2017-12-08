package com.example.cukier.shemboard;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ShemboardService extends InputMethodService
{
    public ShemboardService()
    {
    }
    
    LinearLayout inputView;
    ArrayList<Button> buttons = new ArrayList<>();
    
    @Override
    public View onCreateInputView()
    {
        inputView = (LinearLayout) getLayoutInflater().inflate(
                R.layout.input, null);
        this.setupLayout();
        return inputView;
    }
    
    void setupLayout()
    {
        buttons.add((Button) inputView.findViewById(R.id.button1));
        buttons.add((Button) inputView.findViewById(R.id.button2));
        buttons.add((Button) inputView.findViewById(R.id.button3));
        buttons.add((Button) inputView.findViewById(R.id.button4));
        buttons.add((Button) inputView.findViewById(R.id.button5));
        
        for (int i = 0; i < buttons.size(); i++)
        {
            final int ii = i;
            Button b = this.buttons.get(ii);
            b.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    getCurrentInputConnection().commitText(Integer.toString(ii), 1);
                }
            });
        }
    }
    
    
}
