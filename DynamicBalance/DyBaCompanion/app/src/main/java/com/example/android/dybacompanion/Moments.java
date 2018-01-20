package com.example.android.dybacompanion;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by User on 1/19/2018.
 */

public class Moments extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<String> item_list;
    private ArrayList<HashMap<String, String>> db_moments;
    private DyBaRepo repoinst = new DyBaRepo(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments);

        mListView = (ListView) findViewById(R.id.moments_list_view);

        db_moments = repoinst.getPromptMomentsList();
        item_list = new ArrayList<String>();
        for (int i=0; i < db_moments.size(); i++) {
            //Log.v("dbitemID", todo_items.get(i).get("id3"));
            item_list.add(db_moments.get(i).get("id3")+". "+db_moments.get(i).get("timestamp3")+": "+db_moments.get(i).get("promptans"));
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, item_list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, final int position,
                                    long arg3)
            {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Moments.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.editprompt_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Moments.this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText context = (EditText)mView.findViewById(R.id.editcontext);

                context.setText(db_moments.get(position).get("promptcontext"));

                alertDialogBuilderUserInput
                        .setTitle(String.valueOf(adapter.getItemAtPosition(position)))
                        .setCancelable(false)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                repoinst.updatePrompt(position+1, db_moments.get(position).get("timestamp3"), db_moments.get(position).get("promptans"), db_moments.get(position).get("promptcontext"));
                                db_moments = repoinst.getPromptMomentsList();

                                //Toast.makeText(NavigationUI.this, "Stress moment recorded.", Toast.LENGTH_SHORT).show();
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
