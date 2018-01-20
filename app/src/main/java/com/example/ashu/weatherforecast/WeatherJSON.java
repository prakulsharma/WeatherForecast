package com.example.ashu.weatherforecast;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

/**
 * Created by ashu on 19/1/18.
 */

public class WeatherJSON {


    public  static HashMap<String,String> data=new HashMap<>();

    public static String locationKey,localizedName,weatherText,currentTemp,relativeHumidity,uvIndex;
    public static String windDirection,windSpeed,visibilityValue,pressureValue;

    public static void getCurrentCondition(String JSONFile){
        try {
            if(JSONFile!=null) {

                JSONArray root = new JSONArray(JSONFile);
                JSONObject zero=root.getJSONObject(0);

                weatherText=zero.getString("WeatherText");

                JSONObject temperature=zero.getJSONObject("Temperature");
                JSONObject metricTemperature=temperature.getJSONObject("Metric");
                currentTemp=""+metricTemperature.getDouble("Value");

                relativeHumidity=""+zero.getInt("RelativeHumidity")+" %";

                uvIndex=""+zero.getInt("UVIndex");

                JSONObject wind=zero.getJSONObject("Wind");
                JSONObject direction=wind.getJSONObject("Direction");
                windDirection=direction.getString("English");
                if(windDirection.length()==3){
                    windDirection=windDirection.substring(0,1)+"-"+windDirection.substring(1);
                }
                else if(windDirection.length()==4){
                    windDirection=windDirection.substring(0,2)+"-"+windDirection.substring(2);
                }
                JSONObject speed=wind.getJSONObject("Speed");
                JSONObject metricSpeed=speed.getJSONObject("Metric");
                windSpeed=""+metricSpeed.getDouble("Value")+" km/hr";

                JSONObject visibility=zero.getJSONObject("Visibility");
                JSONObject metricVisibility=visibility.getJSONObject("Metric");
                visibilityValue=""+metricVisibility.getDouble("Value")+" km";

                JSONObject pressure=zero.getJSONObject("Pressure");
                JSONObject metricPressure=pressure.getJSONObject("Metric");
                pressureValue=""+metricPressure.getInt("Value")+" mb";

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

        public  static void getLocationKey(String JsonFile){

            try {
                if(JsonFile!=null) {

                    JSONObject root = new JSONObject(JsonFile);
                    locationKey=root.getString("Key");
                    localizedName=root.getString("LocalizedName");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    public  static HashMap<String,String> getForecast(String JsonFile){


        try {
            if(JsonFile!=null) {

                JSONObject root = new JSONObject(JsonFile);
                JSONArray dailyForecasts= root.getJSONArray("DailyForecasts");
                for(int i=0;i<5;i++){
                    JSONObject dayI=dailyForecasts.getJSONObject(i);
                    JSONObject day=dayI.getJSONObject("Day");
                    String dayICondiition=day.getString("IconPhrase");
                    JSONObject night=dayI.getJSONObject("Night");
                    String dayINightCondition=night.getString("IconPhrase");
                    JSONObject temp=dayI.getJSONObject("Temperature");
                    JSONObject min=temp.getJSONObject("Minimum");
                    String dayIMinTemp=min.getString("Value");
                    JSONObject max=temp.getJSONObject("Maximum");
                    String dayIMaxTemp=max.getString("Value");

                    data.put("day"+i+"MinTemp",""+dayIMinTemp);
                    data.put("day"+i+"MaxTemp",""+dayIMaxTemp);
                    data.put("day"+i+"Condition",dayICondiition);
                    data.put("day"+i+"NightCondition",dayINightCondition);


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;

    }
}
