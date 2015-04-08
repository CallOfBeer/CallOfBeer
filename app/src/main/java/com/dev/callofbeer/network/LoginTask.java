package com.dev.callofbeer.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.dev.callofbeer.R;
import com.dev.callofbeer.fragments.AuthenticationFragment;
import com.dev.callofbeer.utils.UserManager;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by martin on 02/04/15.
 */
public class LoginTask extends AsyncTask<String, String, String> {

    public static final String COB_BASE_URL = "http://api.auth.callofbeer.com";
    public static final String COB_CLIENT_ID = "1_rdr03kmwqlw8880kgkwo0c00g4og40k4ccsc0o4sc4wk8c0gg";
    public static final String COB_REDIRECT_URI = "cob://auth";
    public static final String COB_RESPONSE_TYPE = "token";

    private UserManager manager;
    private String basic_auth;

    public LoginTask(UserManager ma, String st) {
        manager = ma;
        basic_auth = st;
    }

    @Override
    protected String doInBackground(String... urls) {

        try {
            URL url = new URL(COB_BASE_URL + "/app_dev.php/oauth/v2/auth?client_id=" + COB_CLIENT_ID + "&redirect_uri=" + COB_REDIRECT_URI + "&response_type=" + COB_RESPONSE_TYPE);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", basic_auth);

            connection.setInstanceFollowRedirects(false);

            connection.connect();

            if (connection.getResponseCode() == 302) {
                return connection.getHeaderField("Location");
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Uri uri = Uri.parse(result);
        manager.loginResponseUri(uri);
    }
}