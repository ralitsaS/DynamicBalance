package com.example.bobloos.coach;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.SelfReportModel;
import com.example.bobloos.model.UserModel;

import java.util.List;

public class NewSelfReport extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton fab;
    EditText context;
    Spinner contextSpinner;
    UserModel user;
    DatabaseHandler db;
    String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(this);
        List<String> context_list = db.getContextData();
        setContentView(R.layout.activity_new_self_report);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        contextSpinner = (Spinner) findViewById(R.id.spinner2);
        context = (EditText)  findViewById(R.id.add_context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, context_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contextSpinner.setAdapter(adapter);

        //storyTextView = (EditText) findViewById(R.id.self_report_edit_text);
        fab = (FloatingActionButton) findViewById(R.id.add_story_button);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveSelfReport();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nieuwe zelfrapportage");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        setUser();
    }

    private void setUser(){
        user = db.getUser(1);
        if(user == null){
            db.addUser(new UserModel());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveSelfReport(){
        if(context.getText().toString().equals(""))
        {
            text = contextSpinner.getSelectedItem().toString();
        } else
        {
            text = context.getText().toString();
            db.addContextData(text);
        }

        long timeInMs = System.currentTimeMillis();

        SelfReportModel selfReport = new SelfReportModel();
        selfReport.setUserId(String.valueOf(user.getId()));
        selfReport.setUniqueUserId(String.valueOf(user.getUniqueUserId()));
        selfReport.setReportText(text);
        selfReport.setTimeStamp(String.valueOf(timeInMs));
        db.addSelfReport(selfReport);

        db.addFeedbackData(String.valueOf(user.getUniqueUserId()), String.valueOf(timeInMs), null, null, text);

        Intent intent = new Intent(NewSelfReport.this, MainActivity.class);
        intent.putExtra("pageId", "1");
        NewSelfReport.this.startActivity(intent);
        NewSelfReport.this.finish();
    }
}
