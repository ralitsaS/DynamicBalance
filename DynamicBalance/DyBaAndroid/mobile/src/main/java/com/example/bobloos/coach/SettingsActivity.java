package com.example.bobloos.coach;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.MonitorDataModel;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.SelfReportModel;
import com.example.bobloos.model.UserModel;
import com.example.bobloos.shared.Constants;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;
    UserModel user;
    DatabaseHandler db;
    Button baseLineButton;
    Button exportButton;
    Button saveSettingsButton;
    public ProgressDialog prgDialog;

    ArrayList<HashMap<String,String>> all_monitordata;
    ArrayList<HashMap<String,String>> all_physstate;
    ArrayList<HashMap<String,String>> all_userdata;
    ArrayList<HashMap<String,String>> all_hrdata;
    ArrayList<HashMap<String,String>> all_selfreport;
    ArrayList<HashMap<String,String>> all_activity;
    ArrayList<HashMap<String,String>> all_locweather;
    ArrayList<HashMap<String,String>> feedback_data;
    List<HashMap<String,String>> sub_db_data;
    String table1;
    String table2;
    String table3;
    String table4;
    String table5;
    String table6;
    String table7;
    Gson gson = new GsonBuilder().create();
    EditText patientAVGHR;
    EditText patientSTDF;
    TextView patientUniqueID;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor sharedPrefsEditor;
    String sensitivity;

    DefaultApplication helper = DefaultApplication.getInstance();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        db = new DatabaseHandler(this);
        setContentView(R.layout.activity_settings);
        setUser();
        setPrefHandler();


        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Synching SQLite Data with Remote MySQL DB. Please wait...");
        prgDialog.setCancelable(false);

        baseLineButton = (Button) findViewById(R.id.baseLineButton);
        setBaseLineButtonListener();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupDrawer();
        getSupportActionBar().setTitle("Instellingen");

        exportButton = (Button) findViewById(R.id.exportButton);
//        exportButton.setVisibility(View.INVISIBLE);
        exportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // exportDataToServer();

                trainModel();

                Log.d("EXPORT", "called");
            }
        });

        saveSettingsButton = (Button) findViewById(R.id.saveSettingsButton);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Settings", "save");
                saveSettings();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setBaseLineButtonListener() {
        baseLineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, BaseLineInitActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                SettingsActivity.this.finish();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonLight:
                if (checked)
                    sensitivity = "1";
                    break;
            case R.id.radioButtonNormal:
                if (checked)
                    // Ninjas rule
                    sensitivity = "2";
                    break;
            case R.id.radioButtonSensitive:
                if (checked)
                    sensitivity = "3";
                    break;
        }
    }

    private void saveSettings(){
        user.setAvgHeartRate(patientAVGHR.getText().toString());
        user.setStdfHeartRate(patientSTDF.getText().toString());
        user.setSensitivityPref(sensitivity);
        db.updateUser(user);

        Context context = getApplicationContext();
        CharSequence text = "New settings are saved!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void setUser() {
        user = db.getUser(1);

        patientAVGHR = (EditText) findViewById(R.id.editTextHRResult);
        patientSTDF = (EditText) findViewById(R.id.editTextStdfResult);
        patientUniqueID = (TextView) findViewById(R.id.textViewUniqueUserId);

        patientAVGHR.setText(user.getAvgHeartRate());
        patientSTDF.setText(user.getStdfHeartRate());
        patientUniqueID.setText(user.getUniqueUserId());
        sensitivity = user.getSensitivityPref();
        Log.d("SETTINGS SENSITIVITY", sensitivity);

        switch (sensitivity){
            case "1":
                RadioButton b = (RadioButton) findViewById(R.id.radioButtonLight);
                b.setChecked(true);
                break;
            case "2":
                RadioButton b2 = (RadioButton) findViewById(R.id.radioButtonNormal);
                b2.setChecked(true);
                break;
            case "3":
                RadioButton b3 = (RadioButton) findViewById(R.id.radioButtonSensitive);
                b3.setChecked(true);
                break;
        }
    }

    private void setPrefHandler() {
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
        sharedPrefsEditor = sharedPrefs.edit();
    }

    private void setupDrawer() {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("Jouw Coach");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName("Instellingen");
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item2)

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {
                            if (drawerItem instanceof Nameable) {
                                StringHolder itemName = ((Nameable) drawerItem).getName();
                                if (itemName.toString() == "Instellingen") {
                                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    SettingsActivity.this.startActivity(intent);
                                }

                                if (itemName.toString() == "Jouw Coach") {
                                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    SettingsActivity.this.startActivity(intent);
                                }
                            }
                        }

                        return false;
                    }
                })
                .build();
    }

    private void exportDataToServer() {
        //exportUsers();
        //exportHeartRate();
        //exportMonitorData();
        //exportPhysStates();
        //exportSelfReports();

        all_monitordata = db.getMonitorData();
        all_physstate = db.getPhysState();
        all_userdata = db.getUsers();
        all_hrdata = db.getHRData();
        all_selfreport = db.getSelfReports();
        all_activity = db.getActivities();
        all_locweather = db.getAllLocWeather();

        ArrayList<Integer> synced_nums = db.getSyncedNums();



        table1 = createJsonTable(all_monitordata, synced_nums.get(0));
        table2 = createJsonTable(all_physstate, synced_nums.get(1));
        table3 = createJsonTable(all_userdata, synced_nums.get(2));
        table4 = createJsonTable(all_hrdata, synced_nums.get(3));
        table5 = createJsonTable(all_selfreport, synced_nums.get(4));
        table6 = createJsonTable(all_activity, synced_nums.get(5));
        table7 = createJsonTable(all_locweather, synced_nums.get(6));

        db.insertNewSyncedNums(all_monitordata.size(), all_physstate.size(), all_userdata.size(), all_hrdata.size(),
                all_selfreport.size(), all_activity.size(), all_locweather.size());

        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        RequestParams params = new RequestParams();
        params.put("table1", table1);
        params.put("table2", table2);
        params.put("table3", table3);
        params.put("table4", table4);
        params.put("table5", table5);
        params.put("table6", table6);
        params.put("table7", table7);
        client.post("http://dyl.utwente.nl/insert_values_android.php",params ,new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                String m = response.toString();
                //Log.d("php response",m);
                prgDialog.hide();
                Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                prgDialog.hide();
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "error code: "+statusCode, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });


    }

    public String createJsonTable(ArrayList<HashMap<String,String>> db_data, int n){

        String table = "";
        if(db_data.size()>=1) {
            sub_db_data = db_data.subList(n, db_data.size());
            table = gson.toJson(sub_db_data);
        } else table = gson.toJson(db_data);

        return table;
    }

    public void trainModel(){

        feedback_data = db.getFeedbackData();
        String jsondata = gson.toJson(feedback_data);


       // RequestParams params = new RequestParams();
        //params.put("data", jsondata);
        try {
            StringEntity entity = new StringEntity(jsondata);
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30000);
            client.setMaxRetriesAndTimeout(0,30000);
            client.post(SettingsActivity.this,"http://applab.ai.ru.nl:8081/train", entity , "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                    String m = new String(response);
                    //Log.d("php response",m);
                    //prgDialog.hide();
                    Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    // prgDialog.hide();
                    if(statusCode == 404){
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    }else if(statusCode == 500){
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "error code: "+statusCode, Toast.LENGTH_LONG).show();
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


}
