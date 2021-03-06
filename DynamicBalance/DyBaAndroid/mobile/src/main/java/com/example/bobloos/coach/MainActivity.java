package com.example.bobloos.coach;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.fragments.SelfReportFragment;
import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.UserModel;
import com.example.bobloos.shared.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public BroadcastReceiver mMessageReceiverInstance;

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor sharedPrefsEditor;

    Toolbar toolbar;
    UserModel user;
    PhysStateModel physState;
    DatabaseHandler db;
    private static RemoteSensorManager remoteSensorManager;

    long lastMeasurementTime = 0L;
    Switch coachSwitch;
    TextView coachSwitchTextView;
    PendingIntent alarmIntent;
    Boolean processingAlarmIntent = false;
    FloatingActionButton fab;
    ViewPager viewPager;
    int MY_PERMISSION_REQUEST_BODY_SENSORS;
    public GoogleApiClient mApiClient;
    PendingIntent loc_pendingIntent;
    PendingIntent pendingIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        remoteSensorManager = RemoteSensorManager.getInstance(this);
        db = new DatabaseHandler(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupDrawer();
        getSupportActionBar().setTitle("Jouw Coach");

        setPrefHandler();
        setUser();

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupActivity();
            Intent intent = getIntent();
            String pagerViewId = intent.getStringExtra("pageId");
            if (pagerViewId != null){
                viewPager.setCurrentItem(Integer.valueOf(pagerViewId));
            }





        //mApiClient.connect();
        checkAppPermission();
    }

    private void checkAppPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.BODY_SENSORS},
                    MY_PERMISSION_REQUEST_BODY_SENSORS);
        }
    }

    private void setupActivity(){
        Log.d("DEBUG", "VIEWPAGER NOW");
        Log.d("DEBUG: TRUE", String.valueOf(viewPager != null) );
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        // register receivers
        try{
            MainActivity.this.unregisterReceiver(mMessageReceiver);
            Log.d("LOG", "UNREGISTRERD ON CREATE");

        } catch (Throwable e) {
        }

        try{
            MainActivity.this.unregisterReceiver(mMessageReceiver);
            Log.d("LOG", "UNREGISTRERD ON CREATE");

        } catch (Throwable e) {
        }


        Log.d("LOG", "CALLING ON CREATE");
        MainActivity.this.registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast"));
        MainActivity.this.registerReceiver(mMessageReceiver, new IntentFilter("com.example.Broadcast2"));



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        fab = (FloatingActionButton) findViewById(R.id.add_story_button);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewSelfReport.class);
                startActivity(intent);
            }
        });

        coachSwitch = (Switch) findViewById(R.id.coachSwitch);
        coachSwitchTextView = (TextView) findViewById(R.id.coach_switch_text_view);

        CompoundButton.OnCheckedChangeListener checkBtnChangeCoachMode = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(coachSwitch.isShown()){
                    if (isChecked) {
                        long timeInMs = System.currentTimeMillis();

                        db.deleteAllMaster();
                        db.addMasterData(String.valueOf(user.getUniqueUserId()),String.valueOf(System.currentTimeMillis()),
                                "75", "0", "0", "1", "1", "1", "10", "0",
                                "0", null, null, null);

                        mApiClient.connect();
                        db.replaceSwitchAndTS("on", String.valueOf(timeInMs));
                        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60000,
                        //        alarmIntent);
                        Intent intent3 = new Intent();
                        intent3.setAction("com.example.sendMessageAlarm");
                        intent3.putExtra("moment", String.valueOf(1));
                        sharedPrefsEditor.putInt("momentState", 1);
                        MainActivity.this.sendBroadcast(intent3);

                        sharedPrefsEditor.putBoolean("coachSwitchStateChecked", true);

                        coachSwitchTextView.setText("Coaching is on");

                        lastMeasurementTime = System.currentTimeMillis();
                        remoteSensorManager.startMeasurement();
                        Intent intent = new Intent();
                        intent.setAction("com.example.Broadcast1");
                        intent.putExtra("START_TIME", lastMeasurementTime); // get current millisec time
                        MainActivity.this.sendBroadcast(intent);
                        SharedPreferences pref = MainActivity.this.getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putLong("START_TIME", lastMeasurementTime);
                        editor.apply();
                    } else {

                        if(mApiClient.isConnected()){
                            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, loc_pendingIntent);
                            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mApiClient, pendingIntent);
                            mApiClient.disconnect();
                        }



                        //alarmIntent.cancel();
                        // stop measurement of baseline
                        db.replaceSwitchAndTS("off", String.valueOf(System.currentTimeMillis()));
                        sharedPrefsEditor.putBoolean("coachSwitchStateChecked", false);
                        coachSwitchTextView.setText("Coaching is off");
                        Intent intent = new Intent();
                        intent.setAction("com.example.Broadcast1");
                        intent.putExtra("START_TIME", 0L); // clear millisec time
                        MainActivity.this.sendBroadcast(intent);
                        remoteSensorManager.stopMeasurement();
                        SharedPreferences pref = MainActivity.this.getSharedPreferences("START_TIME", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putLong("START_TIME", 0L);
                        editor.apply();
                    }
                    sharedPrefsEditor.commit();
                }

            }
        };


        coachSwitch.setOnCheckedChangeListener(checkBtnChangeCoachMode);
        setSwitch();




        // start new intent
        Intent intent = new Intent();
        intent.setAction("com.example.Broadcast");
        intent.putExtra("START_TIME", 0L); // clear millisec time
        MainActivity.this.sendBroadcast(intent);
    }





    private void setPrefHandler(){
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, 0);
        sharedPrefsEditor = sharedPrefs.edit();
    }

    private void setUser(){
        user = db.getUser(1);
        if(user == null){
            db.addUser(new UserModel());
            user = db.getUser(1);

            String[] context_list = new String[]{"unknown", "appointment/presentation", "working/studying",
                    "social situation", "change of schedule/plan"};

            for(int i=0; i<context_list.length; i++)
            {
                db.addContextData(context_list[i]);
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new CoachListFragment(), "JOUW METINGEN");
        adapter.addFragment(new SelfReportFragment(), "ZELFRAPPORTAGE");

//        adapter.addFragment(new HelpFragment(), "TECHNISCHE HULP");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    fab.setVisibility(View.INVISIBLE);
                }

                if (position == 1){
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    private void setupDrawer(){
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
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                MainActivity.this.startActivity(intent);
                            }

                            if (itemName.toString() == "Jouw Coach") {
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                MainActivity.this.startActivity(intent);
                            }
                        }
                    }

                    return false;
                }
            })
            .build();
    }

    // handler for received intents
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.example.Broadcast2")) {
                MainActivity.this.getFragmentManager();
            }
            else {
                // extract data included in the intent
                /*
                try {
                    // get extras
                    float[] heartRate = intent.getFloatArrayExtra("HR");
                    int accuracy = intent.getIntExtra("ACCR", 0);
                    int sensorType = intent.getIntExtra("SENSOR_TYPE", 0);
                    int userId = user.getId();
                    long timeInMs = System.currentTimeMillis();

                    Log.d("DATA FROM SENSOR", String.valueOf(heartRate[0]));

                    HeartRateDataModel hrModel = new HeartRateDataModel( );
                    hrModel.setUserId(String.valueOf(userId));
                    hrModel.setAccuracy(String.valueOf(accuracy));
                    hrModel.setUniqueUserId(user.getUniqueUserId());
                    hrModel.setHeartRate(String.valueOf(heartRate[0]));
                    hrModel.setMeasurementTime(timeInMs);
                    Log.d("ReceiverMain", "Got HR: " + heartRate[0] + ". Got Accuracy: " + accuracy);
                    db.addHeartRateData(hrModel);
                } catch (Exception e) {
                }
                */
            }
        }
    };




    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 33);
        }
        else{
            Log.d("LOCATION", "permission granted");
            LocationRequest request = new LocationRequest();
            request.setInterval(600000);
            request.setFastestInterval(600000);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            Intent loc_intent = new Intent( this, LocationService.class );
            loc_pendingIntent = PendingIntent.getService( this, 1, loc_intent, PendingIntent.FLAG_UPDATE_CURRENT );
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, request, loc_pendingIntent);
        }

        Log.e("RANDY", "ActivityRecognitionApi started");
        Intent intent = new Intent( this, ActivityRecognizedService.class );
        pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 60000, pendingIntent );






    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 33: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d("LOCATION", "permission granted");
                    LocationRequest request = new LocationRequest();
                    request.setInterval(600000);
                    request.setFastestInterval(600000);
                    request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    Intent loc_intent = new Intent( this, LocationService.class );
                    loc_pendingIntent = PendingIntent.getService( this, 1, loc_intent, PendingIntent.FLAG_UPDATE_CURRENT );
                    LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, request, loc_pendingIntent);

                } else {
                    // permission was denied
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void setSwitch(){

        HashMap<String, String> switch_info = db.getSwitchandTS();
        String coach_switch = switch_info.get("switch");

        switch((coach_switch != null) ? coach_switch : "off"){
            case "on":
                coachSwitch.setChecked(true);
                coachSwitchTextView.setText("Coaching is on");
                break;
            case "off":
                coachSwitch.setChecked(false);
                coachSwitchTextView.setText("Coaching is off");
                break;
        }

    }

    @Override
    protected void onStart(){
        super.onStart();

        Log.e("GOOGLE API CONNECTED", String.valueOf(mApiClient.isConnected()));
        setSwitch();

    }

}

