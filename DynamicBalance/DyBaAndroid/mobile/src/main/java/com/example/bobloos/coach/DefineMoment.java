package com.example.bobloos.coach;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.UserModel;
import com.example.bobloos.shared.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by User on 4/30/2018.
 */

public class DefineMoment extends Service{

    DatabaseHandler db;
    UserModel user;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor sharedPrefsEditor;
    PhysStateModel physState;
    ArrayList<HashMap<String, String>> db_master;
    Gson gson = new GsonBuilder().create();

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DatabaseHandler(getApplicationContext());
        user = db.getUser(1);
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
        sharedPrefsEditor = sharedPrefs.edit();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        List<HeartRateDataModel> latest_ten_measures = db.getLatestMeasures();
        int count = latest_ten_measures.size();
        float totalHR = 0;
        for (int i = 0; i < count; i++) {
            float hr = Float.valueOf(latest_ten_measures.get(i).getHeartRate());
            Log.d("DEFINE MOMENT, HR TIME", latest_ten_measures.get(i).getMeasurementTime().toString());
            totalHR += hr;
        }

        float avgHr = totalHR/count;
        float userAverageHr = Float.valueOf(user.getAvgHeartRate());
        float userStdfHr = Float.valueOf(user.getStdfHeartRate());
        float hrDiff = avgHr - userAverageHr;

        Log.d("AVGHR", String.valueOf(avgHr));
        Log.d("useraveragdeHR", String.valueOf(userAverageHr));
        Log.d("userSTDFhr", String.valueOf(userStdfHr));
        Log.d("hrDiff", String.valueOf(hrDiff));

        int moment_mode = 999;
        Log.d("Use Sensitivity", getSensitivity().toString());

        double interval = (userStdfHr * getSensitivity());
        Log.d("INTERVAL", String.valueOf(interval));

        Integer previousMomentState = sharedPrefs.getInt("momentState", 1);

        if(hrDiff <= interval*-5){
            sharedPrefsEditor.putInt("momentState", -5);
            Log.d("Moment", "in state -5");
            moment_mode = -5;
        } else if (hrDiff > interval*-5 && hrDiff <= (interval*-4) ){
            sharedPrefsEditor.putInt("momentState", -4);
            moment_mode = -4;
            Log.d("Moment", "in state -4");
        } else if (hrDiff > interval*-4 && hrDiff <= (interval*-3) ){
            sharedPrefsEditor.putInt("momentState", -3);
            moment_mode = -3;
            Log.d("Moment", "in state -3");
        } else if (hrDiff > interval*-3 && hrDiff <= (interval*-2) ){
            sharedPrefsEditor.putInt("momentState", -2);
            moment_mode = -2;
            Log.d("Moment", "in state -2");
        } else if (hrDiff > interval*-2 && hrDiff <= (interval*-1) ){
            sharedPrefsEditor.putInt("momentState", -1);
            moment_mode = -1;
            Log.d("Moment", "in state -1");
        } else if (hrDiff > interval*-1 && hrDiff <= (interval) ){
            sharedPrefsEditor.putInt("momentState", 1);
            moment_mode = 1;
            Log.d("Moment", "in state 1");
        } else if (hrDiff > interval && hrDiff <= (interval*2) ){
            sharedPrefsEditor.putInt("momentState", 2);
            moment_mode = 2;
            Log.d("Moment", "in state 2");
        } else if (hrDiff >  (interval*2) && hrDiff <= (interval*3) ){
            sharedPrefsEditor.putInt("momentState", 3);
            moment_mode = 3;
            Log.d("Moment", "in state 3");
        } else if (hrDiff > (interval*3) && hrDiff <= (interval*4 )) {
            sharedPrefsEditor.putInt("momentState", 4);
            moment_mode = 4;
            Log.d("Moment", "in state 4");
        } else if (hrDiff > (interval*4) ) {
            sharedPrefsEditor.putInt("momentState", 5);
            moment_mode = 5 ;
            Log.d("Moment", "in state 5");
        }

        sharedPrefsEditor.commit();
        long timeInMs = System.currentTimeMillis();

        if (previousMomentState == moment_mode) {
            Log.d("MEASUREER", "NO NEW STATE");
            Log.d("MEASUREER", previousMomentState.toString());
            Log.d("MEASUREER", String.valueOf(moment_mode));
        }else{
            Log.d("MEASUREER", "NEW STATE!!!!");
            Log.d("MEASUREER", previousMomentState.toString());
            Log.d("MEASUREER", String.valueOf(moment_mode));


            physState = new PhysStateModel();
            physState.setLevel(String.valueOf(moment_mode));
            physState.setUserId(String.valueOf(user.getId()));
            physState.setStateTimeStamp(String.valueOf(timeInMs));

            db.addPhysState(physState);




        }

        db.addMasterData(String.valueOf(user.getUniqueUserId()), String.valueOf(timeInMs), null, null,
                null, null, null, null, null, null, null,
                String.valueOf(moment_mode), String.valueOf(userAverageHr), String.valueOf(interval));




        sendToServer();

        //boolean coachCheck= sharedPrefs.getBoolean("coachSwitchStateChecked", false);
        //Toast.makeText(getApplicationContext(), String.valueOf(coachCheck), Toast.LENGTH_LONG).show();

        return START_NOT_STICKY;
    }

    public void sendToServer(){

        db_master = db.getMasterData();
        String jsondata = gson.toJson(db_master);

        try {
            StringEntity entity = new StringEntity(jsondata);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30000);
            client.setMaxRetriesAndTimeout(0,30000);
            client.post(DefineMoment.this,"http://applab.ai.ru.nl:8081/predict", entity , "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    String m = new String(response);
                    String[] separated;
                    //Log.d("php response",m);
                    //prgDialog.hide();
                    //Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();
                    if(!m.equals("nope"))
                    {
                        separated = m.split(":");
                        if(separated[1].equals("1")){
                            Intent intentsend = new Intent();
                            intentsend.setAction("com.example.sendMessageAlarm");
                            intentsend.putExtra("moment", "1");

                            DefineMoment.this.sendBroadcast(intentsend);
                        }


                        db.addFeedbackData(String.valueOf(user.getUniqueUserId()), separated[0], separated[1], separated[1], null);
                    }

                    db.deleteAllMaster();
                    db.replaceSwitchAndTS("on", String.valueOf(System.currentTimeMillis()));

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    // prgDialog.hide();
                    if(statusCode == 404){
                        //Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    }else if(statusCode == 500){
                        //Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }else{
                        //Toast.makeText(getApplicationContext(), "error code: "+statusCode, Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }

            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Double getSensitivity(){
        String sensitivity = user.getSensitivityPref();
        switch (sensitivity){
            case "1":
                return 1.5;
            case "2":
                return 1.0;
            case "3":
                return 0.5;
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}
