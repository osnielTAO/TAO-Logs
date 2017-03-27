package com.logs.tao.taologs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


// TODO: populate modules from database
// TODO: populate logs from database
// TODO: add accesibility labels

public class LogPicker extends AppCompatActivity{

    private Button startbutton;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_picker);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasContinue = mSharedPreferences.getBoolean(getString(R.string.saved_log), false);

        final Spinner logs = (Spinner) findViewById(R.id.logPicker);
        ArrayAdapter<CharSequence> logsAdapter = ArrayAdapter.createFromResource(this, R.array.logs, android.R.layout.simple_spinner_item);
        logsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logs.setAdapter(logsAdapter);

        final Spinner modules = (Spinner) findViewById(R.id.modulePicker);
        ArrayAdapter<CharSequence> modulesAdapter = ArrayAdapter.createFromResource(this, R.array.modules, android.R.layout.simple_spinner_item);
        modulesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modules.setAdapter(modulesAdapter);

        final Spinner action = (Spinner) findViewById(R.id.actionOptions);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        action.setAdapter(spinnerAdapter);
        spinnerAdapter.add("New");
        spinnerAdapter.add("Review");
        if (hasContinue) spinnerAdapter.add("Continue");
        spinnerAdapter.notifyDataSetChanged();

        startbutton = (Button) findViewById(R.id.startButton);
        startbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Load questions depending on selected log
                String moduleSelection = (String) modules.getSelectedItem();
                String logSelection = (String) logs.getSelectedItem();
                String actionSelection = (String) action.getSelectedItem();
                parseSelection(moduleSelection, logSelection, actionSelection);
            }
        });
    }


    private void parseSelection(String moduleSelected, String logSelected, String actionSelected) {
        switch(actionSelected){
            case "New":
                Intent start = new Intent(this, Questionary.class);
                start.putExtra("Header", logSelected);
                start.putExtra("Count", 5);
                startActivity(start);
                break;
            case "Review":  //TODO: send intent to review activity
                break;
            case "Continue":  //TODO: load local copy and continue
        }
    }
}