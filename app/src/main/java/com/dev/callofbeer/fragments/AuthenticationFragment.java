package com.dev.callofbeer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dev.callofbeer.R;
import com.dev.callofbeer.activities.CallOfBeerActivity;
import com.dev.callofbeer.models.Authentication;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.dev.callofbeer.fragments.AuthenticationFragment.OnAuthenticationFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AuthenticationFragment extends Fragment {

    public static final String COB_BASE_URL = "http://api.auth.callofbeer.com";
    public static final String COB_CLIENT_ID = "2_3otx4kun3x44cwogc8o080gkckc0ggwwoocsosog44kw8w8s4k";
    public static final String COB_REDIRECT_URI = "cob://auth";
    public static final String COB_RESPONSE_TYPE = "token";

    private OnAuthenticationFragmentInteractionListener mListener;

    private WebView authWebView;
    private boolean toLogout = false;

    public AuthenticationFragment() {
    }

    public static AuthenticationFragment newInstance(boolean loginAction) {
        AuthenticationFragment myFragment = new AuthenticationFragment();

        Bundle args = new Bundle();
        args.putBoolean("loginAction", loginAction);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_authentication, container, false);

        authWebView = (WebView) root.findViewById(R.id.authWebView);
        authWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("cob")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url), (CallOfBeerActivity) mListener, CallOfBeerActivity.class);
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });

        if (getArguments().getBoolean("loginAction")) {
            authWebView.loadUrl(COB_BASE_URL + "/oauth/v2/auth?client_id=" + COB_CLIENT_ID + "&redirect_uri=" + COB_REDIRECT_URI + "&response_type=" + COB_RESPONSE_TYPE);
        } else {
            authWebView.loadUrl(COB_BASE_URL + "/oauth/v2/auth/logout");
            if (getActivity() != null) {
                toLogout = true;
            } else {
                SharedPreferences preferences  = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user", "null");
                editor.apply();
            }
        }

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAuthenticationFragmentInteractionListener) activity;
            if (toLogout) {
                SharedPreferences preferences  = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user", "null");
                editor.apply();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void newCobIntent(Intent intent) {
        Uri uri = intent.getData();
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
        authentication.setType("bearer");

        ObjectMapper mapper = new ObjectMapper();
        String authString = null;
        try {
            authString = mapper.writeValueAsString(authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (authString == null) {
            mListener.onUserLoggingFailed();
            return;
        }
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", authString);
        editor.apply();

        mListener.onUserLogged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAuthenticationFragmentInteractionListener {
        public void onUserLogged();
        public void onUserLoggingFailed();
    }

}
