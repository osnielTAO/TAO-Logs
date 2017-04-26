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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.tools.ExtHttpClientStack;
import org.taoconnect.logs.tools.R;
import org.taoconnect.logs.tools.SslHttpClient;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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
                sleep(5000); //TODO: change this to one hour
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
            ArrayList<Integer> rowIds = new ArrayList<Integer>();
            while (itr.hasNext()){
                String key = itr.next();
                names.add(key);
                rowIds.add(mJSON.getInt(key));
            }
            pushToRemoteDB(names, rowIds, mJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // The keys are the permanent local table names and the values are the last rowId that was saved
    private void pushToRemoteDB(ArrayList<String> keys, ArrayList<Integer> values, JSONObject obj) throws JSONException {
        MySQLiteHelper mHelper = new MySQLiteHelper(this);
        SQLiteDatabase db = mHelper.getReadableDatabase();

        int i = 0;
        for(final String tablename : keys){
            Log.e("Event", "This is the rowId " + values.get(i));
            Cursor row = db.rawQuery("SELECT * FROM " + tablename + " WHERE " + InitialSchema._ID + " > " + values.get(i++), null);
            while(row.moveToNext()){  // Row is initially at -1
                String[] columns = row.getColumnNames();
                // Update the row id in the shared preferences
                int it = Integer.parseInt(row.getString(row.getColumnIndex(columns[0])));
                obj.put(tablename, it);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(getResources().getString(R.string.jsonobject), obj.toString());
                editor.apply();

                final ArrayList<String> valuesToPost = new ArrayList<String>();
                for(int j = 1; j< columns.length; j++){ // Don't grab the id
                    valuesToPost.add(row.getString(row.getColumnIndex(columns[j])));
                }

                InputStream keyStore = getResources().openRawResource(R.raw.mykeystore);
                RequestQueue queue = Volley.newRequestQueue(this, new ExtHttpClientStack(new SslHttpClient(keyStore,"123456")));
                String url = "https://172.23.50.150/insert";
                StringRequest mRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        Log.e("Event ", "Received: " + response);
                    }

                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(getApplicationContext(),"That didn't work service", Toast.LENGTH_SHORT).show();
                        String message = null;
                        if (error instanceof NetworkError) {
                            message = "Network error";
                        } else if (error instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!!";
                        } else if (error instanceof AuthFailureError) {
                            message = "auth fail";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (error instanceof NoConnectionError) {
                            message = "NoConnection";
                        } else if (error instanceof TimeoutError) {
                            message = "Connection TimeOut! Please check your internet connection.";
                        }
                        Log.e("Error", message);


                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        LinkedHashMap<String,String> params = new LinkedHashMap<String,String>();

                        params.put("tablename", tablename);
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("user", "osniel");
                            int n = 0;
                            for(String value : valuesToPost){
                                obj.put(String.valueOf(n), value);
                                n++;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params.put("values", obj.toString());
                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError{
                        Map<String, String> params = new HashMap<String,String>();
                        params.put("row", "this is a header");
                        return params;
                    }
                };
                queue.add(mRequest);
            }
            row.close();
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
