package com.dev.callofbeer.authentication.activities;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.callofbeer.R;
import com.dev.callofbeer.authentication.models.Authorization;
import com.dev.callofbeer.authentication.utils.Config;
import com.dev.callofbeer.authentication.utils.UserManager;
import com.dev.callofbeer.models.authentication.User;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * A login screen that offers login via email/password.
 */
public class CobAuthenticatorActivity extends AccountAuthenticatorActivity {

    public static final String ARG_ACCOUNT_TYPE = "com.callofbeer";
    public static final String ARG_AUTH_TYPE = "";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private UserRegisterTask mRegisterTask = null;

    private MaterialEditText usernameEditText;
    private MaterialEditText passwordEditText;

    private MaterialEditText usernameRegisterEditText;
    private MaterialEditText emailRegisterEditText;
    private MaterialEditText passwordRegisterEditText;
    private MaterialEditText passwordRepeatRegisterEditText;

    private View mProgressView;
    private View mLoginFormView;
    private View mRegisterFormView;
    private View mActiveView;

    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cob_authenticator);

        if (accountManager == null) {
            accountManager = AccountManager.get(this);
        }

        mLoginFormView = findViewById(R.id.cob_authent_login_layout);
        mRegisterFormView = findViewById(R.id.cob_authent_register_layout);

        mProgressView = findViewById(R.id.login_progress);

        mActiveView = mLoginFormView;

        // Set up the login form.
        usernameEditText = (MaterialEditText) findViewById(R.id.cob_authent_login_username);

        passwordEditText = (MaterialEditText) findViewById(R.id.cob_authent_login_password);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENTER) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        usernameRegisterEditText = (MaterialEditText) findViewById(R.id.cob_authent_register_username);
        emailRegisterEditText = (MaterialEditText) findViewById(R.id.cob_authent_register_email);
        passwordRegisterEditText = (MaterialEditText) findViewById(R.id.cob_authent_register_password);
        passwordRepeatRegisterEditText = (MaterialEditText) findViewById(R.id.cob_authent_register_password_repeat);

        passwordRegisterEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordRepeatRegisterEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        passwordRepeatRegisterEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENTER) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mUsernameSignInButton = (Button) findViewById(R.id.cob_authent_login_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.cob_authent_register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        Button toRegisterButton = (Button) findViewById(R.id.cob_authent_to_register_button);
        toRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginFormView.setVisibility(View.GONE);
                mRegisterFormView.setVisibility(View.VISIBLE);
                mActiveView = mRegisterFormView;
            }
        });

        Button toLoginButton = (Button) findViewById(R.id.cob_authent_to_login_button);
        toLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterFormView.setVisibility(View.GONE);
                mLoginFormView.setVisibility(View.VISIBLE);
                mActiveView = mLoginFormView;
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        usernameEditText.setError(null);
        passwordEditText.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.error_field_required));
            focusView = usernameEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password, null);
            mAuthTask.execute((Void) null);
        }
    }

    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        usernameRegisterEditText.setError(null);
        emailRegisterEditText.setError(null);
        passwordRegisterEditText.setError(null);
        passwordRepeatRegisterEditText.setError(null);

        // Store values at the time of the login attempt.
        String username = usernameRegisterEditText.getText().toString();
        String email = emailRegisterEditText.getText().toString();
        String password = passwordRegisterEditText.getText().toString();
        String password2 = passwordRepeatRegisterEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordRegisterEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordRegisterEditText;
            cancel = true;
        }

        if (!password.equals(password2)) {
            passwordRepeatRegisterEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordRepeatRegisterEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailRegisterEditText.setError(getString(R.string.error_field_required));
            focusView = emailRegisterEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailRegisterEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailRegisterEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            usernameRegisterEditText.setError(getString(R.string.error_field_required));
            focusView = usernameRegisterEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mRegisterTask = new UserRegisterTask(username, email, password);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mActiveView.setVisibility(show ? View.GONE : View.VISIBLE);
            mActiveView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mActiveView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mActiveView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Authorization> {

        private final String mUsername;
        private final String mPassword;
        private final User mUser;

        UserLoginTask(String email, String password, User user) {
            mUsername = email;
            mPassword = password;
            mUser = user;
        }

        @Override
        protected Authorization doInBackground(Void... params) {
            try {
                return UserManager.login(Config.COB_CLIENT_ID, Config.COB_CLIENT_SECRET, "password", mUsername, mPassword, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Authorization authorization) {
            mAuthTask = null;
            showProgress(false);

            if (authorization != null && authorization.getAccess_token() != null) {
                Account account = new Account(mUsername, Config.COB_USER_TYPE);

                Bundle userBundle = new Bundle();

                try {
                    if (mUser != null) {
                        userBundle.putString("username", mUser.getUsername());
                        userBundle.putString("email", mUser.getEmail());
                        userBundle.putInt("id", mUser.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    userBundle = null;
                }

                accountManager.addAccountExplicitly(account, mPassword, userBundle);

                final Intent intent = new Intent();
                intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
                intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Config.COB_USER_TYPE);
                intent.putExtra(AccountManager.KEY_AUTHTOKEN, authorization.getAccess_token());
                setAccountAuthenticatorResult(intent.getExtras());
                setResult(RESULT_OK, intent);

                finish();
            } else if (authorization != null && authorization.getError().equals("invalid_grant")){
                passwordEditText.setError(getString(R.string.error_incorrect_password));
                passwordEditText.requestFocus();
            } else {
                Toast.makeText(getApplicationContext(), "Internal Error", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, User> {

        private final String mEmail;
        private final String mPassword;
        private final String mUsername;

        UserRegisterTask(String username, String email, String password) {
            mUsername = username;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected User doInBackground(Void... params) {
            try {
                return UserManager.register(Config.COB_CLIENT_REGISTER_ID, Config.COB_CLIENT_SECRET, mUsername, mEmail, mPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final User user) {
            mRegisterTask = null;

            if (user != null) {
                new UserLoginTask(mUsername, mPassword, user).execute();
            } else {
                Toast.makeText(getApplicationContext(), "Registration Error", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }
    }
}



