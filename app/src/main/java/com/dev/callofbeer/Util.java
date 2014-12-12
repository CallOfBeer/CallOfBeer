package com.dev.callofbeer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by admin on 12/12/2014.
 */
public class Util {

    /*------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------*/
    // FUNCTION UTIL -- INTERNET
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

    /*------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------*/
    // FUNCTION UTIL -- Localisation
    /*------------------------------------------------------------------------------*/
    /*------------------------------------------------------------------------------*/

    public static boolean isLocalisationAvailable(final Activity mActivity)
    {
        LocationManager locManager=(LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnableGPS=locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isEnableNTW=locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("Localisation", isEnableGPS+", "+isEnableNTW);

        final LocationManager manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            //Afficher erreur ou config GPS
            Log.d("LocalisationTest"," Localisation is not aviavalable");
            AlertDialog.Builder builders = new AlertDialog.Builder(mActivity);
            builders.setTitle("Sorry, There is no Localisation Available");
            builders.setPositiveButton("Open setting",  new DialogInterface.OnClickListener(){

                public void onClick(DialogInterface dialog, int which)
                {
                    Intent i = new Intent( android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS) ;
                    mActivity.startActivityForResult(i, 1);
                }
            });
            builders.show();
            return false;
        }
        else
        {
            Log.d("LocalisationTest", " --------The Localisation is available-----------------");
            return true;
        }
    }
}
