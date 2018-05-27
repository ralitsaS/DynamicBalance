package com.example.bobloos.coach;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.UserModel;
import com.example.bobloos.shared.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 5/25/2018.
 */

public class BaseLineService extends Service{

    DatabaseHandler db;
    UserModel user;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor sharedPrefsEditor;
    BroadcastReceiver hrReceiver;
    private static RemoteSensorManager remoteSensorManager;
    long lastMeasurementTime = 0L;


    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHandler(getApplicationContext());
        user = db.getUser(1);
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
        sharedPrefsEditor = sharedPrefs.edit();
        remoteSensorManager = RemoteSensorManager.getInstance(this);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        hrReceiver = this.hrMessageReceiver;
        BaseLineService.this.registerReceiver(hrReceiver, new IntentFilter("com.example.Broadcast"));

        long timeInMs = System.currentTimeMillis();
        sharedPrefsEditor.putLong("baseLineTimeStamp", timeInMs);
        sharedPrefsEditor.putBoolean("baseLinePerforming", true);
        sharedPrefsEditor.commit();

        lastMeasurementTime = System.currentTimeMillis();
        remoteSensorManager.startMeasurement();
        Intent intent1 = new Intent();
        intent1.setAction("com.example.Broadcast1");
        intent1.putExtra("START_TIME", lastMeasurementTime); // get current millisec time
        BaseLineService.this.sendBroadcast(intent1);
        SharedPreferences pref = BaseLineService.this.getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("START_TIME", lastMeasurementTime);
        editor.apply();

        return START_NOT_STICKY;
    }


    private BroadcastReceiver hrMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // get extras
                float[] heartRate = intent.getFloatArrayExtra("HR");
                int accuracy = intent.getIntExtra("ACCR", 0);
                int sensorType = intent.getIntExtra("SENSOR_TYPE", 0);
                int userId = user.getId();
                long timeInMs = System.currentTimeMillis();

                HeartRateDataModel hrModel = new HeartRateDataModel( );
                hrModel.setUserId(String.valueOf(userId));
                hrModel.setAccuracy(String.valueOf(accuracy));
                hrModel.setUniqueUserId(user.getUniqueUserId());
                hrModel.setHeartRate(String.valueOf(heartRate[0]));
                hrModel.setMeasurementTime(timeInMs);
                Log.d("ReceiverBaseline", "Got HR: " + heartRate[0] + ". Got Accuracy: " + accuracy);
                db.addHeartRateData(hrModel);
                baseLineEnoughMeasuresFromMoment();
            } catch (Exception e) {
            }
        }
    };

    private void baseLineEnoughMeasuresFromMoment(){
        Long timestamp = sharedPrefs.getLong("baseLineTimeStamp", 0);
        Log.d("TIMESTAMP", timestamp.toString());
        List<HeartRateDataModel> hrs_by_timestamp = db.getAllHeartRateAfterTimeStamp(timestamp);
        int list_count = hrs_by_timestamp.size();
        Log.d("BASELINE TILL NOW", String.valueOf(list_count));
        if(list_count>300){
            stopAndSaveBaseLineReading();
        } else if (list_count == 50 || list_count == 100 || list_count == 150 || list_count == 200 || list_count == 300){
            setBaseLine();
        }
    }

    private void stopAndSaveBaseLineReading(){
        remoteSensorManager.stopMeasurement();
        setBaseLine();

        if (hrReceiver != null) {
            this.unregisterReceiver(hrReceiver);
            hrReceiver = null;
        }


        sharedPrefsEditor.putBoolean("backtoMain", true);
        sharedPrefsEditor.putBoolean("baseLinePerforming", false);
        sharedPrefsEditor.commit();

    }

    private void setBaseLine(){
        Long timestamp = sharedPrefs.getLong("baseLineTimeStamp", 0);
        List<HeartRateDataModel> hrs_by_timestamp = db.getAllHeartRateAfterTimeStamp(timestamp);
        float mean = calculateBaselineMean(hrs_by_timestamp);
        float variance = calculateBaselineVariance(hrs_by_timestamp, mean);
        double stdf = Math.sqrt(variance);

        user.setAvgHeartRate(String.valueOf(mean));
        user.setStdfHeartRate(String.valueOf(stdf));
        db.updateUser(user);

        Log.d("MEAN", String.valueOf(mean));
        Log.d("VARIANCE", String.valueOf(variance));
        Log.d("STDF", String.valueOf(stdf));
        Log.d("AVG HEARTRATE", String.valueOf(mean));
        Log.d("AVG_STFD", String.valueOf(stdf));
    }

    private float calculateBaselineMean(List<HeartRateDataModel> hrs){
        int count = hrs.size();
        float sum_hrs = 0;
        for (int i = 0; i < count; i++) {
            float hr = Float.parseFloat(hrs.get(i).getHeartRate());
            sum_hrs += hr;
        }
        return sum_hrs/count;
    }

    private float calculateBaselineVariance(List<HeartRateDataModel> hrs, float mean){
        int count = hrs.size();
        float sum_square_variance = 0;
        for (int i = 0; i < count; i++) {
            float variance = (Float.parseFloat(hrs.get(i).getHeartRate()) - mean) ;
            double square_variance = Math.pow(variance, 2);
            sum_square_variance += square_variance;
        }

        return sum_square_variance/count;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}
