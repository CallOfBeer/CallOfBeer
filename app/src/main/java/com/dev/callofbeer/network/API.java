package com.dev.callofbeer.network;

/**
 * Created by admin on 12/12/2014.
 */
import android.util.Log;

import com.dev.callofbeer.models.EventBeer;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yun on 19/11/14.
 */

public class API {

    /**
     * @param in : buffer with the php result
     * @param bufSize : size of the buffer
     * @return : the string corresponding to the buffer
     */
    public static String InputStreamToString (InputStream in, int bufSize) {
        final StringBuilder out = new StringBuilder();
        final byte[] buffer = new byte[bufSize];
        try {
            for (int ctr; (ctr = in.read(buffer)) != -1;) {
                out.append(new String(buffer, 0, ctr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot convert stream to string", e);
        }
        // On retourne la chaine contenant les donnees de l'InputStream
        return out.toString();
    }

    /**
     * @param in : buffer with the php result
     * @return : the string corresponding to the buffer
     */
    public static String InputStreamToString (InputStream in) {
        // On appelle la methode precedente avec une taille de buffer par defaut
        return InputStreamToString(in, 1024);
    }

    public static ArrayList<EventBeer> getEvents(ArrayList<LatLng> screen) {
        try {
            String myurl= "http://api.callofbeer.com/events?topLat="+screen.get(0).latitude+"&topLon="+screen.get(0).longitude+"&botLat="+screen.get(1).latitude+"&botLon="+screen.get(1).longitude+"";
            Log.e("URL", myurl);
            ArrayList<EventBeer> tabValue = new ArrayList<EventBeer>();
            URL url = new URL(myurl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            String result = API.InputStreamToString(inputStream);


            try {
                JSONArray array = new JSONArray(result);

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    JSONObject address = new JSONObject(obj.get("address").toString());
                    JSONArray geo = new JSONArray(address.get("geolocation").toString());

                    Double lat = geo.getDouble(1);
                    Double lon = geo.getDouble(0);

                    Long time = Long.getLong(obj.getString("date").toString());
                    // Not a good time

                    tabValue.add(new EventBeer(obj.getString("name").toString(),time,lat,lon));
                }
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return tabValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendEvent(EventBeer event){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://api.callofbeer.com/events");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("eventName", event.getNomEvent()));
            Long time = event.getTimer();
            nameValuePairs.add(new BasicNameValuePair("eventDate", time.toString()));
            nameValuePairs.add(new BasicNameValuePair("addressLat", Double.toString(event.getLatitude())));
            nameValuePairs.add(new BasicNameValuePair("addressLon", Double.toString(event.getLongitude())));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.w("HTTP", response.getStatusLine().toString());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }

    }

}
