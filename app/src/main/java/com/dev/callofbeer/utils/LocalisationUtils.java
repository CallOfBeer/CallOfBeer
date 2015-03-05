package com.dev.callofbeer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by matth on 04/03/15.
 */
public class LocalisationUtils {


    /**
     *  Check if the localisation is available on the device and show a popup dialog
     *  to enable the GPS if it's not.
     *
     * @param mActivity
     * @return true if localisation is activated on the device otherwise false
     */
    public static boolean isLocalisationAvailable(final Activity mActivity)
    {
        LocationManager locManager=(LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnableGPS=locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isEnableNTW=locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("Localisation", isEnableGPS + ", " + isEnableNTW);

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
