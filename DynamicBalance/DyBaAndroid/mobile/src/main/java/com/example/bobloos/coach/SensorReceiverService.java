package com.example.bobloos.coach;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.MonitorDataModel;
import com.example.bobloos.model.UserModel;
import com.example.bobloos.shared.DataMapKeys;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bob.loos on 15/05/16.
 */

public class SensorReceiverService extends WearableListenerService {
    private static final String TAG = "SensorDashboard/SensorReceiverService";

    private RemoteSensorManager sensorManager;
    long startTime;
    boolean isRunning = false;
    DatabaseHandler db;
    UserModel user;

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHandler(this);
        user = db.getUser(1);
        sensorManager = RemoteSensorManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
        startTime = pref.getLong("START_TIME", 0L);
        registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast1"));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mMessageReceiver);
    }
    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged()");
        for (DataEvent dataEvent : dataEvents) {
            DataItem dataItem = dataEvent.getDataItem();
            Uri uri = dataItem.getUri();
            String path = uri.getPath();
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                isRunning = true;
                if (path.startsWith("/sensors/")) {
                    unpackSensorData(
                            Integer.parseInt(uri.getLastPathSegment()),
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
                else if (path.startsWith("/task/")) {
                    unpackSensorData(
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
            } else {
                isRunning = false;
            }
        }
    }

    private void unpackSensorData(DataMap dataMap) {
        String message = dataMap.getString(DataMapKeys.TASK_MESSAGE);
        Intent intent = new Intent();
        intent.setAction("com.example.Broadcast2");
        intent.putExtra("TASK_MESSAGE", message);
        sendBroadcast(intent);
    }

    private void unpackSensorData(int sensorType, DataMap dataMap) {
        isRunning = true;
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK);
        int hour_of_day = c.get(Calendar.HOUR_OF_DAY);

        int accuracy = dataMap.getInt(DataMapKeys.ACCURACY);
        long timestamp = dataMap.getLong(DataMapKeys.TIMESTAMP);
        float[] values = dataMap.getFloatArray(DataMapKeys.VALUES);

        Log.d(TAG, "Received sensor data " + sensorType + " = " + Arrays.toString(values) + " timestamp:" + timestamp + " start time:" + startTime);
        // Save to database if Start time more than 0
        if (startTime > 0) {

            String avgHR = null;
            String acceleration = null;
            String steps = null;

            switch (sensorType){
                case 21:
                    avgHR = String.valueOf(values[0]);
                    break;
                case 1:
                    acceleration = String.valueOf(Math.sqrt(Math.pow(values[0],2) + Math.pow(values[1],2) + Math.pow(values[2],2)));
                    break;
                case 19:
                    HashMap<String, String> last_step_data = db.getLastStepCount();
                    float steps_per_min = 0;
                    if(last_step_data!=null){
                        long last_ts = Long.parseLong(last_step_data.get("timestamp"));
                        int last_steps = Integer.parseInt(last_step_data.get("steps"));
                        if(values[0]>=last_steps){
                            steps_per_min = (values[0]-last_steps)/((timestamp-last_ts)/60000);
                        }else steps_per_min = (values[0])/((timestamp-last_ts)/60000);
                    }

                    steps = String.valueOf(steps_per_min);
                    break;
            }

            db.addMasterData(String.valueOf(user.getUniqueUserId()), String.valueOf(timestamp), avgHR, acceleration, steps,
                    String.valueOf(hour_of_day), String.valueOf(day_of_week), null, null, null, null,
                    null, null, null);

            db.addMonitorData(new MonitorDataModel(0, String.valueOf(sensorType), String.valueOf(user.getUniqueUserId()), Arrays.toString(values),
                    String.valueOf(startTime), String.valueOf(accuracy), String.valueOf(timestamp), String.valueOf(hour_of_day), String.valueOf(day_of_week)));
            List<MonitorDataModel> list = db.getAllUserMonitorData();

        } else {
            isRunning = false;
        }
        if(sensorType == 21){
            HeartRateDataModel hrModel = new HeartRateDataModel( );
            hrModel.setUserId(String.valueOf(user.getId()));
            hrModel.setAccuracy(String.valueOf(accuracy));
            hrModel.setUniqueUserId(user.getUniqueUserId());
            hrModel.setHeartRate(String.valueOf(values[0]));
            hrModel.setMeasurementTime(timestamp);
            db.addHeartRateData(hrModel);
        }

        sensorManager.addSensorData(sensorType, accuracy, timestamp, values);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long message1 = intent.getLongExtra("START_TIME",0L);
            startTime = message1;// set start time
            SharedPreferences pref = getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
            startTime = pref.getLong("START_TIME", 0L);
            Log.d("Receiver", "Got START_TIMEB: " + String.valueOf(startTime));
        }
    };
}

