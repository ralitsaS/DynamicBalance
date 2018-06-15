package com.example.bobloos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.bobloos.model.HeartRateDataModel;
import com.example.bobloos.model.MonitorDataModel;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.SelfReportModel;
import com.example.bobloos.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 9;
    // Database Name
    private static final String DATABASE_NAME = "coachDB";

    // Table Names
    private static final String TABLE_MONITOR_DATA = "monitorData";
    private static final String TABLE_PHYS_STATE = "physStates";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_HEART_RATE_DATA = "heartRateData";
    private static final String TABLE_SELF_REPORTS = "selfReports";
    private static final String TABLE_ACTIVITY = "recActivity";
    private static final String TABLE_LOC_WEATHER = "locWeather";
    private static final String TABLE_SYNCED = "syncedNums";
    private static final String TABLE_MASTER = "master";
    private static final String TABLE_FEEDBACK = "feedback";
    private static final String TABLE_CONTEXT = "context_list";

    // COMMON column names
    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_UNIQUE_USER_ID = "uniqueUserId";
    private static final String KEY_MEASUREMENT_TIME = "measurementTime";


    // MONITOR DATA column names
    private static final String KEY_SENSOR_UPDATE_TIME = "sensorUpdateTime";
    private static final String KEY_SENSOR_ID = "sensorId";
    private static final String KEY_SENSOR_VAL = "sensorVal";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_DAY = "day";

    // PHYS STATE column names
    private static final String KEY_STATE_TIME_STAMP = "stateTimeStamp";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_FEEDBACK = "feedback";
    private static final String KEY_CONTEXT_DESCRIPTION = "contextDescription";

    private static final String KEY_CONTEXT = "context";
    private static final String KEY_PREDICTION = "prediction";
    private static final String KEY_Y = "y";

    // USERS column names
    private static final String KEY_AVG_HEART_RATE = "avgHeartRate";
    private static final String KEY_STDF_HEART_RATE = "stdfHeartRate";
    private static final String KEY_SENSITIVY_PREF = "sensitivityPref";

    // HEART RATE DATA column names
    private static final String KEY_HEART_RATE = "heartRate";
    private static final String KEY_ACCURACY = "accuracy";

    // HEART RATE DATA column names
    private static final String KEY_REPORT_TEXT = "reportText";
    private static final String KEY_TIMESTAMP = "timestamp";

    // ACTIVITY column names
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_CONFIDENCE = "confidence";
    private static final String KEY_TS = "ts";

    // ACTIVITY column names
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_TEMPERATURE = "temp";
    private static final String KEY_HUMIDITY = "humidity";
    private static final String KEY_RAIN = "rain";
    private static final String KEY_CLOUDS = "clouds";

    //nums column names
    public static final String KEY_NUM_1 = "num1";
    public static final String KEY_NUM_2 = "num2";
    public static final String KEY_NUM_3 = "num3";
    public static final String KEY_NUM_4 = "num4";
    public static final String KEY_NUM_5 = "num5";
    public static final String KEY_NUM_6 = "num6";
    public static final String KEY_NUM_7 = "num7";

    public static final String KEY_ACCELERATION = "acceleration";
    public static final String KEY_STEPS = "steps";
    public static final String KEY_USER_HR = "userHR";
    public static final String KEY_USER_STD = "userSTD";
    public static final String KEY_CONTEXTLIST = "contextlist";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MONITOR_DATA_TABLE = "CREATE TABLE "+TABLE_MONITOR_DATA+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SENSOR_UPDATE_TIME + " TEXT, " +
                KEY_SENSOR_ID + " TEXT, " +
                KEY_USER_ID + " TEXT, " +
                KEY_ACCURACY + " TEXT, " +
                KEY_MEASUREMENT_TIME + " TEXT, " +
                KEY_SENSOR_VAL + " TEXT, " +
                KEY_HOUR + " TEXT, " +
                KEY_DAY + " TEXT )";

        String CREATE_PHYS_STATE_TABLE = "CREATE TABLE "+TABLE_PHYS_STATE+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_STATE_TIME_STAMP + " TEXT, "+
                KEY_USER_ID + " TEXT, "+
                KEY_LEVEL + " TEXT, "+
                KEY_FEEDBACK + " TEXT, "+
                KEY_CONTEXT_DESCRIPTION + " TEXT)";

        String CREATE_USERS_TABLE = "CREATE TABLE "+TABLE_USERS+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_UNIQUE_USER_ID + " TEXT, "+
                KEY_AVG_HEART_RATE + " TEXT, "+
                KEY_SENSITIVY_PREF + " TEXT, "+
                KEY_STDF_HEART_RATE + " TEXT)";

        String CREATE_HEART_RATE_DATA_TABLE = "CREATE TABLE "+TABLE_HEART_RATE_DATA+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_USER_ID + " TEXT, "+
                KEY_UNIQUE_USER_ID + " TEXT, "+
                KEY_HEART_RATE + " TEXT, "+
                KEY_ACCURACY + " TEXT, "+
                KEY_MEASUREMENT_TIME + " INTEGER)";

        String CREATE_SELF_REPORT_TABLE = "CREATE TABLE "+TABLE_SELF_REPORTS+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_USER_ID + " TEXT, "+
                KEY_UNIQUE_USER_ID + " TEXT, "+
                KEY_REPORT_TEXT + " TEXT, "+
                KEY_TIMESTAMP + " TEXT)";

        String CREATE_ACTIVITY_TABLE = "CREATE TABLE "+TABLE_ACTIVITY+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_USER_ID + " TEXT, "+
                KEY_ACTIVITY + " TEXT, "+
                KEY_CONFIDENCE + " TEXT, "+
                KEY_TS + " TEXT)";

        String CREATE_LOCATION_TABLE = "CREATE TABLE "+TABLE_LOC_WEATHER+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_USER_ID + " TEXT, "+
                KEY_LATITUDE + " TEXT, "+
                KEY_LONGITUDE + " TEXT, "+
                KEY_TEMPERATURE + " TEXT, "+
                KEY_HUMIDITY + " TEXT, "+
                KEY_RAIN + " TEXT, "+
                KEY_CLOUDS + " TEXT, "+
                KEY_TS + " TEXT)";

        String CREATE_TABLE_SYNCED = "CREATE TABLE " + TABLE_SYNCED  + "("
                + KEY_NUM_1  + " INTEGER, "
                + KEY_NUM_2  + " INTEGER, "
                + KEY_NUM_3  + " INTEGER, "
                + KEY_NUM_4  + " INTEGER, "
                + KEY_NUM_5  + " INTEGER, "
                + KEY_NUM_6  + " INTEGER, "
                + KEY_NUM_7  + " INTEGER); ";

        String CREATE_MASTER_TABLE = "CREATE TABLE "+TABLE_MASTER+" ( " +
                KEY_UNIQUE_USER_ID + " TEXT, "+
                KEY_TIMESTAMP + " TEXT, "+
                KEY_HEART_RATE + " TEXT, "+
                KEY_ACCELERATION + " TEXT, "+
                KEY_STEPS + " TEXT, "+
                KEY_HOUR + " TEXT, "+
                KEY_DAY + " TEXT, "+
                KEY_ACTIVITY + " TEXT, "+
                KEY_TEMPERATURE + " TEXT, "+
                KEY_HUMIDITY + " TEXT, "+
                KEY_CLOUDS + " TEXT, "+
                KEY_LEVEL + " TEXT, "+
                KEY_USER_HR + " TEXT, "+
                KEY_USER_STD + " TEXT); ";

        String CREATE_FEEDBACK_TABLE = "CREATE TABLE "+TABLE_FEEDBACK+" ( " +
                KEY_UNIQUE_USER_ID + " TEXT, "+
                KEY_TIMESTAMP + " TEXT, "+
                KEY_PREDICTION + " TEXT, "+
                KEY_Y + " TEXT, "+
                KEY_CONTEXT + " TEXT); ";

        String CREATE_CONTEXT_TABLE = "CREATE TABLE "+TABLE_CONTEXT+" ( " +
                KEY_CONTEXTLIST + " TEXT); ";


        //db.execSQL(CREATE_TABLE_SYNCED);

        db.execSQL(CREATE_MONITOR_DATA_TABLE);
        db.execSQL(CREATE_PHYS_STATE_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_HEART_RATE_DATA_TABLE);
        db.execSQL(CREATE_SELF_REPORT_TABLE);
        db.execSQL(CREATE_ACTIVITY_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_TABLE_SYNCED);
        db.execSQL(CREATE_MASTER_TABLE);
        db.execSQL(CREATE_FEEDBACK_TABLE);
        db.execSQL(CREATE_CONTEXT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONITOR_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHYS_STATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEART_RATE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SELF_REPORTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOC_WEATHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNCED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTEXT);
        this.onCreate(db);
    }

    //////////

    public void addMasterData(String uni_id, String ts, String HR, String accel, String steps, String hour,
                              String day, String act, String temp, String humidity, String clouds, String level,
                              String userHR, String userSTD) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UNIQUE_USER_ID, uni_id);
        values.put(KEY_TIMESTAMP, ts);
        values.put(KEY_HEART_RATE, HR);
        values.put(KEY_ACCELERATION, accel);
        values.put(KEY_STEPS, steps);
        values.put(KEY_HOUR, hour);
        values.put(KEY_DAY, day);
        values.put(KEY_ACTIVITY, act);
        values.put(KEY_TEMPERATURE, temp);
        values.put(KEY_HUMIDITY, humidity);
        values.put(KEY_CLOUDS, clouds);
        values.put(KEY_LEVEL, level);
        values.put(KEY_USER_HR, userHR);
        values.put(KEY_USER_STD, userSTD);
        db.insert(TABLE_MASTER, null, values);
        db.close();


    }

    public ArrayList<HashMap<String, String>> getMasterData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_MASTER;
        ArrayList<HashMap<String, String>> MasterDataAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> MasterData = new HashMap<String, String>();
                MasterData.put("uni_id", cursor.getString(0));
                MasterData.put("timestamp", cursor.getString(1));
                MasterData.put("heart_rate", cursor.getString(2));
                MasterData.put("acceleration", cursor.getString(3));
                MasterData.put("steps_per_min", cursor.getString(4));
                MasterData.put("hour", cursor.getString(5));
                MasterData.put("day", cursor.getString(6));
                MasterData.put("activity", cursor.getString(7));
                MasterData.put("temp", cursor.getString(8));
                MasterData.put("humidity", cursor.getString(9));
                MasterData.put("clouds", cursor.getString(10));
                MasterData.put("level", cursor.getString(11));
                MasterData.put("userHR", cursor.getString(12));
                MasterData.put("userSTD", cursor.getString(13));
                MasterDataAll.add(MasterData);

            } while (cursor.moveToNext());
        }

        return MasterDataAll;
    }

    public void deleteAllMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from " + TABLE_MASTER);
        db.close();
    }

    public void addFeedbackData(String uni_id, String ts, String prediction, String y, String context) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UNIQUE_USER_ID, uni_id);
        values.put(KEY_TIMESTAMP, ts);
        values.put(KEY_PREDICTION, prediction);
        values.put(KEY_Y, y);
        values.put(KEY_CONTEXT, context);
        db.insert(TABLE_FEEDBACK, null, values);
        db.close();


    }

    public ArrayList<HashMap<String, String>> getFeedbackData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_FEEDBACK;
        ArrayList<HashMap<String, String>> FeedbackDataAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> FeedbackData = new HashMap<String, String>();
                FeedbackData.put("uni_id", cursor.getString(0));
                FeedbackData.put("timestamp", cursor.getString(1));
                FeedbackData.put("prediction", cursor.getString(2));
                FeedbackData.put("y", cursor.getString(3));
                FeedbackData.put("context", cursor.getString(4));
                FeedbackDataAll.add(FeedbackData);

            } while (cursor.moveToNext());
        }

        return FeedbackDataAll;
    }

    public HashMap<String, String> getSingleFeedback(String ts) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_FEEDBACK + " WHERE " + KEY_TIMESTAMP + " = " + ts;
        HashMap<String, String> Feedback = new HashMap<String, String>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            Feedback.put("uni_id", cursor.getString(0));
            Feedback.put("timestamp", cursor.getString(1));
            Feedback.put("prediction", cursor.getString(2));
            Feedback.put("y", cursor.getString(3));
            Feedback.put("context", cursor.getString(4));
        }

        return Feedback;
    }

    public void updateFeedback(String timestamp, String y, String context){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Y, y);
        values.put(KEY_CONTEXT, context);

        db.update(TABLE_FEEDBACK, values, KEY_TIMESTAMP + " = ?",
                new String[] { timestamp });
    }

    public void deleteFeedback(String timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEEDBACK, //table name
                KEY_TIMESTAMP + " = ?",  // selections
                new String[]{timestamp}); //selections args
    }

    public void addContextData(String context) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTEXTLIST, context);
        db.insert(TABLE_CONTEXT, null, values);
        db.close();
    }

    public List<String> getContextData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_CONTEXT;
        List<String> ContextData = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ContextData.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }

        return ContextData;
    }

    // ALL CALLS RELATED TO ADDING MONITOR DATA

    public void addMonitorData(MonitorDataModel monitorData) {
        Log.d("addMonitorData", monitorData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_UPDATE_TIME, monitorData.getSensorUpdateTime());
        values.put(KEY_SENSOR_ID, monitorData.getSensorId());
        values.put(KEY_USER_ID, monitorData.getUserId());
        values.put(KEY_SENSOR_VAL, monitorData.getSensorVal());
        values.put(KEY_ACCURACY, monitorData.getAccuracy());
        values.put(KEY_MEASUREMENT_TIME, monitorData.getMeasurementTime());
        values.put(KEY_HOUR, monitorData.getHour());
        values.put(KEY_DAY, monitorData.getDay());
        db.insert(TABLE_MONITOR_DATA, null, values);
        db.close();
    }

    public List<MonitorDataModel> getAllUserMonitorData() {
        List<MonitorDataModel> monitorDatas = new LinkedList<MonitorDataModel>();
        String query = "SELECT  * FROM " + TABLE_MONITOR_DATA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MonitorDataModel monitorData = null;
        if (cursor.moveToFirst()) {
            do {
                monitorData = new MonitorDataModel();
                monitorData.setId(Integer.parseInt(cursor.getString(0)));
                monitorData.setSensorUpdateTime(cursor.getString(1));
                monitorData.setSensorId(cursor.getString(2));
                monitorData.setUserId(cursor.getString(3));
                monitorData.setAccuracy(cursor.getString(4));
                monitorData.setMeasurementTime(cursor.getString(5));
                monitorData.setSensorVal(cursor.getString(6));
                monitorDatas.add(monitorData);
            } while (cursor.moveToNext());
        }
        return monitorDatas;
    }


    public void addActivity(String user_id, String activity, String confidence, String ts) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_ID, user_id);
        values.put(KEY_ACTIVITY, activity);
        values.put(KEY_CONFIDENCE, confidence);
        values.put(KEY_TS, ts);

        db.insert(TABLE_ACTIVITY, null, values);
        db.close();
    }

    public String getLastActivity(){
        String last_activity = "NONE";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_ACTIVITY + " ORDER BY ts DESC limit 1";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            last_activity = cursor.getString(2);
        }

        return last_activity;
    }

    public HashMap<String, String> getLastStepCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, String> Steps = new HashMap<String, String>();
        String query = "SELECT  * FROM " + TABLE_MONITOR_DATA + " WHERE " + KEY_SENSOR_ID +
                " = 19 ORDER BY " + KEY_MEASUREMENT_TIME + " DESC limit 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            Steps.put("timestamp", cursor.getString(5));
            Steps.put("steps", cursor.getString(6));

        }

        return Steps;
    }

    public void addLocWeather(String user_id, String lat, String lon, String temp, String humidity, String rain, String clouds, String ts) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_ID, user_id);
        values.put(KEY_LATITUDE, lat);
        values.put(KEY_LONGITUDE, lon);
        values.put(KEY_TEMPERATURE, temp);
        values.put(KEY_HUMIDITY, humidity);
        values.put(KEY_RAIN, rain);
        values.put(KEY_CLOUDS, clouds);
        values.put(KEY_TS, ts);

        db.insert(TABLE_LOC_WEATHER, null, values);
        db.close();
    }


    ////////////////// GET ALL TABLES ARRAYLIST////////////////////


    public ArrayList<HashMap<String, String>> getMonitorData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_MONITOR_DATA;
        ArrayList<HashMap<String, String>> MonitorDataAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> MonitorData = new HashMap<String, String>();
                MonitorData.put("id1", cursor.getString(0));
                MonitorData.put("sensor_update", cursor.getString(1));
                MonitorData.put("sensor_id", cursor.getString(2));
                MonitorData.put("user_id", cursor.getString(3));
                MonitorData.put("accuracy", cursor.getString(4));
                MonitorData.put("timestamp", cursor.getString(5));
                MonitorData.put("sensor_value", cursor.getString(6));
                MonitorData.put("hour", cursor.getString(7));
                MonitorData.put("day", cursor.getString(8));
                MonitorDataAll.add(MonitorData);

            } while (cursor.moveToNext());
        }

        return MonitorDataAll;
    }

    public ArrayList<HashMap<String, String>> getPhysState() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_PHYS_STATE;
        ArrayList<HashMap<String, String>> PhysStatesAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> PhysState = new HashMap<String, String>();
                PhysState.put("id2", cursor.getString(0));
                PhysState.put("state_timestamp", cursor.getString(1));
                PhysState.put("user_id", cursor.getString(2));
                PhysState.put("level", cursor.getString(3));
                PhysState.put("feedback", cursor.getString(4));
                PhysState.put("context", cursor.getString(5));
                PhysStatesAll.add(PhysState);

            } while (cursor.moveToNext());
        }

        return PhysStatesAll;
    }

    public ArrayList<HashMap<String, String>> getUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_USERS;
        ArrayList<HashMap<String, String>> UsersAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> User = new HashMap<String, String>();
                User.put("id3", cursor.getString(0));
                User.put("uni_id", cursor.getString(1));
                User.put("avg_hr", cursor.getString(2));
                User.put("sensitivity", cursor.getString(3));
                User.put("std_hr", cursor.getString(4));
                UsersAll.add(User);

            } while (cursor.moveToNext());
        }

        return UsersAll;
    }

    public ArrayList<HashMap<String, String>> getHRData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_HEART_RATE_DATA;
        ArrayList<HashMap<String, String>> HRAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> HRData = new HashMap<String, String>();
                HRData.put("id4", cursor.getString(0));
                HRData.put("user_id", cursor.getString(1));
                HRData.put("uni_id", cursor.getString(2));
                HRData.put("heart_rate", cursor.getString(3));
                HRData.put("accuracy", cursor.getString(4));
                HRData.put("measure_time", cursor.getString(5));
                HRAll.add(HRData);

            } while (cursor.moveToNext());
        }

        return HRAll;
    }

    public ArrayList<HashMap<String, String>> getSelfReports() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_SELF_REPORTS;
        ArrayList<HashMap<String, String>> SelfReportsAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> SelfReport = new HashMap<String, String>();
                SelfReport.put("id5", cursor.getString(0));
                SelfReport.put("user_id", cursor.getString(1));
                SelfReport.put("uni_id", cursor.getString(2));
                SelfReport.put("report", cursor.getString(3));
                SelfReport.put("timestamp", cursor.getString(4));
                SelfReportsAll.add(SelfReport);

            } while (cursor.moveToNext());
        }

        return SelfReportsAll;
    }

    public ArrayList<HashMap<String, String>> getActivities() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_ACTIVITY;
        ArrayList<HashMap<String, String>> ActivitiesAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> Activity = new HashMap<String, String>();
                Activity.put("id6", cursor.getString(0));
                Activity.put("user_id", cursor.getString(1));
                Activity.put("activity", cursor.getString(2));
                Activity.put("confidence", cursor.getString(3));
                Activity.put("timestamp", cursor.getString(4));
                ActivitiesAll.add(Activity);

            } while (cursor.moveToNext());
        }

        return ActivitiesAll;
    }

    public ArrayList<HashMap<String, String>> getAllLocWeather() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT  * FROM " + TABLE_LOC_WEATHER;
        ArrayList<HashMap<String, String>> LocWeatherAll = new ArrayList<HashMap<String, String>>();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> LocWeather = new HashMap<String, String>();
                LocWeather.put("id7", cursor.getString(0));
                LocWeather.put("user_id", cursor.getString(1));
                LocWeather.put("lat", cursor.getString(2));
                LocWeather.put("lon", cursor.getString(3));
                LocWeather.put("temp", cursor.getString(4));
                LocWeather.put("humidity", cursor.getString(5));
                LocWeather.put("rain", cursor.getString(6));
                LocWeather.put("clouds", cursor.getString(7));
                LocWeather.put("timestamp", cursor.getString(8));
                LocWeatherAll.add(LocWeather);

            } while (cursor.moveToNext());
        }

        return LocWeatherAll;
    }

    //////////////////////////////////////////////



    public List<MonitorDataModel> getAllUserMonitorDataByLastMeasurementTime(long measurementTime) {
        List<MonitorDataModel> monitorDatas = new LinkedList<MonitorDataModel>();
        String query = "SELECT  * FROM " + TABLE_MONITOR_DATA + " where measurementTime='" + String.valueOf(measurementTime) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        MonitorDataModel monitorData = null;
        if (cursor.moveToFirst()) {
            do {
                monitorData = new MonitorDataModel();
                monitorData.setId(Integer.parseInt(cursor.getString(0)));
                monitorData.setSensorUpdateTime(cursor.getString(1));
                monitorData.setSensorId(cursor.getString(2));
                monitorData.setUserId(cursor.getString(3));
                monitorData.setAccuracy(cursor.getString(4));
                monitorData.setMeasurementTime(cursor.getString(5));
                monitorData.setSensorVal(cursor.getString(6));
                monitorDatas.add(monitorData);
            } while (cursor.moveToNext());
        }
        return monitorDatas;
    }

    public void deleteMeasurementDataByMeasurementTime(long measurementTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MONITOR_DATA, //table name
                "measurementTime" + " = ?",  // selections
                new String[]{String.valueOf(measurementTime)}); //selections args
        db.close();
        Log.d("db", "deleted");
    }


    // ALL CALLS RELATED PHYSSTATES

    // ADD PHYSSTATE
    public void addPhysState(PhysStateModel physState) {
        Log.d("addPhysState", physState.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATE_TIME_STAMP, physState.getStateTimeStamp());
        values.put(KEY_USER_ID, physState.getUserId());
        values.put(KEY_LEVEL, physState.getLevel());
        values.put(KEY_CONTEXT_DESCRIPTION, physState.getContextDescription());
        db.insert(TABLE_PHYS_STATE, null, values);
        db.close();
    }

    public PhysStateModel getPhysState(long state_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PHYS_STATE + " WHERE " + KEY_ID + " = " + state_id;

        Cursor c = db.rawQuery(selectQuery, null);

        Log.d("DATABASE", "GETTING PhysSate ");
        if(c !=null) {
            Log.d("DATABASE", "USER NOT NILL");
            if (c.moveToFirst()) {
                PhysStateModel physState = new PhysStateModel();
                physState.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                physState.setUserId(c.getString(c.getColumnIndex(KEY_USER_ID)));
                physState.setLevel(c.getString(c.getColumnIndex(KEY_LEVEL)));
                physState.setStateTimeStamp(c.getString(c.getColumnIndex(KEY_STATE_TIME_STAMP)));
                physState.setContextDescription(c.getString(c.getColumnIndex(KEY_CONTEXT_DESCRIPTION)));
                return physState;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int updatePhysState(PhysStateModel physState){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        String context_desc = physState.getContextDescription();
        if(context_desc.equals(null)){
            values.put(KEY_CONTEXT_DESCRIPTION, context_desc);
            values.put(KEY_FEEDBACK, context_desc);
        } else {
            String[] split = context_desc.split(":");
            values.put(KEY_CONTEXT_DESCRIPTION, split[1]);
            if(split[0].equals("yes")) values.put(KEY_FEEDBACK, "1");
                else values.put(KEY_FEEDBACK, "0");
        }



        Log.d("UPDATING PhysState", String.valueOf(physState.getId()));
        // updating row
        return db.update(TABLE_PHYS_STATE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(physState.getId()) });
    }


    // GET LIST OF ALL PHYSSTATES
    public List<PhysStateModel> getAllPhysStates() {
        List<PhysStateModel> physStates = new LinkedList<PhysStateModel>();
        String query = "SELECT  * FROM " + TABLE_PHYS_STATE + " ORDER BY stateTimeStamp DESC limit 500";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PhysStateModel physState = null;
        if (cursor.moveToFirst()) {
            do {
                physState = new PhysStateModel();
                physState.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                physState.setStateTimeStamp(cursor.getString(cursor.getColumnIndex(KEY_STATE_TIME_STAMP)));
                physState.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                physState.setLevel(cursor.getString(cursor.getColumnIndex(KEY_LEVEL)));
                physState.setContextDescription(cursor.getString(cursor.getColumnIndex(KEY_CONTEXT_DESCRIPTION)));
                physStates.add(physState);
            } while (cursor.moveToNext());
        }
        return physStates;
    }

    //GET LAST PHYSSTATE
    public PhysStateModel getLastPhysState() {
        List<PhysStateModel> physStates = new LinkedList<PhysStateModel>();
        String query = "SELECT  * FROM " + TABLE_PHYS_STATE + " ORDER BY stateTimeStamp DESC limit 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PhysStateModel physState = null;
        if (cursor.moveToFirst()) {
            physState = new PhysStateModel();
            physState.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            physState.setStateTimeStamp(cursor.getString(cursor.getColumnIndex(KEY_STATE_TIME_STAMP)));
            physState.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
            physState.setLevel(cursor.getString(cursor.getColumnIndex(KEY_LEVEL)));
            physState.setContextDescription(cursor.getString(cursor.getColumnIndex(KEY_CONTEXT_DESCRIPTION)));
        }
        Log.d("gettAllPhysStates()", physStates.toString());
        return physState;
    }

    //GET LAST PHYSSTATE
    public List<PhysStateModel> getLastFivePhysStates() {
        List<PhysStateModel> physStates = new LinkedList<PhysStateModel>();
        String query = "SELECT  * FROM " + TABLE_PHYS_STATE + " ORDER BY stateTimeStamp DESC limit 5";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        PhysStateModel physState = null;
        if (cursor.moveToFirst()) {
            do {
                physState = new PhysStateModel();
                physState.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                physState.setStateTimeStamp(cursor.getString(cursor.getColumnIndex(KEY_STATE_TIME_STAMP)));
                physState.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                physState.setLevel(cursor.getString(cursor.getColumnIndex(KEY_LEVEL)));
                physState.setContextDescription(cursor.getString(cursor.getColumnIndex(KEY_CONTEXT_DESCRIPTION)));
                physStates.add(physState);
            } while (cursor.moveToNext());
        }
        return physStates;
    }


    // ALL CALLS RELATED TO USERS
    public void addUser(UserModel userData) {
        Log.d("addUserData", userData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_UNIQUE_USER_ID, userData.getUniqueUserId());
        values.put(KEY_AVG_HEART_RATE, userData.getAvgHeartRate());
        values.put(KEY_STDF_HEART_RATE, userData.getStdfHeartRate());
        values.put(KEY_SENSITIVY_PREF, userData.getSensitivityPref());
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public UserModel getUser(long user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE " + KEY_ID + " = " + user_id;

        Cursor c = db.rawQuery(selectQuery, null);

        Log.d("DATABASE", "GETTING USER");
        if(c !=null) {
            Log.d("DATABASE", "USER NOT NILL");
            if (c.moveToFirst()) {
                UserModel user = new UserModel();
                user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                user.setUniqueUserId(c.getString(c.getColumnIndex(KEY_UNIQUE_USER_ID)));
                user.setAvgHeartRate(c.getString(c.getColumnIndex(KEY_AVG_HEART_RATE)));
                user.setStdfHeartRate(c.getString(c.getColumnIndex(KEY_STDF_HEART_RATE)));
                user.setSensitivityPref(c.getString(c.getColumnIndex(KEY_SENSITIVY_PREF)));
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int updateUser(UserModel user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STDF_HEART_RATE, user.getStdfHeartRate());
        values.put(KEY_AVG_HEART_RATE, user.getAvgHeartRate());
        values.put(KEY_SENSITIVY_PREF, user.getSensitivityPref());
        // updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> users = new LinkedList<UserModel>();
        String query = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        UserModel user = null;
        if (c.moveToFirst()) {
            do {
                user = new UserModel();
                user.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                user.setUniqueUserId(c.getString(c.getColumnIndex(KEY_UNIQUE_USER_ID)));
                user.setAvgHeartRate(c.getString(c.getColumnIndex(KEY_AVG_HEART_RATE)));
                user.setStdfHeartRate(c.getString(c.getColumnIndex(KEY_STDF_HEART_RATE)));
                user.setSensitivityPref(c.getString(c.getColumnIndex(KEY_SENSITIVY_PREF)));
                users.add(user);
            } while (c.moveToNext());
        }
        return users;
    }

    public void addHeartRateData(HeartRateDataModel heartRateData) {
        Log.d("addHRData", heartRateData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, heartRateData.getUserId());
        values.put(KEY_UNIQUE_USER_ID, heartRateData.getUniqueUserId());
        values.put(KEY_HEART_RATE, heartRateData.getHeartRate());
        values.put(KEY_ACCURACY, heartRateData.getAccuracy());


        values.put(KEY_MEASUREMENT_TIME, heartRateData.getMeasurementTime());

        Log.d("addHRData - Values", values.toString());
        db.insert(TABLE_HEART_RATE_DATA, null, values);
        db.close();
    }

    public List<HeartRateDataModel> getAllHeartRate() {
        List<HeartRateDataModel> heartRates = new LinkedList<HeartRateDataModel>();
        String query = "SELECT  * FROM " + TABLE_HEART_RATE_DATA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        HeartRateDataModel heartRate = null;
        if (cursor.moveToFirst()) {
            do {
                heartRate = new HeartRateDataModel();
                heartRate.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                heartRate.setUniqueUserId(cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_USER_ID)));
                heartRate.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                heartRate.setHeartRate(cursor.getString(cursor.getColumnIndex(KEY_HEART_RATE)));
                heartRate.setAccuracy(cursor.getString(cursor.getColumnIndex(KEY_ACCURACY)));
                heartRate.setMeasurementTime(cursor.getLong(cursor.getColumnIndex(KEY_MEASUREMENT_TIME)));
                heartRates.add(heartRate);
            } while (cursor.moveToNext());
        }
        return heartRates;
    }


    public List<HeartRateDataModel> getAllHeartRateAfterTimeStamp(Long measurementTime) {
        List<HeartRateDataModel> heartRates = new LinkedList<HeartRateDataModel>();
        String query = "SELECT  * FROM " + TABLE_HEART_RATE_DATA + " WHERE measurementTime > '" + measurementTime + "'";;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        HeartRateDataModel heartRate = null;
        if (cursor.moveToFirst()) {
            do {
                heartRate = new HeartRateDataModel();
                heartRate.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                heartRate.setUniqueUserId(cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_USER_ID)));
                heartRate.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                heartRate.setHeartRate(cursor.getString(cursor.getColumnIndex(KEY_HEART_RATE)));
                heartRate.setAccuracy(cursor.getString(cursor.getColumnIndex(KEY_ACCURACY)));
                heartRate.setMeasurementTime(cursor.getLong(cursor.getColumnIndex(KEY_MEASUREMENT_TIME)));
                heartRates.add(heartRate);
            } while (cursor.moveToNext());
        }
        return heartRates;
    }

    public List<HeartRateDataModel> getLatestMeasures() {
        List<HeartRateDataModel> heartRates = new LinkedList<HeartRateDataModel>();
        String query = "SELECT * FROM " + TABLE_HEART_RATE_DATA + " ORDER BY measurementTime DESC limit 10";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        HeartRateDataModel heartRate = null;
        if (cursor.moveToFirst()) {
            do {
                heartRate = new HeartRateDataModel();
                heartRate.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                heartRate.setUniqueUserId(cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_USER_ID)));
                heartRate.setUserId(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                heartRate.setHeartRate(cursor.getString(cursor.getColumnIndex(KEY_HEART_RATE)));
                heartRate.setAccuracy(cursor.getString(cursor.getColumnIndex(KEY_ACCURACY)));
                heartRate.setMeasurementTime(cursor.getLong(cursor.getColumnIndex(KEY_MEASUREMENT_TIME)));
                heartRates.add(heartRate);
            } while (cursor.moveToNext());
        }
        Log.d("getlatest10heartrates()", heartRates.toString());
        return heartRates;
    }

    // ALL CALLS RELATED TO SELF REPORTS
    public void addSelfReport(SelfReportModel selfReportData) {
        Log.d("addSelfReport", selfReportData.toString());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, selfReportData.getUserId());
        values.put(KEY_UNIQUE_USER_ID, selfReportData.getUniqueUserId());
        values.put(KEY_REPORT_TEXT, selfReportData.getReportText());
        values.put(KEY_TIMESTAMP, selfReportData.getTimestamp());
        db.insert(TABLE_SELF_REPORTS, null, values);
        db.close();
    }

    public SelfReportModel getSelfReport(long self_report_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_SELF_REPORTS + " WHERE " + KEY_ID + " = " + self_report_id;

        Cursor c = db.rawQuery(selectQuery, null);

        Log.d("DATABASE", "GETTING SELF REPORT FOR USER");
        if(c !=null) {
            Log.d("DATABASE", "USER NOT NILL");
            if (c.moveToFirst()) {
                SelfReportModel selfReport = new SelfReportModel();
                selfReport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                selfReport.setUserId(c.getString(c.getColumnIndex(KEY_USER_ID)));
                selfReport.setUniqueUserId(c.getString(c.getColumnIndex(KEY_UNIQUE_USER_ID)));
                selfReport.setReportText(c.getString(c.getColumnIndex(KEY_REPORT_TEXT)));
                selfReport.setTimeStamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));
                return selfReport;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public int updateSelfReport(SelfReportModel selfReport){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REPORT_TEXT, selfReport.getReportText());
        //values.put(KEY_TIMESTAMP, selfReport.getTimestamp());

        Log.d("UPDATING SELF REPORT", String.valueOf(selfReport.getId()));

        Log.d("UPDATING SELF REPORT", selfReport.getReportText());
        // updating row
        return db.update(TABLE_SELF_REPORTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(selfReport.getId()) });
    }

    public int deleteSelfReport(SelfReportModel selfReport){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("Delete Self report", String.valueOf(selfReport.getId()));
        return db.delete(TABLE_SELF_REPORTS, //table name
                KEY_ID + " = ?",  // selections
                new String[]{String.valueOf(selfReport.getId())}); //selections args
    }

    public List<SelfReportModel> getAllSelfReports() {
        List<SelfReportModel> selfReports = new LinkedList<SelfReportModel>();
        String query = "SELECT  * FROM " + TABLE_SELF_REPORTS + " ORDER BY timestamp DESC limit 500";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        SelfReportModel selfReport = null;

        if (c.moveToFirst()) {
            do {
                selfReport = new SelfReportModel();
                selfReport.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                selfReport.setUserId(c.getString(c.getColumnIndex(KEY_USER_ID)));
                selfReport.setUniqueUserId(c.getString(c.getColumnIndex(KEY_UNIQUE_USER_ID)));
                selfReport.setReportText(c.getString(c.getColumnIndex(KEY_REPORT_TEXT)));
                selfReport.setTimeStamp(c.getString(c.getColumnIndex(KEY_TIMESTAMP)));
                selfReports.add(selfReport);
            } while (c.moveToNext());
        }
        return selfReports;
    }

    public void insertNewSyncedNums(int num1, int num2, int num3, int num4, int num5, int num6, int num7) {

        //Open connection to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NUM_1,num1);
        values.put(KEY_NUM_2,num2);
        values.put(KEY_NUM_3,num3);
        values.put(KEY_NUM_4,num4);
        values.put(KEY_NUM_5,num5);
        values.put(KEY_NUM_6,num6);
        values.put(KEY_NUM_7,num7);


        // Inserting Row
        db.insert(TABLE_SYNCED, null, values);
        db.close(); // Closing database connection

    }

    public ArrayList<Integer> getSyncedNums() {

        ArrayList<Integer> numbers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String count = "SELECT count(*) FROM " + TABLE_SYNCED;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0)
        {
            String query = "SELECT * from " + TABLE_SYNCED + " order by " + KEY_NUM_1 + " DESC limit 1";
            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst())
            {

                numbers.add(c.getInt(c.getColumnIndex(KEY_NUM_1)));
                numbers.add(c.getInt(c.getColumnIndex(KEY_NUM_2)));
                numbers.add(c.getInt(c.getColumnIndex(KEY_NUM_3)));
                numbers.add(c.getInt(c.getColumnIndex(KEY_NUM_4)));
                numbers.add(c.getInt(c.getColumnIndex(KEY_NUM_5)));
                numbers.add(c.getInt(c.getColumnIndex(KEY_NUM_6)));
                numbers.add(c.getInt(c.getColumnIndex(KEY_NUM_7)));
            }
        }
        else{
            numbers.add(0);
            numbers.add(0);
            numbers.add(0);
            numbers.add(0);
            numbers.add(0);
            numbers.add(0);
            numbers.add(0);
        }

        mcursor.close();
        db.close();
        return numbers;
    }

}
