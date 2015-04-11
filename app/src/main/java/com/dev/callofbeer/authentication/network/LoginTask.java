package com.dev.callofbeer.authentication.network;

import android.os.AsyncTask;

import com.dev.callofbeer.authentication.models.Authorization;
import com.dev.callofbeer.authentication.utils.UserManager;

/**
 * Created by martin on 02/04/15.
 */
public class LoginTask extends AsyncTask<String, String, Authorization> {

    private String client_id;
    private String client_secret;
    private String username;
    private String password;
    private String scope;
    private String grantType;
    private String refreshToken;

    public LoginTask(String client_id, String client_secret, String grantType, String username, String password, String refreshToken, String scope) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.username = username;
        this.password = password;
        this.scope = scope;
        this.grantType = grantType;
        this.refreshToken = refreshToken;
    }

    @Override
    protected Authorization doInBackground(String... urls) {

        return UserManager.login(client_id, client_secret, grantType, username, password, refreshToken, scope);
    }

    @Override
    protected void onPostExecute(Authorization result) {
        super.onPostExecute(result);
    }
}