package com.example.ashu.weatherforecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ashu on 19/1/18.
 */

public class WeatherJSON {

    public static String main,name,windSpeed,windDeg,temp,temp_min,temp_max,pressure,humidity;

    public static void getJsonData(String JSONFile){


        try {
            if(JSONFile!=null) {

                JSONObject root = new JSONObject(JSONFile);

                name=root.getString("name");

                JSONObject mainObject= root.getJSONObject("main");
                temp=mainObject.getString("temp");
                temp_min=mainObject.getString("temp_min");
                temp_max=mainObject.getString("temp_max");
                pressure=mainObject.getString("pressure");
                humidity=mainObject.getString("humidity");

                JSONObject wind=root.getJSONObject("wind");
                windSpeed=wind.getString("speed");
                windDeg=wind.getString("deg");

                JSONArray weather=root.getJSONArray("weather");
                JSONObject weatherZero=weather.getJSONObject(0);
                main=weatherZero.getString("main");




            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
