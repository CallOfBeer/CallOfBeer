package com.dev.callofbeer.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.dev.callofbeer.R;
import com.dev.callofbeer.fragments.CobFloatingMenu;
import com.dev.callofbeer.fragments.AuthenticationFragment;
import com.dev.callofbeer.fragments.MapFragment;
import com.dev.callofbeer.fragments.PanelFragment;
import com.dev.callofbeer.models.Authentication;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class CallOfBeerActivity extends FragmentActivity implements AuthenticationFragment.OnAuthenticationFragmentInteractionListener {

    public static FragmentManager fragmentManager;

    private SlidingUpPanelLayout mSlidingLayout;
    private CobFloatingMenu menu;

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

        menu = (CobFloatingMenu) findViewById(R.id.multiple_actions);

        FragmentTransaction fragmentTransactionPanel = fragmentManager.beginTransaction();
        fragmentTransactionPanel.replace(R.id.main_container, new PanelFragment());
        fragmentTransactionPanel.commit();
    }

    private class SlidingUpPanelView implements SlidingUpPanelLayout.PanelSlideListener {
        @Override
        public void onPanelSlide(View view, float v) {
            if(v >= 0.7f) {
                mSlidingLayout.expandPanel(0.7f);
            }
        }

        @Override
        public void onPanelCollapsed(View view) {}
        @Override
        public void onPanelExpanded(View view) {}
        @Override
        public void onPanelAnchored(View view) {}
        @Override
        public void onPanelHidden(View view) {}
    }

    public void forcedSlidingUp(){
        mSlidingLayout.expandPanel(0.7f);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri.getEncodedSchemeSpecificPart().equals("//auth") && getFragmentManager().findFragmentById(R.id.main_container) instanceof AuthenticationFragment) {
            ((AuthenticationFragment) getFragmentManager().findFragmentById(R.id.main_container)).newCobIntent(intent);
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onUserLogged() {
        Authentication authentication = getUserAuth();

        if (authentication != null) {
            Toast.makeText(this, "Welcome back !", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUserLoggingFailed() {
        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show();
    }

    public Authentication getUserAuth() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String authString = sharedPref.getString("user", "null");
        ObjectMapper mapper = new ObjectMapper();
        Authentication authentication = null;
        if (!authString.equals("null")) {
            try {
                authentication = mapper.readValue(authString, Authentication.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return authentication;
    }
}
