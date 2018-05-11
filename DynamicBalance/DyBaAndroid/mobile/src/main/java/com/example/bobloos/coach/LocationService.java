package com.example.bobloos.coach;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.UserModel;
import com.google.android.gms.location.LocationResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by User on 3/6/2018.
 */

public class LocationService extends IntentService{

    DatabaseHandler db;
    UserModel user;
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric";

    public LocationService() {
        super("LocationService");
    }

    @Override
    public void onCreate() {

        super.onCreate();
        db = new DatabaseHandler(getApplicationContext());
        user = db.getUser(1);


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (LocationResult.hasResult(intent)) {
            LocationResult locationResult = LocationResult.extractResult(intent);
            if(locationResult != null){
                Location location = locationResult.getLastLocation();

                double lat = location.getLatitude();
                double lon = location.getLongitude();

                Log.d("LAT", String.valueOf(lat));
                Log.d("LON", String.valueOf(lon));
                fetchWeather(lat, lon);
            }
            else
                Log.e("LOCATION", "NULL");


        }
        else
            Log.e("LOCATION", "NO RESULT");
    }

    public void fetchWeather(double lat, double lon) {

        String curTime = String.valueOf(System.currentTimeMillis());

        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, lat, lon));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("x-api-key", "6c9f1d009beba08ad29ae4df7e4e7515");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();

            String inputString = "";
            while ((inputString = reader.readLine()) != null) {
                builder.append(inputString);
            }
            reader.close();

            JSONObject json_data = new JSONObject(builder.toString());
            JSONObject main = json_data.getJSONObject("main");
            JSONObject clouds = json_data.getJSONObject("clouds");
            String vol_rain = "0";

            if(json_data.has("rain")&&!json_data.isNull("rain")){
                JSONObject rain = json_data.getJSONObject("rain");
                vol_rain = String.valueOf(rain.getInt("3h"));
            }
            String temp = String.valueOf(main.getDouble("temp"));
            String humidity = String.valueOf(main.getInt("humidity"));
            String cloudiness = String.valueOf(clouds.getInt("all"));



            Log.d("WEATHER", temp+" "+humidity+" "+vol_rain+" "+cloudiness);
            db.addLocWeather(String.valueOf(user.getId()), String.valueOf(lat), String.valueOf(lon), temp,
                    humidity, vol_rain, cloudiness, curTime);

            db.addMasterData(String.valueOf(user.getUniqueUserId()), curTime, null, null, null, null,
                    null, null, temp, humidity, cloudiness, null, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
