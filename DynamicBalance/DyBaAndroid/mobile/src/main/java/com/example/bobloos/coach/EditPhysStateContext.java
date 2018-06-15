package com.example.bobloos.coach;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.SelfReportModel;
import com.example.bobloos.model.UserModel;

import java.util.HashMap;
import java.util.List;

public class EditPhysStateContext extends AppCompatActivity {
    Toolbar toolbar;
    FloatingActionButton fab_add;
    FloatingActionButton fab_delete;
    EditText context;
    String text;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Spinner contextDescriptionSpinner;
    UserModel user;
    HashMap<String, String> single_feedback;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(this);
        List<String> context_list = db.getContextData();
        Log.d("CONTEXT SIZE",String.valueOf(context_list.size()));
        for(int i=0;i<context_list.size();i++)
        {
            Log.d("CONTEXT LIST",context_list.get(i));
        }

        setContentView(R.layout.activity_edit_phys_state_context);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = (EditText)  findViewById(R.id.add_context3);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        contextDescriptionSpinner = (Spinner) findViewById(R.id.context_feedback);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, context_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contextDescriptionSpinner.setAdapter(adapter);

        fab_add = (FloatingActionButton) findViewById(R.id.add_story_button);
        fab_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveContextDescription();
            }
        });

        fab_delete = (FloatingActionButton) findViewById(R.id.delete_story_button);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteContextDescription();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Context toevoegen");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String phys_state_ts = intent.getStringExtra("PhysStateTs");


        setUser();
        setFeedback(phys_state_ts);
        //setPhysState(phys_state_id);
    }

    private void setUser(){
        user = db.getUser(1);
        if(user == null){
            db.addUser(new UserModel());
        }
    }


    private void setFeedback(String ts){
        single_feedback = db.getSingleFeedback(ts);

        String y = single_feedback.get("y");
        switch (y){
            case "1":
                radioGroup.check(R.id.radioButtonY);
                break;
            case "0":
                radioGroup.check(R.id.radioButtonN);
                break;
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

    public void saveContextDescription(){
        //String text = contextDescriptionTextView.getText().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        int y = radioButton.getText().toString().equals("Accurate") ? 1:0;

        if(context.getText().toString().equals(""))
        {
            text = contextDescriptionSpinner.getSelectedItem().toString();
        } else
        {
            text = context.getText().toString();
            db.addContextData(text);
        }

        db.updateFeedback(single_feedback.get("timestamp"), String.valueOf(y), text);

        Intent intent = new Intent(EditPhysStateContext.this, MainActivity.class);
        intent.putExtra("pageId", "0");
        EditPhysStateContext.this.startActivity(intent);
    }

    public void deleteContextDescription(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder
                .setMessage("Weet je zeker dat je jouw toelichting wilt verwijderen? Let op: de meting blijft bestaan!")
                .setPositiveButton("Ja",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        db.updateFeedback(single_feedback.get("timestamp"), "1", null);
                        Intent intent = new Intent(EditPhysStateContext.this, MainActivity.class);
                        intent.putExtra("pageId", "0");
                        EditPhysStateContext.this.startActivity(intent);
                    }
                })
                .setNegativeButton("Nee", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        Log.d("LOG", "GETTING DELETE NO");

                        dialog.cancel();
                    }
                })
                .show();
    }
}