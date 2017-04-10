package org.taoconnect.logs.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.taoconnect.logs.tools.R;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;


// TODO: populate modules from database
// TODO: populate logs from database
// TODO: add accesibility labels

public class LogPicker extends AppCompatActivity {

    private Button startbutton;
    private SharedPreferences mSharedPreferences;
    private final String DBName = "tempLogs.db";
    private SQLiteDatabase tempDB;
    private boolean hasContinue;
    private MySQLiteHelper mHelper;
    private Context context = this;
    private String actionSelection, logSelection, moduleSelection;
    private Spinner logs, modules, action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_picker);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        hasContinue = mSharedPreferences.getBoolean(getString(R.string.saved_log), false);
        mHelper = new MySQLiteHelper(getApplicationContext());

        logs = (Spinner) findViewById(R.id.logPicker);

        modules = (Spinner) findViewById(R.id.modulePicker);
        ArrayAdapter<CharSequence> modulesAdapter = ArrayAdapter.createFromResource(this, R.array.modules, android.R.layout.simple_spinner_item);
        modulesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modules.setAdapter(modulesAdapter);
        modules.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<CharSequence> logsAdapter = null;
                switch(parent.getSelectedItem().toString()){
                    case "Anxiety Logs":
                        logsAdapter = ArrayAdapter.createFromResource(context, R.array.anxietyLogs, android.R.layout.simple_spinner_item);
                        break;
                    case "Behavioral Activation":
                        logsAdapter = ArrayAdapter.createFromResource(context, R.array.behavioralLogs, android.R.layout.simple_spinner_item);
                        break;
                    case "Acceptance and Commitment Therapy":
                        logsAdapter = ArrayAdapter.createFromResource(context, R.array.acceptanceLogs, android.R.layout.simple_spinner_item);
                        break;
                    case "Cognitive Behavioral":
                        logsAdapter = ArrayAdapter.createFromResource(context, R.array.cognitiveLogs, android.R.layout.simple_spinner_item);
                        break;
                }
                logsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                logs.setAdapter(logsAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        action = (Spinner) findViewById(R.id.actionOptions);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        action.setAdapter(spinnerAdapter);
        spinnerAdapter.add("New");
        spinnerAdapter.add("Review");
        if (hasContinue) spinnerAdapter.add("Continue");

        }

    public void startLog(View v){
        logSelection = logs.getSelectedItem().toString();
        actionSelection = action.getSelectedItem().toString();
        parseSelection(logSelection, actionSelection);
     

    }

    private void readDatabase(){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor firstSet = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_RELAX_LOG, null);
        Cursor secondSet = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_ANX_MON_LOG, null);
        Cursor thirdSet = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_EXPOSURE_LOG, null);
        Cursor fourthSet = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_CHALLENGE_LOG, null);
        List<Cursor> myCursors = new ArrayList<Cursor>();
        myCursors.add(firstSet);
        myCursors.add(secondSet);
        myCursors.add(thirdSet);
        myCursors.add(fourthSet);

       for(Cursor section : myCursors) {
                String[] columns = section.getColumnNames();
                    for (String name: columns){
                        Log.e("Row", name);
                    }

        }
        db.close();
    }

    private void parseSelection(String logSelected, String actionSelected) {
        switch(actionSelected){
            case "New":
                Intent start = new Intent(this, Questionary.class);
                start.putExtra("Log", logSelected);
                start.putExtra("Count", getQuestionsInLog(logSelected));
                startActivity(start);
                break;
            case "Review":  //TODO: send intent to review activity
                break;
            case "Continue":  //TODO: load local copy and continue
        }
    }

    // Count is one over to include the submit screen in Quesitonary.java
    private int getQuestionsInLog(String logSelected){
        switch(logSelected){
            case "Anxiety Monitoring Log": return 9;
            case "Relaxation Log": return 4;
            case "Challenge Log": return 13;
            case "Exposure Log": return 8;
            case "Feeling Log":
            case "Activation Plan":
            case "Rumination Record":
            case "Letting Go Exercise Log":
            case "Mindfulness Log":
            case "Anxiety Logs":
            case "Behavioral Activation":
            case "Acceptance and Commitment Therapy":
            case "Cognitive Behavioral":
                break;
        }
        return 0;
    }
}