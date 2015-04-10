package com.dev.callofbeer.authentication.network;

import android.net.Uri;
import android.os.AsyncTask;

import com.dev.callofbeer.authentication.models.Authorization;
import com.dev.callofbeer.authentication.utils.Config;
import com.dev.callofbeer.authentication.utils.UserManager;
import com.dev.callofbeer.models.authentication.User;
import com.dev.callofbeer.network.API;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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

        try {
            URL url = new URL(Config.COB_BASE_URL + "/app_dev.php/oauth/v2/token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            if (grantType.equals("password")) {
                writer.write("username=" + username
                        + "&password=" + password
                        + "&client_id=" + client_id
                        + "&client_secret=" + client_secret
                        + "&grant_type=" + grantType);
            } else if (grantType.equals("refresh_token")) {
                writer.write("refresh_token" + refreshToken
                        + "&client_id=" + client_id
                        + "&client_secret=" + client_secret
                        + "&grant_type=" + grantType
                        + "&scope=" + scope);
            }
            writer.flush();

            connection.connect();

            int test = connection.getResponseCode();

            InputStream inputStream = connection.getInputStream();

            String response = API.InputStreamToString(inputStream);

            ObjectMapper mapper = new ObjectMapper();
            Authorization authorization = mapper.readValue(response, Authorization.class);

            writer.close();

            return authorization;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Authorization result) {
        super.onPostExecute(result);
    }
}