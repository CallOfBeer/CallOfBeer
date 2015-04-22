package com.dev.callofbeer.fragments;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.callofbeer.R;
import com.dev.callofbeer.authentication.network.UserTask;
import com.dev.callofbeer.authentication.utils.Config;
import com.dev.callofbeer.models.authentication.User;
import com.dev.callofbeer.network.API;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private TextView usernameView;
    private TextView emailView;
    private Button logoutbutton;

    private User user;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        usernameView = (TextView) root.findViewById(R.id.cob_user_username);
        emailView = (TextView) root.findViewById(R.id.cob_user_email);
        logoutbutton = (Button) root.findViewById(R.id.cob_user_logout);
        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountManager accountManager = AccountManager.get(getActivity());
                Account[] accts = accountManager.getAccountsByType(Config.COB_USER_TYPE);
                Account acct = accts[0];
                accountManager.removeAccount(acct, null, null);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        AccountManager accountManager = AccountManager.get(getActivity());
        Account[] accts = accountManager.getAccountsByType(Config.COB_USER_TYPE);
        if (accts.length == 0) {
            accountManager.addAccount(Config.COB_USER_TYPE, Config.COB_USER_TYPE, null, null, getActivity(), new OnAccountAcquired(), null);
        } else {
            Account acct = accts[0];
            accountManager.getAuthToken(acct, Config.COB_USER_TYPE, null, getActivity(), new OnTokenAcquired(), null);
        }
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, 1);
                } else {
                    String token = bundle
                            .getString(AccountManager.KEY_AUTHTOKEN);
                    new UserTestTask(token).execute();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class OnAccountAcquired implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, 1);
                    AccountManager accountManager = AccountManager.get(getActivity());
                    Account[] accts = accountManager.getAccountsByType(Config.COB_USER_TYPE);
                    Account acct = accts[0];
                    accountManager.getAuthToken(acct, Config.COB_USER_TYPE, null, getActivity(), new OnTokenAcquired(), null);
                } else {
                    Log.i("I don't", "understand");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class UserTestTask extends AsyncTask<Void, Void, Void> {

        private String authToken;

        public UserTestTask(String token) {
            authToken = token;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(Config.COB_BASE_URL + "/app_dev.php/me");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + authToken);

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                String response = API.InputStreamToString(inputStream);

                ObjectMapper mapper = new ObjectMapper();
                User userR = mapper.readValue(response, User.class);

                if (userR != null) {
                    user = userR;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            usernameView.setText(user.getUsername());
                            emailView.setText(user.getEmail());
                        }
                    });
                    usernameView.setText(user.getUsername());
                    emailView.setText(user.getEmail());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
