package com.dev.callofbeer.authentication.utils;

import android.util.Log;

import com.dev.callofbeer.authentication.models.Authorization;
import com.dev.callofbeer.authentication.network.LoginTask;
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
public class UserManager {

    private static final String COB_CLIENT_ID       = "4_u6w4w4gzngg0cwcw8848sss48k00o804ooc8cgw0cswksko4w";
    private static final String COB_CLIENT_SECRET   = "5o37hhqei0848c0ck04wcgg0scsw0wgkwog000k0wgko8o0cc4";

    public static Authorization userSignIn(String username, String password, String authTokenType) {
        try {
            return new LoginTask(COB_CLIENT_ID, COB_CLIENT_SECRET, "password", username, password, null, null).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Authorization login(String client_id, String client_secret, String grantType, String username, String password, String refreshToken, String scope) {
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

    public static User register(String client_id, String client_secret, String username, String email, String password) {
        try {
            URL url = new URL(Config.COB_BASE_URL + "/app_dev.php/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("username=" + username + "&email=" + email + "&password=" + password + "&client_id=" + client_id + "&client_secret=" + client_secret);
            writer.flush();
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            String response = API.InputStreamToString(inputStream);

            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(response, User.class);

            writer.close();

            if (connection.getResponseCode() == 200) {
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
