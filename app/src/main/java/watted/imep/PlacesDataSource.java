package watted.imep;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.HttpsURLConnection;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * SRP
 */

public class PlacesDataSource {
private static Context context=getApplicationContext();
    //observer design pattern: loosely couple the listener.
    public interface OnPlacesArrivedListener {
        void onPlacesArrived(@Nullable ArrayList<Place> movies, @Nullable Exception e);
        //void onError(Exception e);
    }


    //  private static final String address = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=31.2518072,34.8018654&radius=3000&type=health&keyword=%D7%9E%D7%A8%D7%9B%D7%96%20%D7%A8%D7%A4%D7%95%D7%90%D7%99&key=AIzaSyCP966_avo-4yOBG2friW_CH7LkzYtdi7c";
    //private static final String address = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=31.2518072,34.8018654&type=health&keyword=מרכז רפואי&key=AIzaSyCP966_avo-4yOBG2friW_CH7LkzYtdi7c";
    //private static final String address = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=31.2518072,34.8018654&opennow=true&rankby=distance&keyword=%D7%A7%D7%95%D7%A4%D7%AA%20%D7%97%D7%95%D7%9C%D7%99%D7%9D&key=AIzaSyCP966_avo-4yOBG2friW_CH7LkzYtdi7c";
   // private static final String address2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=31.2518072,34.8018654&rankby=distance&keyword=%D7%9E%D7%A8%D7%9B%D7%96%20%D7%A8%D7%A4%D7%95%D7%90%D7%99&key=AIzaSyCP966_avo-4yOBG2friW_CH7LkzYtdi7c";

    public static void getPlaces(final OnPlacesArrivedListener listener, final Location location) {
        //manifest permission (INTERNET)
       final Handler h = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //success (Exception is null): pass the places to the listener:
                    final ArrayList<Place> places = getPlacesSync(location);
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPlacesArrived(places, null);
                        }
                    });

                } catch (Exception e) {
                    //error (places are null): pass the exception to the listener:
                    listener.onPlacesArrived(null, e);
                }
            }
        });

        thread.start();
    }
   private static double deg2rad(double deg) {
      return (deg * Math.PI / 180.0);
  }
    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
    private static ArrayList<Place> getPlacesSync(Location location) throws IOException, JSONException { //Error vs exception //throwable -> Exception, Error
        ArrayList<Place> places = new ArrayList<>();

        try {
           final String address = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.getLatitude() + "," + location.getLongitude() + "&opennow=true&rankby=distance&keyword=%D7%A7%D7%95%D7%A4%D7%AA%20%D7%97%D7%95%D7%9C%D7%99%D7%9D&key=AIzaSyCP966_avo-4yOBG2friW_CH7LkzYtdi7c";
           final String address2 = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.getLatitude() + "," + location.getLongitude() + "&rankby=distance&keyword=%D7%9E%D7%A8%D7%9B%D7%96%20%D7%A8%D7%A4%D7%95%D7%90%D7%99&key=AIzaSyCP966_avo-4yOBG2friW_CH7LkzYtdi7c";
           //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=32.10848370000001,34.80324919999998&opennow=true&rankby=distance&keyword=%D7%A7%D7%95%D7%A4%D7%AA%20%D7%97%D7%95%D7%9C%D7%99%D7%9D&key=AIzaSyCP966_avo-4yOBG2friW_CH7LkzYtdi7c
           URL url = new URL(address);
           URL url2 = new URL(address2);

        //polymorphic method, //con.getResponseCode()...


        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        HttpsURLConnection con2 = (HttpsURLConnection) url2.openConnection();

        InputStream in = con.getInputStream();
        InputStream in2 = con2.getInputStream();
        String json = read(in);
        String json2 = read(in2);

        System.out.println(json);
        JSONObject o = new JSONObject(json);
        JSONObject o2 = new JSONObject(json2);

        JSONArray jsonArray2 = o.getJSONArray("results");
        JSONArray jsonArray22 = o2.getJSONArray("results");
        for (int i = 0; i < jsonArray2.length(); i++) {

            JSONObject placeObject1 = jsonArray2.getJSONObject(i);
            String title = placeObject1.getString("name");
            String vicinity = placeObject1.getString("vicinity");
            String icone = placeObject1.getString("icon");
            JSONObject geometry = placeObject1.getJSONObject("geometry");
            JSONObject Location = geometry.getJSONObject("location");
            String lat = (Location.getString("lat"));
            String lng = (Location.getString("lng"));

            double theta = location.getLongitude() - Double.parseDouble(lng);

            double dist = Math.sin(deg2rad(location.getLatitude())) * Math.sin(deg2rad(Double.parseDouble(lat))) + Math.cos(deg2rad(location.getLatitude())) * Math.cos(deg2rad(Double.parseDouble(lat))) * Math.cos(deg2rad(theta));

            dist = Math.acos(dist);

            dist = rad2deg(dist);


            dist = dist * 60 * 1.1515 * 1.609344;


            places.add(new Place(title, vicinity, icone, lat, lng, dist));


        }

        for (int i = 0; i < jsonArray22.length(); i++) {

            JSONObject placeObject1 = jsonArray22.getJSONObject(i);
            String title = placeObject1.getString("name");
            String vicinity = placeObject1.getString("vicinity");
            String icone = placeObject1.getString("icon");
            JSONObject geometry = placeObject1.getJSONObject("geometry");
            JSONObject Location = geometry.getJSONObject("location");
            String lat = (Location.getString("lat"));
            String lng = (Location.getString("lng"));

            double theta = location.getLongitude() - Double.parseDouble(lng);
            double dist = Math.sin(deg2rad(location.getLatitude())) * Math.sin(deg2rad(Double.parseDouble(lat))) + Math.cos(deg2rad(location.getLatitude())) * Math.cos(deg2rad(Double.parseDouble(lat))) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515 * 1.609344;

            places.add(new Place(title, vicinity, icone, lat, lng, dist));


        }
        }
        catch (Exception e){
    Log.e(MainActivity.class.getSimpleName(),"*** *no internt",e);
        }
        //  Intent intent = new Intent();

        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place c1, Place c2) {
                return Double.compare(c1.getDistance(), c2.getDistance());
            }
        });
           return places;
    }

    //read an input stream to a string
    private static String read(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line = null;
        while ((line = reader.readLine()) != null)
            builder.append(line);

        return builder.toString();
    }
}
