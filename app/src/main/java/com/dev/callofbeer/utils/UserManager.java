package com.dev.callofbeer.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Base64;

import com.dev.callofbeer.activities.CallOfBeerActivity;
import com.dev.callofbeer.models.authentication.Authentication;
import com.dev.callofbeer.models.authentication.User;
import com.dev.callofbeer.models.authentication.UserSave;
import com.dev.callofbeer.network.LoginTask;
import com.dev.callofbeer.network.RegisterTask;
import com.dev.callofbeer.network.UserTask;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by martin on 02/04/15.
 */
public class UserManager {

    private CallOfBeerActivity activity;
    private OnUserEventsListener mListener;

    public UserManager(CallOfBeerActivity activity) {
        this.activity = activity;
    }

    public UserManager(CallOfBeerActivity activity, OnUserEventsListener listener) {
        this.activity = activity;
        this.mListener = listener;
    }

    public void setOnUserEventsListener(OnUserEventsListener listener) {
        mListener = listener;
    }

    public OnUserEventsListener getOnUserEventsListener() {
        return mListener;
    }

    public void logByUsernamePassword(String username, String password) {
        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (username + ":" + password).getBytes(),
                Base64.NO_WRAP);
        new LoginTask(this, base64EncodedCredentials).execute();
    }

    public void loginResponseUri(Uri uri) {
        Intent i = new Intent(Intent.ACTION_VIEW, uri, activity, CallOfBeerActivity.class);
        activity.startActivity(i);
    }

    public void newCobLoginIntent(Intent intent) {
        Uri uri = intent.getData();
        Authentication authentication = UserManager.uriToAuthentication(uri);
        if (saveUser(null, authentication)){
            mListener.onUserLogged(authentication);
        } else {
            mListener.onUserLoginFailed();
        }
        pullUserFromApi(authentication);
    }

    public void registerByUsernameEmailPassword(String username, String email, String password) {
        new RegisterTask(this, username, email, password).execute();
    }

    public void registerResponse(User user, String password) {
        if (user != null) {
            logByUsernamePassword(user.getUsername(), password);
            mListener.onUserRegistered(user);
        } else {
            mListener.onUserRegisterFailed();
        }
    }

    public User getLocalUser() {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        if (preferences.contains("user")) {
            String userString = preferences.getString("user", "");
            ObjectMapper mapper = new ObjectMapper();
            UserSave userSave = null;
            try {
                userSave = mapper.readValue(userString, UserSave.class);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (userSave == null || (userSave.getAuthentication() == null && userSave.getUser() == null)) {
                return null;
            }
            if (userSave.getAuthentication() != null && userSave.getUser() == null) {
                return null;
            } else {
                return userSave.getUser();
            }
        } else {
            return null;
        }
    }

    public void pullUser() {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        if (preferences.contains("user")) {
            String userString = preferences.getString("user", "");
            ObjectMapper mapper = new ObjectMapper();
            UserSave userSave = null;
            try {
                userSave = mapper.readValue(userString, UserSave.class);
            } catch (Exception e) {
                e.printStackTrace();
                mListener.onUserPulledError();
            }
            if (userSave == null || (userSave.getAuthentication() == null && userSave.getUser() == null)) {
                mListener.onUserUnlogged();
            }
            if (userSave.getAuthentication() != null && userSave.getUser() == null) {
                pullUserFromApi(userSave.getAuthentication());
            } else {
                mListener.onUserPulled(userSave);
            }
        } else {
            mListener.onUserUnlogged();
        }
    }

    private void pullUserFromApi(Authentication authentication) {
        new UserTask(this, authentication).execute();
    }

    public void userPulled(UserSave userSave) {
        saveUser(userSave);
        mListener.onUserPulled(userSave);
    }

    public static Authentication uriToAuthentication(Uri uri) {
        Authentication authentication = new Authentication();
        String access_token = "";
        Pattern pattern = Pattern.compile("access_token=([0-9A-Za-z]{86})");
        Matcher matcher = pattern.matcher(uri.toString());
        while(matcher.find()) {
            access_token = matcher.group(1);
        }
        authentication.setAccess_token(access_token);
        Date date = new Date();
        authentication.setExpiration(new Date(date.getTime() + 3600000));
        authentication.setType("Bearer ");
        return authentication;
    }

    public boolean saveUser(User user, Authentication authentication) {
        UserSave userSave = new UserSave();
        userSave.setUser(user);
        userSave.setAuthentication(authentication);

        ObjectMapper mapper = new ObjectMapper();
        String authString = null;
        try {
            authString = mapper.writeValueAsString(userSave);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (authString == null) {
            return false;
        }
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", authString);
        editor.apply();
        mListener.onUserSaved(userSave);
        return true;
    }

    public void saveUser(UserSave user) {
        saveUser(user.getUser(), user.getAuthentication());
    }

    public void logoutUser() {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", "");
        editor.apply();
        mListener.onUserLogout();
    }

    public interface OnUserEventsListener {
        public void onUserRegistered(User user);
        public void onUserRegisterFailed();
        public void onUserLogged(Authentication authentication);
        public void onUserLoginFailed();
        public void onUserPulled(UserSave userSave);
        public void onUserPulledError();
        public void onUserSaved(UserSave userSave);
        public void onUserLogout();
        public void onUserUnlogged();
    }
}
