package org.taoconnect.logs.controllers;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.tools.R;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.Thread.sleep;

public class Checkremote extends IntentService {

    private SharedPreferences mSharedPreferences;

    public Checkremote() {
        super("Checkremote");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("Event", "Started the service");
        while(!isNetworkConnected()){
            Log.e("Event", "Network NOT connected");
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e("Event", "Network connected");
        mSharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        String JSONString = mSharedPreferences.getString(getString(R.string.jsonobject), "0");
        try {
            JSONObject mJSON = new JSONObject(JSONString);
            Iterator<String> itr = mJSON.keys();
            ArrayList<String> names = new ArrayList<String>();
            ArrayList<Long> rowIds = new ArrayList<Long>();
            while (itr.hasNext()){
                String key = itr.next();
                names.add(key);
                rowIds.add(mJSON.getLong(key));
            }
            pushToRemoteDB(names, rowIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // The keys are the permanent local table names and the values are the last rowId that was saved
    private void pushToRemoteDB(ArrayList<String> keys, ArrayList<Long> values){
        MySQLiteHelper mHelper = new MySQLiteHelper(this);
        SQLiteDatabase db = mHelper.getReadableDatabase();

        int i = 0;
        for(String tablename : keys){
            Cursor cursor = db.rawQuery("SELECT * FROM " + tablename + " WHERE " + InitialSchema._ID + " > " + values.get(i++), null);
            while(cursor.moveToNext()){
                //TODO: Push to DB
                Log.e("Event", "Actually did something");
            }
            cursor.close();
        }
        db.close();
    }
    // Returns true if connected, false otherwise
    // This one checks that the device is connected to some network
    private boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null)  && isInternetAvailable();
    }

    // This one checks that there is an actual internet connection
    private boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e("Event", "Exit the service");
    }
}
