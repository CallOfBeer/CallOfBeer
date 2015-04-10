package com.dev.callofbeer.authentication.network;

import android.os.AsyncTask;
import android.util.Log;

import com.dev.callofbeer.models.authentication.User;
import com.dev.callofbeer.authentication.utils.UserManager;
import com.dev.callofbeer.network.API;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by martin on 02/04/15.
 */
public class RegisterTask extends AsyncTask<String, String, User> {

    public static final String COB_BASE_URL = "http://api.auth.callofbeer.com";
    public static final String COB_CLIENT_ID = "1_rdr03kmwqlw8880kgkwo0c00g4og40k4ccsc0o4sc4wk8c0gg";

    private UserManager userManager;
    private String username;
    private String email;
    private String password;

    public RegisterTask(UserManager um, String username, String email, String password) {
        userManager = um;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    protected User doInBackground(String... urls) {

        try {
            URL url = new URL(COB_BASE_URL + "/app_dev.php/users");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("username=" + username + "&email=" + email + "&password=" + password + "&client_id=" + COB_CLIENT_ID);
            writer.flush();
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            String response = API.InputStreamToString(inputStream);

            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(response, User.class);

            writer.close();

            if (connection.getResponseCode() == 200) {
                Log.i("Register", connection.getResponseMessage());
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