package com.example.android.dybacompanion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 1/11/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "dyba.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //All necessary tables you'd like to create will be created here
        String CREATE_TABLE_ACTIVITYHR = "CREATE TABLE " + DyBaDB.TABLE1  + "("
                + DyBaDB.KEY_ID_1  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DyBaDB.KEY_timestamp_1 + " TEXT, "
                + DyBaDB.KEY_avgHR + " TEXT, "
                + DyBaDB.KEY_activity + " TEXT); ";

        db.execSQL(CREATE_TABLE_ACTIVITYHR);

        String CREATE_TABLE_AHR = "CREATE TABLE " + DyBaDB.TABLE2  + "("
                + DyBaDB.KEY_ID_2  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DyBaDB.KEY_timestamp_2 + " TEXT, "
                + DyBaDB.KEY_AHR + " TEXT, "
                + DyBaDB.KEY_AHRbeats + " TEXT); ";

        db.execSQL(CREATE_TABLE_AHR);

        String CREATE_TABLE_PROMPT = "CREATE TABLE " + DyBaDB.TABLE3  + "("
                + DyBaDB.KEY_ID_3  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DyBaDB.KEY_timestamp_3 + " TEXT, "
                + DyBaDB.KEY_prompt_ans + " TEXT, "
                + DyBaDB.KEY_prompt_context + " TEXT); ";

        db.execSQL(CREATE_TABLE_PROMPT);

        String CREATE_TABLE_REPORT = "CREATE TABLE " + DyBaDB.TABLE4  + "("
                + DyBaDB.KEY_ID_4  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DyBaDB.KEY_timestamp_4 + " TEXT, "
                + DyBaDB.KEY_selfreport + " TEXT, "
                + DyBaDB.KEY_selfreport_context + " TEXT); ";

        db.execSQL(CREATE_TABLE_REPORT);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if it exists, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + DyBaDB.TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + DyBaDB.TABLE2);
        db.execSQL("DROP TABLE IF EXISTS " + DyBaDB.TABLE3);
        db.execSQL("DROP TABLE IF EXISTS " + DyBaDB.TABLE4);

        // Create tables again
        onCreate(db);

    }
}
