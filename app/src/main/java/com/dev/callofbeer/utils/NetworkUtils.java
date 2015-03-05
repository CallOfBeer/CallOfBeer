package com.dev.callofbeer.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.dev.callofbeer.R;

/**
 * Created by admin on 12/12/2014.
 */
public class NetworkUtils {

    /**
     *  Check if the network isn't available then show a popup dialog to redirect on
     *  wifi settings
     *
     * @param mActivity
     * @return true if the device is connected otherwise false
     */
    public static boolean HttpTest(final Activity mActivity)
    {
        Log.d("HttpTest", " -------------------------");
        if( !isNetworkAvailable( mActivity) )
        {
            Log.d("HttpTest"," network is not aviavalable");
            AlertDialog.Builder builders = new AlertDialog.Builder(mActivity);
            builders.setTitle("Sorry, There is no Network Available");
            LayoutInflater _inflater = LayoutInflater.from(mActivity);
            View convertView = _inflater.inflate(R.layout.view_dialog_access_internet,null);


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


    /**
     *
     * Check if the network is available on the device:
     *     info: NetworkInfo[] -> check differents types of connection (GPS and/or Wifi)
     *
     * @param mActivity
     * @return true if the network is available otherwise false
     */
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
