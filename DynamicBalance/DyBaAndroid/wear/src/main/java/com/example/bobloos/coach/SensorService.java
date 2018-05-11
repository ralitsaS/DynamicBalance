package com.example.bobloos.coach;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by bob.loos on 14/05/16.
 */
public class SensorService extends Service implements SensorEventListener {
    private static final String TAG = "SensorService";

    private final static int SENS_ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    private final static int SENS_ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;
    private final static int SENS_HUMIDITY = Sensor.TYPE_RELATIVE_HUMIDITY;
    private final static int SENS_SIGNIFICANT_MOTION = Sensor.TYPE_SIGNIFICANT_MOTION;
    private final static int SENS_STEP_DETECTOR = Sensor.TYPE_STEP_DETECTOR;
    private final static int SENS_HEARTRATE = Sensor.TYPE_HEART_RATE;
    private final static int SENS_STEP_COUNTER = Sensor.TYPE_STEP_COUNTER;

    SensorManager mSensorManager;

    private Sensor mHeartrateSensor;
    ScheduledExecutorService hrScheduler;
    private DeviceClient client;
    Notification.Builder builder;
    private int currentValue=0;
    private long lastTimeSentUnixHR=0L;
    private long lastTimeSentUnixACC=0L;
    private long lastTimeSentUnixSC=0L;
    private float currentStepCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        client = DeviceClient.getInstance(this);

        builder = new Notification.Builder(this);
        builder.setContentTitle("Halo");
        builder.setContentText("Monitoring...");

        startForeground(1, builder.build());

        startMeasurement();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMeasurement();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void startMeasurement() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(SENS_ACCELEROMETER);
        Sensor significantMotionSensor = mSensorManager.getDefaultSensor(SENS_SIGNIFICANT_MOTION);
        Sensor stepcounterSensor = mSensorManager.getDefaultSensor(SENS_STEP_COUNTER);
        mHeartrateSensor = mSensorManager.getDefaultSensor(SENS_HEARTRATE);

        Sensor humiditySensor = mSensorManager.getDefaultSensor(SENS_HUMIDITY);
        Sensor rotationVectorSensor = mSensorManager.getDefaultSensor(SENS_ROTATION_VECTOR);
        Sensor stepDetectorSensor = mSensorManager.getDefaultSensor(SENS_STEP_DETECTOR);

        // register various listeners
        if (mSensorManager != null) {


            Log.w(TAG, "YEAHHH WE SHOULD NOW START MEASURING");
            List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
            Log.d(TAG, sensors.toString());


            if (accelerometerSensor != null) {
                mSensorManager.registerListener(this, accelerometerSensor, 30000000);
            } else {
                Log.w(TAG, "No Accelerometer found");
            }


            if (stepcounterSensor != null) {
                mSensorManager.registerListener(this, stepcounterSensor, 30000000);
            } else {
                Log.d(TAG, "No Step counter Sensor found");
            }

            if (mHeartrateSensor != null) {
                final int measurementDuration   = 43200;   // Seconds (12hrs)
                final int measurementBreak      = 10;    // Seconds

                hrScheduler = Executors.newScheduledThreadPool(1);
                hrScheduler.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                Log.w(TAG, "register Heartrate Sensor");
                                mSensorManager.registerListener(SensorService.this, mHeartrateSensor, SensorManager.SENSOR_DELAY_NORMAL);

                                try {
                                    Thread.sleep(measurementDuration * 1000);
                                } catch (InterruptedException e) {
                                    Log.e(TAG, "Interrupted while waitting to unregister Heartrate Sensor");
                                }

                                Log.w(TAG, "unregister Heartrate Sensor");
                                mSensorManager.unregisterListener(SensorService.this, mHeartrateSensor);
                            }
                        }, 3, measurementDuration + measurementBreak, TimeUnit.SECONDS);
            } else {
                Log.d(TAG, "No Heartrate Sensor found");
            }
            /*

            if (significantMotionSensor != null) {
                mSensorManager.registerListener(this, significantMotionSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Significant Motion Sensor found");
            }
            if (humiditySensor != null) {
                mSensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Humidity Sensor found");
            }

            if (stepDetectorSensor != null) {
                mSensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Step Detector Sensor found");
            }
            if (rotationVectorSensor != null) {
                mSensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.d(TAG, "No Rotation Vector Sensor found");
            }
            */
        }
    }

    private void stopMeasurement() {
        // unregister sensor manager
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
        // shut down heartrate scheduler
        if (hrScheduler != null)
            hrScheduler.shutdown();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // log incoming sensor data
//        Log.d("Sensor", event.sensor.getType() + "," + event.accuracy + "," + event.timestamp + "," + Arrays.toString(event.values));

        long currentTimeUnix = System.currentTimeMillis() / 1000L;
        long nSeconds = 30L;

        // send heartrate data and create new intent
        if (event.sensor.getType() == SENS_HEARTRATE && event.values.length > 0 && event.accuracy > 0) {
            int newValue = Math.round(event.values[0]);

            if(newValue!=0 && lastTimeSentUnixHR < (currentTimeUnix-nSeconds)) {
                lastTimeSentUnixHR = System.currentTimeMillis() / 1000L;
                currentValue = newValue;

                Log.d(TAG, "Broadcast HR.");
                Intent intent = new Intent();
                intent.setAction("com.example.Broadcast");
                intent.putExtra("HR", event.values);
                intent.putExtra("ACCR", event.accuracy);
                intent.putExtra("TIME", event.timestamp);
                sendBroadcast(intent);

                client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);

            }
        }
        // also send motion/humidity/step data to know when NOT to interpret hr data

        if (event.sensor.getType() == SENS_STEP_COUNTER && event.values[0]-currentStepCount!=0) {

            if(lastTimeSentUnixSC < (currentTimeUnix-nSeconds)){
                lastTimeSentUnixSC = System.currentTimeMillis() / 1000L;
                currentStepCount = event.values[0];
                client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
            }

        }

        if (event.sensor.getType() == SENS_ACCELEROMETER) {

            if(lastTimeSentUnixACC < (currentTimeUnix-nSeconds)){
                lastTimeSentUnixACC = System.currentTimeMillis() / 1000L;

                client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
            }
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

