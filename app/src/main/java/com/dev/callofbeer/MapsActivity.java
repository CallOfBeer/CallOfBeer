package com.dev.callofbeer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements View.OnClickListener,LocationListener,GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager lManager;
    private Location location;
    private String provider;
    private Criteria criteria;
    private Marker markerMe = null;
    private ArrayList<Marker> marker = new ArrayList<Marker>();
    private int compteurMarker = 0;
    private CameraUpdate update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Permet la connexion et récupération JSON
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_maps);
        findViewById(R.id.igab).setOnClickListener(this);

        // What do ? at start app
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // What do ? at return app
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                if(Util.HttpTest(MapsActivity.this)){ // if network is good
                 if(Util.isLocalisationAvailable(MapsActivity.this)){ // if network is good
                        if(markerMe == null){
                            mMap.setOnCameraChangeListener(this);
                            LatLng pos = takePosition();
                            update = CameraUpdateFactory.newLatLngZoom(pos,15);
                            markerMe = mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .title("You")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icone_me)));
                            mMap.animateCamera(update);
                        }
                        setUpMap();
                     }else{/* Else Nothing, because AlertDialog open !!! */}
                 }else{/* Else Nothing, because AlertDialog open !!! */}
            }
        }
    }

    /*--------------------------------------------------------------------------*/
                                /* take Event */
    /*--------------------------------------------------------------------------*/
    private void setUpMap() {
        ArrayList<EventBeer> tab = API.getEvents(takeMarker());

        for(int j = 0 ; j < tab.size() ; j++){
            Double lat = tab.get(j).getLat();
            Double lon = tab.get(j).getLongi();
            LatLng pos = new LatLng(lat,lon);
            String name = tab.get(j).getNomEvent();
            Log.e("LATLON",lat.toString());
            mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_biere)));
        }
    }

    private ArrayList<LatLng> takeMarker(){
        LatLng topLeft = mMap.getProjection().getVisibleRegion().farLeft;
        LatLng bottomRight = mMap.getProjection().getVisibleRegion().nearRight;
        ArrayList<LatLng> screen = new ArrayList<LatLng>();
        screen.add(topLeft);screen.add(bottomRight);
        return screen;
    }


    /*--------------------------------------------------------------------------*/
                                /* send Event */
    /*--------------------------------------------------------------------------*/
    public Dialog onCreateDialog(final LatLng position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        final View myView = inflater.inflate(R.layout.add_event, null);
        builder.setView(myView);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        // Add action buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Calendar calendar = Calendar.getInstance();
                java.util.Date now = calendar.getTime();
                java.sql.Timestamp time = new java.sql.Timestamp(now.getTime());
                long myTime = time.getTime();

                myTime = Math.round(myTime/1000);

                EditText editText = (EditText) myView.findViewById(R.id.nomevent);
                String message = editText.getText().toString();

                EventBeer event = new EventBeer(message,myTime, position.latitude,position.longitude);
                API.sendEvent(event);
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
    /*--------------------------------------------------------------------------*/
                                /* code function app */
    /*--------------------------------------------------------------------------*/
    private void clickIGotABeer() {
        if(Util.isLocalisationAvailable(MapsActivity.this)) {
            LatLng pos = takePosition();
            markerMe.setPosition(pos);
            update = CameraUpdateFactory.newLatLngZoom(takePosition(), 15);
            mMap.animateCamera(update);
            if((pos.latitude == 44.84403344)&&(pos.longitude == -0.58759689)){
                // Value not changed - GPS none
                Toast.makeText(this, "Sorry, GPS not available", Toast.LENGTH_LONG).show();
            }else{
                Dialog dia = onCreateDialog(pos);
                dia.show();
            }
        }
    }

    public LatLng takePosition(){
        LatLng pos = new LatLng(44.84403344,-0.58759689); // Bordeaux, centre-ville
        try {
            lManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            boolean isNetworkEnabled = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, this);
                    Log.d("Network", "Network Enabled");
                    if (lManager != null) {
                        location = lManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();
                            pos = new LatLng(latitude,longitude);
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        lManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,0,0, this);
                        Log.d("GPS", "GPS Enabled");
                        if (lManager != null) {
                            location = lManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                Double latitude = location.getLatitude();
                                Double longitude = location.getLongitude();
                                pos = new LatLng(latitude,longitude);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace();}
        return pos;
    }


    /*-----------------------------------------------------------------------*/
                                /* implement methods */
    /*-----------------------------------------------------------------------*/
    @Override
    public void onLocationChanged(Location location) {markerMe.setPosition(takePosition());}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {setUpMap();} /* MAJ Event on map*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.igab:
                clickIGotABeer();
                break;
            default:
                break;
        }
    }
}
