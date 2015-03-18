package com.dev.callofbeer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.dev.callofbeer.R;
import com.dev.callofbeer.fragments.MapFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by matth on 04/03/15.
 */

/**
 *
 * MainActivity
 *
 *
 */
public class CallOfBeerActivity extends FragmentActivity {

    public static FragmentManager fragmentManager;

    private SlidingUpPanelLayout mSlidingLayout;
    private FloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relativelayout_map, new MapFragment());
        fragmentTransaction.commit();

        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidinguppanellayout_layout);
        mSlidingLayout.setAnchorPoint(0.7f);
        mSlidingLayout.setPanelSlideListener(new SlidingUpPanelView());

        button = (FloatingActionButton) findViewById(R.id.fab_test);
        button.setTitle("Test de la mort");


    }

    private class SlidingUpPanelView implements SlidingUpPanelLayout.PanelSlideListener {
        @Override
        public void onPanelSlide(View view, float v) {
            if(v >= 0.7f) {
                mSlidingLayout.expandPanel(0.7f);
            }
        }

        @Override
        public void onPanelCollapsed(View view) {

        }

        @Override
        public void onPanelExpanded(View view) {

        }

        @Override
        public void onPanelAnchored(View view) {

        }

        @Override
        public void onPanelHidden(View view) {

        }
    }
}
