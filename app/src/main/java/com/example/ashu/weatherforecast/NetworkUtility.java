package com.example.ashu.weatherforecast;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by ashu on 19/1/18.
 */

public class NetworkUtility {
    final static String BASE_URL ="http://dataservice.accuweather.com?apikey=1av2s8PN1C7b8SGs6mYrmorKMMWAq7Yf" ;
    public static String abc="";
    static String logNetworkUtility="WeatherForecast";


    public static URL buildUrl(String str) {

        Uri builtUri;
        if(str=="currentCondition"){
            abc="currentCondition";
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath("currentconditions/v1/"+WeatherJSON.locationKey)
                    .appendQueryParameter("metric","true")
                    .appendQueryParameter("details","true")
                    .build();
        }else if(str=="locationKey"){
            abc="locationKey";
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath("locations/v1/cities/geoposition/search")
                    .appendQueryParameter("metric","true")
                    .appendQueryParameter("q",MainActivity.currentLat+","+MainActivity.currentLon)
                    .build();
        }
        else{
            abc="forecast";
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath("forecasts/v1/daily/5day/"+WeatherJSON.locationKey)
                    .appendQueryParameter("metric","true")
                    .build();
        }


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.i(logNetworkUtility,"Network "+url);
        String JSONFILE = null;
        try {


            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput)
                JSONFILE = scanner.next();
            else
                JSONFILE=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSONFILE;

    }
}
