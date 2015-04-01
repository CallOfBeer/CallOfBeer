package com.dev.callofbeer.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.dev.callofbeer.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.List;

public class CobFloatingMenu extends FloatingActionsMenu {

    public CobFloatingMenu(Context context) {
        super(context);
        initMenu();
    }

    public CobFloatingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMenu();
    }

    public CobFloatingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initMenu();
    }

    public void addListButton(List<FloatingActionButton> buttons) {
        for (FloatingActionButton button : buttons) {
            this.addButton(button);
        }
    }

    public void initMenu() {
        FloatingActionButton button2 = new FloatingActionButton(getContext());
        button2.setColorNormal(getResources().getColor(R.color.A200));
        button2.setColorPressed(getResources().getColor(R.color.A100));
        button2.setSize(FloatingActionButton.SIZE_MINI);
        button2.setIcon(R.drawable.common_signin_btn_text_normal_light);
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Coucou", Toast.LENGTH_SHORT).show();
            }
        });
        this.addButton(button2);

        FloatingActionButton button3 = new FloatingActionButton(getContext());
        button3.setColorNormal(getResources().getColor(R.color.R600));
        button3.setColorPressed(getResources().getColor(R.color.R400));
        button3.setIcon(R.drawable.common_signin_btn_text_normal_light);
        button3.setSize(FloatingActionButton.SIZE_MINI);
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Caca", Toast.LENGTH_SHORT).show();
            }
        });
        this.addButton(button3);
    }
}
