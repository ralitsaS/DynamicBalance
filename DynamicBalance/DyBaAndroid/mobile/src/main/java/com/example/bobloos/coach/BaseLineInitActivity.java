package com.example.bobloos.coach;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.UserModel;
import com.example.bobloos.shared.Constants;

import java.util.List;


public class BaseLineInitActivity extends Activity {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor sharedPrefsEditor;
    DatabaseHandler db;
    UserModel user;

    TextView hallo;
    TextView instructions;
    TextView note;

    ProgressBar progressBar;
    Button baseLineButton;
    BroadcastReceiver hrReceiver;

    private static RemoteSensorManager remoteSensorManager;
    long lastMeasurementTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_base_line_init);
        db = new DatabaseHandler(this);
        remoteSensorManager = RemoteSensorManager.getInstance(this);

        hallo = (TextView) findViewById(R.id.halloTextView);
        instructions = (TextView) findViewById(R.id.instructionTextView);
        note = (TextView) findViewById(R.id.noteTextView);

        baseLineButton = (Button) findViewById(R.id.startBaseLineMeasureButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        setUser();
        setBaseLineButtonListener();
        setPrefHandler();


        if(sharedPrefs.getBoolean("baseLinePerforming", false)){
            hallo.setText("Bezig...");
            instructions.setText("Op dit moment berekenen we jouw basismeting van je hartslag. Zorg dat het horloge goed vast zit. Dit process kan maximaal 5 uur duren. Vroegtijdig de basismeting stoppen kan, ten nadelen van de betrouwbaarheid van het systeem");
            baseLineButton.setText("STOP BASISMETING");
            progressBar.setVisibility(View.VISIBLE);
            setProgressBar();
        } else if(sharedPrefs.getBoolean("backtoMain", false)){
            hallo.setText("Klaar!");
            instructions.setText("Jouw basismeting is voltooid en je kunt nu gebruik maken van de applicatie");
            baseLineButton.setText("BEGINNEN");
            progressBar.setVisibility(View.INVISIBLE);
        } else{
            hallo.setText("Mmmmmm...");
            instructions.setText("Je hebt al eerder een basismeting uitgevoerd. Wanneer je hieronder op de knop drukt, zal deze basismeting overschreven worden. Het uitvoeren van een basismeting duurt maximaal 5 uur!");
            progressBar.setVisibility(View.INVISIBLE);
        }


    }

    private void setPrefHandler(){
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
        sharedPrefsEditor = sharedPrefs.edit();
    }

    private void setUser(){
        user = db.getUser(1);
        if(user == null){
            db.addUser(new UserModel());
        }
    }



    private void setBaseLineButtonListener(){
        baseLineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Log.d("BASELINE INIT", "BASELINE CLICKED");
                if(sharedPrefs.getBoolean("baseLinePerforming", false)) {
                    stopAndSaveBaseLineReading();
                } else {
                    if(sharedPrefs.getBoolean("backtoMain", false)){
                        sharedPrefsEditor.putBoolean("backtoMain", false);
                        sharedPrefsEditor.commit();
                        Intent intent = new Intent(BaseLineInitActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        startBaselineReading();
                    }
                }
            }
        });
    }

    private void startBaselineReading() {

        Intent serviceIntent = new Intent(BaseLineInitActivity.this, BaseLineService.class);
        startService(serviceIntent);
        /// UI STUFF

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        hallo.setText("Bezig...");
        instructions.setText("Op dit moment berekenen we jouw basismeting van je hartslag. Zorg dat het horloge goed vast zit. Dit process kan maximaal 5 uur duren. Vroegtijdig de basismeting stoppen kan, ten nadelen van de betrouwbaarheid van het systeem");
        baseLineButton.setText("STOP BASISMETING");
        Log.d("BASELINE INIT", "SHOULD HAVE BROADCASED INTENT");
    }

    private void stopAndSaveBaseLineReading(){
        remoteSensorManager.stopMeasurement();
        setBaseLine();

        if (hrReceiver != null) {
            this.unregisterReceiver(hrReceiver);
            hrReceiver = null;
        }

        progressBar.setVisibility(View.INVISIBLE);



        sharedPrefsEditor.putBoolean("backtoMain", true);
        sharedPrefsEditor.putBoolean("baseLinePerforming", false);
        sharedPrefsEditor.commit();




    }


    private void setProgressBar(){

        Long timestamp = sharedPrefs.getLong("baseLineTimeStamp", 0);
        List<HeartRateDataModel> hrs_by_timestamp = db.getAllHeartRateAfterTimeStamp(timestamp);
        int list_count = hrs_by_timestamp.size();

        double result = (( (double)list_count / 300.0) * 100.0);

        progressBar.setProgress( (int)result);
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




}
