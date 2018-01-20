package com.example.android.dybacompanion.service;
import com.example.android.dybacompanion.Call;
/**
 * Created by Laura on 13/09/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.example.android.dybacompanion.DyBaRepo;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;

public class SAPServiceProvider extends SAAgent{
    public SAPServiceProvider() {
        super(TAG, SAPServiceProviderConnection.class);
    }

    public final static String TAG ="SAPServiceProvider";
    public final static int SAP_SERVICE_CHANNEL_ID = 110;
    public DyBaRepo repo_inst = new DyBaRepo(this);
    private SAPServiceProviderConnection myConnection;

    private final IBinder mIBinder = new LocalBinder();

    HashMap<Integer,SAPServiceProviderConnection> connectionMap = null;

    private void sendMessageToActivity(String msg) {
        Intent intent = new Intent("SAPmessage");
        intent.putExtra("Status", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("beats");
            sendBeats(message);
            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            //startCall();

        }
    };

    @Override
    protected void
    onFindPeerAgentResponse(SAPeerAgent peerAgent, int result)
    {
        if (result == PEER_AGENT_FOUND)
        {
	/* Peer Agent is found */
        }
        else if (result == FINDPEER_DEVICE_NOT_CONNECTED)
        {
	/* Peer Agents are not found, no accessory device connected */
        }
        else if(result == FINDPEER_SERVICE_NOT_FOUND )
        {
	/* No matching service on connected accessory */
        }
    }

    @Override
    protected void onServiceConnectionResponse(SASocket thisConnection, int result){
        if (result == SAAgent.CONNECTION_SUCCESS)
        {
            if (thisConnection != null)
            {
                myConnection = (SAPServiceProviderConnection) thisConnection;

                if (connectionMap == null){
                    connectionMap = new HashMap<Integer, SAPServiceProviderConnection>();
                }

                myConnection.connectionID = (int) (System.currentTimeMillis() & 255);

                connectionMap.put(myConnection.connectionID, myConnection);

                Toast.makeText(getBaseContext(), "CONNECTION ESTABLISHED", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Gear connection is successful.");

                sendBeats(repo_inst.getAHRbeatsValue());
            } else {
                Log.e(TAG, "SASocket object is null");
            }
        }
        else if (result == SAAgent.CONNECTION_ALREADY_EXIST)
        {
            Log.e(TAG, "Gear connection is already exist.");
        }
    }

    public void
    sendBeats(final String message)
    {
        if (myConnection != null)
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        myConnection.send(SAP_SERVICE_CHANNEL_ID, message.getBytes());
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SERVICE IS DOING SHITTT","TTTTTTT");
        SA mAccessory = new SA();
        try{
            mAccessory.initialize(this);
            Log.d("SAP PROVIDER", "ON CREATE TRY BLOCK");
        } catch (SsdkUnsupportedException e){
            Log.d("SAP PROVIDER", "ON REATE TRY BLOCK ERROR UNSUPPORTED SDK");
        } catch (Exception e1){
            Log.e(TAG,"Cannot initialize accessory package");
            e1.printStackTrace();
            stopSelf();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("beats"));
    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent saPeerAgent) {
        acceptServiceConnectionRequest(saPeerAgent);
    }

    public String getDeviceInfo(){
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return manufacturer + " " + model;
    }

    public class LocalBinder extends Binder{
        public SAPServiceProvider getService(){
            return SAPServiceProvider.this;
        }
    }

    public class SAPServiceProviderConnection extends SASocket{

        private int connectionID;


        protected SAPServiceProviderConnection() {
            super(SAPServiceProviderConnection.class.getName());
        }

        @Override
        public void onError(int channelID, String errorString, int error) {
            Log.e(TAG,"ERROR: "+errorString+ " | "+ error);
        }



        @Override
        public void onReceive(int channelID, byte[] data) {
            final String message = new String(data);

            /*
            Time time = new Time();

            time.set(System.currentTimeMillis());

            String timeStr = " " + String.valueOf(time.minute) + ":"
                    + String.valueOf(time.second);

            String strToUpdateUI = new String(data);

            */
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());


            if(message.equals("y")|| message.equals("n"))
            {
                repo_inst.insertPrompt(timeStamp, message, "none");
                Log.d("DB insert prompt:",message);
            }
            else if(message.substring(0,3).equals("AHR"))
            {
                String[] split = message.substring(3).split(":");
                repo_inst.insertAHR(timeStamp, split[0], split[1]);
                Log.d("DB insert ahr:",message);
            }
            else
            {
                String[] split = message.split(":");

                repo_inst.insertActivityHR(timeStamp, split[0], split[1]);
                Log.d("DB insert activity hr:",message);
            }




            //message = getDeviceInfo() + strToUpdateUI.concat(timeStr);
            //message = String.valueOf(data);
            Log.d("SAP MESSAGE",message);
            Log.d("SAP TS",timeStamp);
            //sendMessageToActivity(strToUpdateUI);



        }


        public void sendMessage(final String message){
            final SAPServiceProviderConnection uHandler = connectionMap.get(Integer
                    .parseInt(String.valueOf(connectionID)));
            if(uHandler==null){
                Log.e(TAG,"Error, can not get SAPSERviceProviderConnection handler");
                return;
            }
            new Thread(new Runnable(){
                public void run(){
                    try {
                        uHandler.send(SAP_SERVICE_CHANNEL_ID, message.getBytes());
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        @Override
        protected void onServiceConnectionLost(int i) {
            if(connectionMap != null){
                connectionMap.remove(connectionID);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }


}
