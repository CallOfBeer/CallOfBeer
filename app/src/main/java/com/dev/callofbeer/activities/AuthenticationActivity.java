package com.dev.callofbeer.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.dev.callofbeer.R;
import com.dev.callofbeer.network.API;

public class AuthenticationActivity extends ActionBarActivity {

    public static final String API_BASE = "http://api.auth.callofbeer.com";
    public static final String COB_CLIENT_ID = "2_3otx4kun3x44cwogc8o080gkckc0ggwwoocsosog44kw8w8s4k";
    public static final String COB_REDIRECT_URI = "cob://auth";

    WebView authWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        authWebView = (WebView) findViewById(R.id.auth_webview);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_authentication, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        authWebView.loadUrl(API_BASE + "/oauth/v2/auth?client_id="+COB_CLIENT_ID+"&redirect_uri="+COB_REDIRECT_URI+"&response_type=token");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getData().getPath().equals("auth")){
            Log.d("access_token", intent.getData().getQueryParameter("access_token"));
        }
    }
}
