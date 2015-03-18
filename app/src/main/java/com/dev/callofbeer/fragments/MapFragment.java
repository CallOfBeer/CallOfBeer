package com.dev.callofbeer.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dev.callofbeer.R;
import com.dev.callofbeer.activities.CallOfBeerActivity;
import com.dev.callofbeer.models.EventBeer;
import com.dev.callofbeer.network.API;
import com.dev.callofbeer.utils.LocalisationUtils;
import com.dev.callofbeer.utils.NetworkUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by matth on 04/03/15.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private final static double latitude = 44.84403344;
    private final static double longitude = -0.58759689;

    private GoogleMap mMap;
    private Marker mMarker;
    private LatLng position;
    private View view;
    private Hashtable<Integer, EventBeer> allEventBeers;

    private boolean isNetworkListener = false;
    private boolean isGPSListener = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        allEventBeers = new Hashtable<Integer, EventBeer>();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {}

        setUpMapIfNeeded();

        return view;
    }


    /**
     *
     * Create the map and set marker position on the map
     *
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) CallOfBeerActivity.fragmentManager
                    .findFragmentById(R.id.fragment_map)).getMap();
            setUpMapIfNeeded();
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            if(NetworkUtils.HttpTest(getActivity()) && LocalisationUtils.isLocalisationAvailable(getActivity())) {
                if(mMarker == null) {
                    mMap.setOnCameraChangeListener(new CameraChangeListener());
                    position = getPosition(latitude, longitude);

                    mMarker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                    mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
                    updateMap();
                }
            }
        }
    }


    /**
     *
     * Refresh the marker map when the map move
     *
     */
    private void updateMap() {
       new UpdateEventMarker().execute(getMapVisibleRegionSize());
    }


    /**
     * Get the size of the visible region on the screen
     *
     * @return ArrayList<LatLng> size of the visible region top left and bottom right
     */
    private ArrayList<LatLng> getMapVisibleRegionSize() {
        LatLng topLeft = mMap.getProjection().getVisibleRegion().farLeft;
        LatLng bottomRight = mMap.getProjection().getVisibleRegion().nearRight;

        ArrayList<LatLng> screenSizeArrayList = new ArrayList<LatLng>();
        screenSizeArrayList.add(topLeft);
        screenSizeArrayList.add(bottomRight);

        return screenSizeArrayList;
    }


    /**
     *
     * Get the position of the device
     *
     * @param latitude
     * @param longitude
     * @return return the current position of the device
     */
    public LatLng getPosition(double latitude, double longitude) {
        LatLng position = new LatLng(latitude, longitude);
        Location location = null;

        try {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isNetworkEnabled) {
                if(!isNetworkListener) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener());
                    isNetworkListener = true;
                }
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (isGPSEnabled) {
                if(!isGPSListener) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener());
                    isGPSListener = true;
                }
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                isGPSListener = false;
                isNetworkListener = false;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        if(location != null) {
            position = new LatLng(location.getLatitude(), location.getLongitude());
        }

        return position;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(Map.Entry<Integer, EventBeer> entry : allEventBeers.entrySet()) {
            EventBeer eventBeer = entry.getValue();
            LatLng eventPosition = new LatLng(eventBeer.getLatitude(), eventBeer.getLongitude());

            if(marker.getPosition().equals(eventPosition)){
                Toast.makeText(getActivity(),Integer.toString(eventBeer.getId()), Toast.LENGTH_SHORT).show();
                ((CallOfBeerActivity) getActivity()).forcedSlidingUp();
            }
        }
        return true;
    }


    /**
     *
     * Listener when the map is zoomed or moved
     *
     */
    private class CameraChangeListener implements GoogleMap.OnCameraChangeListener {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            updateMap();
        }
    }


    /**
     *
     * Listener when the device is in movement
     *
     */
    private class LocationListener implements android.location.LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            mMarker.setPosition(getPosition(position.latitude, position.longitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    }


    /**
     *
     * Class of asynchronous task to request API and get new events
     *
     */
    private class UpdateEventMarker extends AsyncTask<ArrayList<LatLng>, Void, Hashtable<Integer, EventBeer>> {
        @Override
        protected Hashtable<Integer, EventBeer> doInBackground(ArrayList<LatLng>... params) {
            ArrayList<EventBeer> eventBeerArrayList = API.getEvents(params[0]);
            for (EventBeer eventBeer : eventBeerArrayList) {
                allEventBeers.put(eventBeer.getId(), eventBeer);
            }
            return allEventBeers;
        }

        @Override
        protected void onPostExecute(Hashtable<Integer, EventBeer> eventBeers) {
            if(eventBeers != null) {
                for(Map.Entry<Integer, EventBeer> entry : allEventBeers.entrySet()) {
                    int key = entry.getKey();
                    EventBeer eventBeer = entry.getValue();

                    LatLng eventPosition = new LatLng(eventBeer.getLatitude(), eventBeer.getLongitude());

                    mMap.addMarker(new MarkerOptions()
                            .position(eventPosition)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    // do what you have to do here
                    // In your case, an other loop.
                }
            }
        }
    }
}
