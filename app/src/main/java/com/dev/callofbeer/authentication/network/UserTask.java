package com.dev.callofbeer.authentication.network;

import android.os.AsyncTask;

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
public class UserTask extends AsyncTask<String, String, UserSave> {
    public static final String COB_BASE_URL = "http://api.auth.callofbeer.com";
    public static final String COB_CLIENT_ID = "1_rdr03kmwqlw8880kgkwo0c00g4og40k4ccsc0o4sc4wk8c0gg";

    private UserManager userManager;
    private Authentication authentication;

    public UserTask(UserManager um, Authentication authentication) {
        userManager = um;
        this.authentication = authentication;
    }

    @Override
    protected UserSave doInBackground(String... urls) {

        try {
            URL url = new URL(COB_BASE_URL + "/app_dev.php/me");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", authentication.getType() + authentication.getAccess_token());

            connection.connect();

            InputStream inputStream = connection.getInputStream();

            String response = API.InputStreamToString(inputStream);

            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.readValue(response, User.class);

            if (connection.getResponseCode() == 200) {
                UserSave userSave = new UserSave();
                userSave.setAuthentication(authentication);
                userSave.setUser(user);
                return userSave;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(UserSave result) {
        //userManager.userPulled(result);
    }
}
