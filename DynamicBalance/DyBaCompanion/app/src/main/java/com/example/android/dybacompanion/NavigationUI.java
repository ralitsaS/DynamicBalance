package com.example.android.dybacompanion;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by User on 1/11/2018.
 */

public class NavigationUI extends AppCompatActivity {

    public DyBaRepo repoinst = new DyBaRepo(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        final Button enter_user_stress = (Button) findViewById(R.id.button_opendialogbox);
        enter_user_stress.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(NavigationUI.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.selfreport_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(NavigationUI.this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText hours = (EditText)mView.findViewById(R.id.hours);
                final EditText minutes = (EditText)mView.findViewById(R.id.minutes);
                final EditText context = (EditText)mView.findViewById(R.id.context);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                                repoinst.insertReport(timeStamp, hours+" hours,"+minutes+" mins ago", context.getText().toString());
                                Toast.makeText(NavigationUI.this, "Stress moment recorded.", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });


        final Button detected_moments = (Button) findViewById(R.id.button_moments);
        detected_moments.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),Moments.class);
                startActivity(i);
            }
        });

        final Button adjust_ahr = (Button) findViewById(R.id.button_ahr);
        adjust_ahr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(NavigationUI.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.ahr_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(NavigationUI.this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText beats = (EditText)mView.findViewById(R.id.beats);
                beats.setText(repoinst.getAHRbeatsValue());


                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                                Intent intent = new Intent("beats");
                                intent.putExtra("beats", beats.getText());
                                LocalBroadcastManager.getInstance(NavigationUI.this).sendBroadcast(intent);

                                Toast.makeText(NavigationUI.this, "AHR condition changed.", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });
    }
}
