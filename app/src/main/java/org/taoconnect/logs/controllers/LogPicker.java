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

import org.taoconnect.logs.models.LogAnxietyMonitoring;
import org.taoconnect.logs.models.LogChallenge;
import org.taoconnect.logs.models.LogExposure;
import org.taoconnect.logs.models.LogRelaxation;
import org.taoconnect.logs.tools.R;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

// TODO: add accesibility labels
/** Class: LogPicker
 *  This class depends on the log models (e.g LogAnxietyMonitoring, LogChallenge...) and the local databases.
 *  It displays three spinners: one for the module, one for the logs, one for the action
 *  After a module gets selected, the right logs for that module get loaded
 *  If the log has a temporal table with more than one row then the continue option will be displayed in the action
 *  spinner.
 *  Once all spinners have been selected it sends an intent to questionary with the amount of questions in the log,
 *  the selected log and the selected action for that log.
 *  Actions can be review, continue and new.
 */
public class LogPicker extends AppCompatActivity {
    private Context context = this;
    private String actionSelection, logSelection;
    private Spinner logs, modules, action;
    private LogAnxietyMonitoring logAnxiety;
    private LogChallenge logChallenge;
    private LogExposure logExposure;
    private LogRelaxation logRelaxation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_picker);

        loadSpinners();

    }

    private void loadSpinners(){
        logs = (Spinner) findViewById(R.id.logPicker);
        modules = (Spinner) findViewById(R.id.modulePicker);
        action = (Spinner) findViewById(R.id.actionOptions);

        // Every time a module gets clicked, load the appropiate logs corresponding to that log
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

        final ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        action.setAdapter(actionAdapter);
        actionAdapter.add("New");
        actionAdapter.add("Review");

        // Every time a log is clicked, check to see if it has a temp table associated with the log object
        logs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean hasContinue = false;
                switch (parent.getSelectedItem().toString()){
                    case "Anxiety Monitoring Log": logAnxiety = new LogAnxietyMonitoring(context);
                        hasContinue = logAnxiety.hasTempTable();
                        break;
                    case "Relaxation Log":
                        logRelaxation = new LogRelaxation(context);
                        hasContinue = logRelaxation.hasTempTable();
                        break;
                    case "Challenge Log": logChallenge = new LogChallenge(context);
                        hasContinue = logChallenge.hasTempTable();
                        break;
                    case "Exposure Log": logExposure = new LogExposure(context);
                        hasContinue = logExposure.hasTempTable();
                        break;
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
                if(hasContinue){
                    Log.e("Event", "Continue added");
                    actionAdapter.add("Continue");
                }
                else{
                    actionAdapter.remove("Continue");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Auto generated stub
            }
        });
    }

    // Start button listener
    public void startLog(View v){
        logSelection = logs.getSelectedItem().toString();
        actionSelection = action.getSelectedItem().toString();
        parseSelection(logSelection, actionSelection);
    }

    // Send appropiate intent message over to Questionary
    private void parseSelection(String logSelected, String actionSelected) {
        switch(actionSelected){
            case "New":
                Intent start = new Intent(this, Questionary.class);
                start.putExtra("Log", logSelected);
                start.putExtra("Count", getQuestionsInLog(logSelected));
                start.putExtra("Action", "New");
                startActivity(start);
                break;
            case "Review":  //TODO: send intent to review activity
                break;
            case "Continue":
                Intent cont = new Intent(this, Questionary.class);
                cont.putExtra("Log", logSelected);
                cont.putExtra("Count", getQuestionsInLog(logSelected));
                cont.putExtra("Action", "Continue");
                startActivity(cont);
                break;
        }
    }

    // Count is one over to include the submit screen in Questionary.java
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