package com.example.ashu.weatherforecast;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

     TextView currentCity,currentTemp, visibility, uvIndex,humidity,windSpeed,windDirection,pressure;
     TextView windHeading,pressureHeading,uvHeading,visibilityHeading,humidityHeading;
     TextView day0Condition,day1Condition,day3Condition,day2Condition,day4Condition;
     TextView day0Temp,day1Temp,day2Temp,day3Temp,day4Temp;
     TextView day0,day1,day2,day3,day4;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    final String logTag="Weather Forecast";
    public static String currentLat="",currentLon="";
    final int REQUEST_CODE=10;
    static boolean internetStatus;
    String locationKey;
    static URL url;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this,SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        currentCity = findViewById(R.id.currentCity);
        currentTemp = findViewById(R.id.currentTemp);
        visibility = findViewById(R.id.visibility);
        uvIndex = findViewById(R.id.uvIndex);
        humidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.windSpeed);
        windDirection = findViewById(R.id.windDirection);
        pressure = findViewById(R.id.pressureValue);

        pressureHeading = findViewById(R.id.pressureHeading);
        windHeading = findViewById(R.id.windHeading);
        visibilityHeading = findViewById(R.id.visibilityHeading);
        humidityHeading = findViewById(R.id.humidityHeading);
        uvHeading = findViewById(R.id.uvIndexHeading);

        day0= findViewById(R.id.day0);
        day0Condition=findViewById(R.id.day0Condition);
        day0Temp=findViewById(R.id.day0Temp);

        day1=findViewById(R.id.day1);
        day1Condition=findViewById(R.id.day1Condition);
        day1Temp=findViewById(R.id.day1Temp);

        day2=findViewById(R.id.day2);
        day2Condition=findViewById(R.id.day2Condition);
        day2Temp=findViewById(R.id.day2Temp);

        day3=findViewById(R.id.day3);
        day3Condition=findViewById(R.id.day3Condition);
        day3Temp=findViewById(R.id.day3Temp);

        day4=findViewById(R.id.day4);
        day4Condition=findViewById(R.id.day4Condition);
        day4Temp=findViewById(R.id.day4Temp);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE:
                getLocation();
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(6000000);
        getLocation();


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(logTag,"Connection Suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(logTag,"Connection Failed");

    }

    @Override
    public void onLocationChanged(Location location) {

        currentLat=""+location.getLatitude();
        currentLon=""+location.getLongitude();
        url=NetworkUtility.buildUrl("locationKey");
        new QueryTask().execute(url);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    public void getLocation(){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.


                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    requestPermissions(new String[]{
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                },REQUEST_CODE);

            }

        }else
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
    }

    public  boolean checkInternet(){
        boolean internetStatus=true;
        ConnectivityManager connec=(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if(connec.getNetworkInfo(0).getState()== android.net.NetworkInfo.State.CONNECTED||connec.getNetworkInfo(1).getState()==android.net.NetworkInfo.State.CONNECTED
                ||connec.getNetworkInfo(0).getState()== NetworkInfo.State.CONNECTING||connec.getNetworkInfo(1).getState()== NetworkInfo.State.CONNECTING)
            internetStatus=true;
        else if(connec.getNetworkInfo(0).getState()== NetworkInfo.State.DISCONNECTED||connec.getNetworkInfo(1).getState()== NetworkInfo.State.DISCONNECTED)
            internetStatus=false;

        return internetStatus;
    }

    private class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String SearchResults = null;
            try {
                internetStatus=checkInternet();
                if(internetStatus==true)
                    SearchResults = NetworkUtility.getResponseFromHttpUrl(searchUrl);
                else {
                    SearchResults=null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                SearchResults=null;
            }

            return SearchResults;
        }

        @Override
        protected void onPostExecute(String weatherSearchResult) {

            if (weatherSearchResult != null && !weatherSearchResult.equals("")&&NetworkUtility.abc=="locationKey") {
                Log.i(logTag,"weatherSearchResults   "+weatherSearchResult);
                WeatherJSON.getLocationKey(weatherSearchResult);
                URL currentCondn=NetworkUtility.buildUrl("currentCondition");
                new QueryTask().execute(NetworkUtility.buildUrl("currentCondition"));
            }
            else if(weatherSearchResult != null && !weatherSearchResult.equals("")&&NetworkUtility.abc=="currentCondition"){

                WeatherJSON.getCurrentCondition(weatherSearchResult);

                currentTemp.setText(WeatherJSON.currentTemp+"°C");
                currentCity.setText(WeatherJSON.localizedName+" | "+WeatherJSON.weatherText);
                windHeading.setText(R.string.wind);
                windSpeed.setText(WeatherJSON.windSpeed);
                windDirection.setText(WeatherJSON.windDirection);
                pressureHeading.setText(R.string.pressure);
                pressure.setText(WeatherJSON.pressureValue);
                visibilityHeading.setText(R.string.visibilty);
                visibility.setText(WeatherJSON.visibilityValue);
                uvHeading.setText(R.string.uvIndex);
                uvIndex.setText(WeatherJSON.uvIndex);
                humidityHeading.setText(R.string.humidity);
                humidity.setText(WeatherJSON.relativeHumidity);

                new QueryTask().execute(NetworkUtility.buildUrl("forecast"));

            }
            else if(weatherSearchResult != null && !weatherSearchResult.equals("")&&NetworkUtility.abc=="forecast"){

                HashMap<String,String> d=WeatherJSON.getForecast(weatherSearchResult);

                day0Condition.setText(WeatherJSON.weatherText);

                Date date = new Date();
                date.setHours(date.getHours() + 24);
                SimpleDateFormat simpDate;
                simpDate = new SimpleDateFormat("kk:mm:ss");
                String time=simpDate.format(date);
                int hh=Integer.parseInt(time.substring(0,2));

                if(hh>=6&&hh<18){
                    //set texts for DayCondition
                    day1Condition.setText(d.get("day1Condition"));
                    day2Condition.setText(d.get("day2Condition"));
                    day3Condition.setText(d.get("day3Condition"));
                    day4Condition.setText(d.get("day4Condition"));
                }else{
                    //set texts for NightCondition

                    day1Condition.setText(d.get("day1NightCondition"));
                    day2Condition.setText(d.get("day2NightCondition"));
                    day3Condition.setText(d.get("day3NightCondition"));
                    day4Condition.setText(d.get("day4NightCondition"));
                }
                day0Temp.setText(d.get("day0MaxTemp")+"/"+d.get("day0MinTemp")+" °C");
                day1Temp.setText(d.get("day1MaxTemp")+"/"+d.get("day1MinTemp")+" °C");
                day2Temp.setText(d.get("day2MaxTemp")+"/"+d.get("day2MinTemp")+" °C");
                day3Temp.setText(d.get("day3MaxTemp")+"/"+d.get("day3MinTemp")+" °C");
                day4Temp.setText(d.get("day4MaxTemp")+"/"+d.get("day4MinTemp")+" °C");

                day0.setText("Today");
                day1.setText("Tommorow");

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                switch (day){

                    case Calendar.MONDAY:
                        day2.setText("Wednesday");day3.setText("Thursday");day4.setText("Friday");
                        break;
                    case Calendar.TUESDAY:
                        day2.setText("Thursday");day3.setText("Friday");day4.setText("Saturday");
                        break;
                    case Calendar.WEDNESDAY:
                        day2.setText("Friday");day3.setText("Saturday");day4.setText("Sunday");
                        break;
                    case Calendar.THURSDAY:
                        day2.setText("Saturday");day3.setText("Sunday");day4.setText("Monday");
                        break;
                    case Calendar.FRIDAY:
                        day2.setText("Sunday");day3.setText("Monday");day4.setText("Tuesday");
                        break;
                    case Calendar.SATURDAY:
                        day2.setText("Mon");day3.setText("Tuesday");day4.setText("Wednesday");
                        break;
                    case Calendar.SUNDAY:
                        day2.setText("Tuesday");day3.setText("Wednesday");day4.setText("Thursday");
                        break;
                }

            }
            else
            {
                Toast t=Toast.makeText(MainActivity.this,"Couldn't Connect to Internet",Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }
}
