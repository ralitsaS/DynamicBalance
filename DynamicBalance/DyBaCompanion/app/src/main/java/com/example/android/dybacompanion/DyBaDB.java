package com.example.android.dybacompanion;

/**
 * Created by User on 1/11/2018.
 */

public class DyBaDB {
    //tables
    public static final String TABLE1 = "activityHR";
    public static final String TABLE2 = "AHR";
    public static final String TABLE3 = "prompt";
    public static final String TABLE4 = "selfreport";

    //columns
    public static final String KEY_ID_1 = "id1";
    public static final String KEY_timestamp_1 = "timestamp1";
    public static final String KEY_avgHR = "avgHR";
    public static final String KEY_activity = "activity";

    public static final String KEY_ID_2 = "id2";
    public static final String KEY_timestamp_2 = "timestamp2";
    public static final String KEY_AHR = "AHR";
    public static final String KEY_AHRbeats = "AHRbeats";

    public static final String KEY_ID_3 = "id3";
    public static final String KEY_timestamp_3 = "timestamp3";
    public static final String KEY_prompt_ans = "prompt_ans";
    public static final String KEY_prompt_context = "prompt_context";

    public static final String KEY_ID_4 = "id4";
    public static final String KEY_timestamp_4 = "timestamp4";
    public static final String KEY_selfreport = "selfreport";
    public static final String KEY_selfreport_context = "selfreport_context";

    //variable types
    public int id1;
    public String timestamp1;
    public String avgHR;
    public String activity;

    public int id2;
    public String timestamp2;
    public String AHR;
    public String AHRbeats;

    public int id3;
    public String timestamp3;
    public String prompt_ans;
    public String prompt_context;

    public int id4;
    public String timestamp4;
    public String selfreport;
    public String selfreport_context;


}
