package com.example.ashu.weatherforecast;

import android.*;
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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

     TextView currentCity,currentTemp,tempMax,tempMin,humidity,windSpeed,windDirection,pressure;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    final String logTag="Weather Forecast";
    public static String currentLat="",currentLon="";
    final int REQUEST_CODE=10;
    boolean internetStatus;
    static URL url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentCity = findViewById(R.id.currentCity);
        currentTemp = findViewById(R.id.currentTemp);
        tempMax = findViewById(R.id.maxTemp);
        tempMin = findViewById(R.id.minTemp);
        humidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.windSpeed);
        windDirection = findViewById(R.id.windDirection);
        pressure = findViewById(R.id.pressureValue);


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

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000);
        getLocation();


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(logTag,"Connction Suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(logTag,"Connction Failed");

    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(logTag,""+location);


        currentLat=""+location.getLatitude();
        currentLon=""+location.getLongitude();

        url=NetworkUtility.buildUrl();
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
            if (weatherSearchResult != null && !weatherSearchResult.equals("")) {

                WeatherJSON.getJsonData(weatherSearchResult);

                currentTemp.setText(""+WeatherJSON.temp);
                currentCity.setText(""+WeatherJSON.name+" | "+WeatherJSON.main);
                tempMax.setText(""+WeatherJSON.temp_max);
                tempMin.setText(""+WeatherJSON.temp_min);
                windSpeed.setText(""+WeatherJSON.windSpeed+" m/s");
                pressure.setText(""+WeatherJSON.pressure+" hPa");
                String dir="";

                double deg= Double.parseDouble(WeatherJSON.windDeg);
                     if(deg>=0&&deg<22.5){dir="N";}
                else if(deg>=22.5&&deg<67.5){dir="NE";}
                else if(deg>=67.5&&deg<112.5){dir="E";}
                else if(deg>=112.5&&deg<157.5){dir="SE";}
                else if(deg>=157.5&&deg<202.5){dir="S";}
                else if(deg>=202.5&&deg<247.5){dir="SW";}
                else if(deg>=247.5&&deg<292.5){dir="W";}
                else if(deg>=292.5&&deg<337.5){dir="NW";}
                else if(deg>=337.5&&deg<=360){dir="N";}

                windDirection.setText(dir);

            }
            else
            {
                Toast t=Toast.makeText(MainActivity.this,"Couldn't Connect to Internet",Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }
}
