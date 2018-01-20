package com.example.android.dybacompanion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 1/11/2018.
 */

public class DyBaRepo {
    private DBHelper dbHelper;

    public DyBaRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insertActivityHR(String timestamp, String avgHR, String activity) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DyBaDB.KEY_timestamp_1,timestamp);
        values.put(DyBaDB.KEY_avgHR,avgHR);
        values.put(DyBaDB.KEY_activity, activity);

        // Inserting Row
        db.insert(DyBaDB.TABLE1, null, values);
        db.close(); // Closing database connection

    }

    public void insertAHR(String timestamp, String AHR, String AHRbeats) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DyBaDB.KEY_timestamp_2,timestamp);
        values.put(DyBaDB.KEY_AHR,AHR);
        values.put(DyBaDB.KEY_AHRbeats,AHRbeats);

        // Inserting Row
        db.insert(DyBaDB.TABLE2, null, values);
        db.close(); // Closing database connection

    }

    public void insertPrompt(String timestamp, String prompt_ans, String prompt_context) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DyBaDB.KEY_timestamp_3,timestamp);
        values.put(DyBaDB.KEY_prompt_ans,prompt_ans);
        values.put(DyBaDB.KEY_prompt_context,prompt_context);

        // Inserting Row
        db.insert(DyBaDB.TABLE3, null, values);
        db.close(); // Closing database connection

    }

    public void insertReport(String timestamp, String selfreport, String selfreport_context) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DyBaDB.KEY_timestamp_4,timestamp);
        values.put(DyBaDB.KEY_selfreport,selfreport);
        values.put(DyBaDB.KEY_selfreport_context,selfreport_context);

        // Inserting Row
        db.insert(DyBaDB.TABLE4, null, values);
        db.close(); // Closing database connection

    }

    public ArrayList<HashMap<String, String>> getPromptMomentsList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                DyBaDB.KEY_ID_3 + "," +
                DyBaDB.KEY_timestamp_3 + "," +
                DyBaDB.KEY_prompt_ans  + "," +
                DyBaDB.KEY_prompt_context  +
                " FROM " + DyBaDB.TABLE3;

        ArrayList<HashMap<String, String>> PromptMomentsList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> PromptMoments = new HashMap<String, String>();
                PromptMoments.put("id3", cursor.getString(cursor.getColumnIndex(DyBaDB.KEY_ID_3)));
                PromptMoments.put("timestamp3", cursor.getString(cursor.getColumnIndex(DyBaDB.KEY_timestamp_3)));
                PromptMoments.put("promptans", cursor.getString(cursor.getColumnIndex(DyBaDB.KEY_prompt_ans)));
                PromptMoments.put("promptcontext", cursor.getString(cursor.getColumnIndex(DyBaDB.KEY_prompt_context)));
                PromptMomentsList.add(PromptMoments);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return PromptMomentsList;

    }

    public void updatePrompt(int id, String timestamp, String prompt_ans, String prompt_context) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DyBaDB.KEY_timestamp_3, timestamp);
        values.put(DyBaDB.KEY_prompt_ans, prompt_ans);
        values.put(DyBaDB.KEY_prompt_context, prompt_context);

        db.update(DyBaDB.TABLE3, values, DyBaDB.KEY_ID_3 + "= ?", new String[] { String.valueOf(id) });
        db.close(); // Closing database connection

    }

    public String getAHRbeatsValue() {

        String value = "3";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String count = "SELECT count(*) FROM " + DyBaDB.TABLE2;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0)
        {
            String query = "SELECT * from " + DyBaDB.TABLE2 + " order by " + DyBaDB.KEY_ID_2 + " DESC limit 1";
            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst())
            {
                value = c.getString(c.getColumnIndex(DyBaDB.KEY_AHRbeats));
            }
        }

        mcursor.close();
        db.close();
        return value;
    }
}
