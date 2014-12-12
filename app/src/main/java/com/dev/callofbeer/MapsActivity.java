package com.dev.callofbeer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_maps);
        // What do ? at start app
        LaunchControl();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // What do ? at return app
        LaunchControl();
    }

    private void LaunchControl() {
        setUpMapIfNeeded();
        boolean a = HttpTest(MapsActivity.this);
        if(a == true)
            Log.w("INTERNET", "TRUE");
        else
            Log.w("INTERNET", "False");


        // GPS
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    /*------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------*/
    // FUNCTION UTIL
    /*------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------*/

    /*** check network*/
    public static boolean HttpTest(final Activity mActivity)
    {
        Log.d("HttpTest", " -------------------------");
        if( !isNetworkAvailable( mActivity) )
        {
            Log.d("HttpTest"," network is not aviavalable");
            AlertDialog.Builder builders = new AlertDialog.Builder(mActivity);
            builders.setTitle("Sorry, There is no Network Available");
            LayoutInflater _inflater = LayoutInflater.from(mActivity);
            View convertView = _inflater.inflate(R.layout.none_access_internet,null);


            builders.setView(convertView);
            builders.setPositiveButton("Sure",  new DialogInterface.OnClickListener(){

                public void onClick(DialogInterface dialog, int which)
                {
                    mActivity.finish();
                }
            });
            builders.show();
            return false;
        }
        else
        {
            Log.d("HttpTest", " --------The network is available-----------------");
            return true;
        }
    }

    public static boolean isNetworkAvailable( Activity mActivity )
    {
        Context context = mActivity.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null)
        {
            Log.d("isNetworkAvailable"," connectivity is null");
            return false;
        }
        else
        {
            Log.d("isNetworkAvailable"," connectivity is not null");

            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                Log.d("isNetworkAvailable"," info is not null");
                for (int i = 0; i <info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                    else
                        Log.d("isNetworkAvailable"," info["+i+"] is not connected");
                }
            }
            else
                Log.d("isNetworkAvailable"," info is null");
        }
        return false;
    }
}
