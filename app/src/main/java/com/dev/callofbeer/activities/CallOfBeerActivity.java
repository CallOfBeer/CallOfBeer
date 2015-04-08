package com.dev.callofbeer.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dev.callofbeer.R;
import com.dev.callofbeer.fragments.CobFloatingMenu;
import com.dev.callofbeer.fragments.AuthenticationFragment;
import com.dev.callofbeer.fragments.CreateEventFragment;
import com.dev.callofbeer.fragments.MapFragment;
import com.dev.callofbeer.models.authentication.Authentication;
import com.dev.callofbeer.models.authentication.User;
import com.dev.callofbeer.models.authentication.UserSave;
import com.dev.callofbeer.utils.UserManager;
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
    private CobFloatingMenu menu;

    private UserManager userManager;

    private RelativeLayout loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingProgress = (RelativeLayout) findViewById(R.id.globalProgress);

        userManager = new UserManager(this, new UserManager.OnUserEventsListener() {
            @Override
            public void onUserRegistered(User user) {
                Toast.makeText(getBaseContext(), "Welcome " + user.getUsername() + "!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserRegisterFailed() {
                Toast.makeText(getBaseContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserLogged(Authentication authentication) {
                Toast.makeText(getBaseContext(), "You're now logged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserLoginFailed() {
                Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserPulled(UserSave userSave) {
                Toast.makeText(getBaseContext(), userSave.getUser().getUsername() + " pulled !", Toast.LENGTH_SHORT).show();
                toogleProgressBar(false);
            }

            @Override
            public void onUserPulledError() {
                Toast.makeText(getBaseContext(), "User Pull Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserSaved(UserSave userSave) {
                Toast.makeText(getBaseContext(), "User Saved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserLogout() {
                Toast.makeText(getBaseContext(), "Logged out", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onUserUnlogged() {
                Toast.makeText(getBaseContext(), "No User Logged", Toast.LENGTH_SHORT).show();
            }
        });

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.relativelayout_map, new MapFragment());
        fragmentTransaction.commit();

        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.slidinguppanellayout_layout);
        mSlidingLayout.setAnchorPoint(0.7f);
        mSlidingLayout.setPanelSlideListener(new SlidingUpPanelView());

        mSlidingLayout.setDragView(findViewById(R.id.cob_draggable_panel));

        menu = (CobFloatingMenu) findViewById(R.id.multiple_actions);
        menu.setActivity(this);

        FragmentTransaction fragmentTransactionPanel = fragmentManager.beginTransaction();
        fragmentTransactionPanel.replace(R.id.main_container, new CreateEventFragment());
        fragmentTransactionPanel.commit();
    }

    public UserManager getUserManager() {
        return userManager;
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
        if (uri != null && uri.getEncodedSchemeSpecificPart().equals("//auth") && getFragmentManager().findFragmentById(R.id.main_container) instanceof AuthenticationFragment) {
            userManager.newCobLoginIntent(intent);
        }
        super.onNewIntent(intent);
    }

    public void startLogin() {
        AuthenticationFragment authenticationFragment = AuthenticationFragment.newInstance(true);
        getFragmentManager().beginTransaction().replace(R.id.main_container, authenticationFragment).commit();
        mSlidingLayout.expandPanel(0.7f);
    }

    public void toogleProgressBar(boolean b) {
        if (b) {
            loadingProgress.setVisibility(View.VISIBLE);
        } else {
            loadingProgress.setVisibility(View.GONE);
        }
    }
}
