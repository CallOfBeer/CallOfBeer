package com.dev.callofbeer.authentication.network;

import android.os.AsyncTask;

import com.dev.callofbeer.authentication.utils.Config;
import com.dev.callofbeer.models.authentication.Authentication;
import com.dev.callofbeer.models.authentication.User;
import com.dev.callofbeer.models.authentication.UserSave;
import com.dev.callofbeer.authentication.utils.UserManager;
import com.dev.callofbeer.network.API;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by martin on 02/04/15.
 */
public class UserTask extends AsyncTask<String, String, User> {

    private String authToken;

    public UserTask(String authToken) {
        this.authToken = authToken;
    }

    @Override
    protected User doInBackground(String... urls) {

        try {
            URL url = new URL(Config.COB_BASE_URL + "/app_dev.php/me");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer" + authToken);

            connection.connect();

            InputStream inputStream = connection.getInputStream();

            String response = API.InputStreamToString(inputStream);

            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(response, User.class);

            if (connection.getResponseCode() == 200) {
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
