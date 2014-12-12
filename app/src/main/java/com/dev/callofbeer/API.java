package com.dev.callofbeer;

/**
 * Created by admin on 12/12/2014.
 */
import android.util.Log;

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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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

    public static ArrayList<LatLng> getEvents(ArrayList<LatLng> screen) {
        try {
            String myurl= "http://api.callofbeer.com/events?topLat="+screen.get(0).latitude+"&topLon="+screen.get(0).longitude+"&botLat="+screen.get(1).latitude+"&botLon="+screen.get(1).longitude+"";
            Log.e("URL", myurl);
            ArrayList<LatLng> tabValue = new ArrayList<LatLng>();
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

                    Double ok = geo.getDouble(0);
                    Double ok1 = geo.getDouble(1);

                    tabValue.add(new LatLng(ok1, ok));
                }
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            Log.e("tabValue", tabValue.toString());
            return tabValue;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendEvent(EventBeer event){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://api.callofbeer.com");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("eventName", event.getNomEvent()));
            nameValuePairs.add(new BasicNameValuePair("eventDate", event.getTimer().toString()));
            nameValuePairs.add(new BasicNameValuePair("addressLat", Double.toString(event.getLat())));
            nameValuePairs.add(new BasicNameValuePair("addressLon", Double.toString(event.getLongi())));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.w("Timer", event.getTimer().toString());
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.w("HTTP", response.getStatusLine().toString());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        /*
            try {
                String my_url = "http://api.callofbeer.com";
                URL url = new URL(my_url);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

                //add reuqest header
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; \"\n" +
                        "        + \"Windows NT 5.1; en-US; rv:1.8.0.11) ");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                String urlParameters = "eventName="+event.getNomEvent()+"&eventDate="+event.getTimer()+"&addressLat="+event.getLat()+"&addressLon="+event.getLongi();

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
                Log.w("SEND","\nSending 'POST' request to URL : " + url);
                Log.w("SEND","Post parameters : " + urlParameters);
                Log.w("SEND","Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                Log.w("SEND",response.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
*/

    }

}
