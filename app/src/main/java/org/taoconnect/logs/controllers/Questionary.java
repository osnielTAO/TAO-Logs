package org.taoconnect.logs.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import org.taoconnect.logs.models.LogAnxietyMonitoring;
import org.taoconnect.logs.models.LogChallenge;
import org.taoconnect.logs.models.LogExposure;
import org.taoconnect.logs.models.LogRelaxation;
import org.taoconnect.logs.tools.R;

import java.util.ArrayList;
import java.util.List;



public class Questionary extends AppCompatActivity {

    private static int count;  //Number of questions
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String log;
    private LogAnxietyMonitoring logAnxiety;
    private LogChallenge logChallenge;
    private LogExposure logExposure;
    private LogRelaxation logRelaxation;
    private static int[] layouts;
    private static String[] headers;
    private static List<View> views = new ArrayList<View>();
    private static int itr = 0;
    private static int prevPos=0;
    private static boolean movingLeft = false;

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // onCreateView is called once for each instance returned by this
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position+1);
        }

        public int getItemPosition(Object object){
            return POSITION_NONE;
        }
        // Total number of questions to be displayed
        @Override
        public int getCount() {
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position);
        }

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView;
            int section = getArguments().getInt(ARG_SECTION_NUMBER);
            if(section == count){
                rootView = inflater.inflate(R.layout.submit_questionary, container, false);
            }
            else {
                rootView = inflater.inflate(layouts[section-1], container, false);
                if(layouts[section-1] == R.layout.single_choice_questionary){
                    ListView list = (ListView) rootView.findViewById(R.id.singlechoices);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(),android.R.layout.simple_list_item_single_choice,
                            getResources().getStringArray(R.array.singleChoiceRelaxationLog));
                    list.setAdapter(adapter);
                    list.setSelection(0);
                } else if (layouts[section-1] == R.layout.multiple_choice_questionary) {
                    ArrayAdapter<String> adapter;
                    ListView list = (ListView) rootView.findViewById(R.id.choices);
                    if(headers[section-1].equals("Unhealthy Assumptions")) {
                       adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_multiple_choice,
                                getResources().getStringArray(R.array.unhealthyAssumptions));
                        list.setAdapter(adapter);
                    }
                    else if(headers[section-1].equals("Unhealthy Core Beliefs")){
                        adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_multiple_choice,
                                getResources().getStringArray(R.array.unhealthyCoreBeliefs));
                        list.setAdapter(adapter);
                    }

                }
                TextView header = (TextView) rootView.findViewById(R.id.header);
                header.setText(headers[section-1]);

            }

            if(!movingLeft) // To avoid duplicates when cycling back
                views.add(rootView);
            return rootView;
        }
    }

    /** This method only gets called once **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionary);

        Intent receivedIntent = getIntent();
        Bundle extras = receivedIntent.getExtras();
        count = extras.getInt("Count");
        log = extras.getString("Log");
        createLogClass(log);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                Log.e("Event", "Page changed " + position);
                prevPos = itr;
                itr = position;
                if(prevPos>itr){
                    movingLeft = true;
                    Log.e("Event", "Moving left");
                }
                else{
                    movingLeft = false;
                }
                setParameters(position, log);
            }
        });

        itr=0;
    }

    private void setParameters(int pos, String log){
        View v= null;
        if(movingLeft) {  // Get last item which is on the right
            v = views.get(pos + 1);
        }
        else{            // Get last item which is on the left
            v = views.get(pos - 1);
        }
        TextView text = (TextView) v.findViewById(R.id.header);
        String tag = text.getText().toString();

        switch(log){
            case "Anxiety Monitoring Log":
                switch(tag){
                    case "Date of anxiety":
                        DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth()+1;
                        int year = datePicker.getYear();
                        String date = month + "/" + day + "/" + year;
                        logAnxiety.setDate(date);
                        break;
                    case "Time of Anxiety Event":
                        TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);
                        int hour = timePicker.getCurrentHour();
                        int min = timePicker.getCurrentMinute();
                        String time = hour + ":" + min;
                        logAnxiety.setTime(time);
                        break;
                    case "Anxiety Level":
                        SeekBar progress = (SeekBar) v.findViewById(R.id.seekBar);
                        int level = progress.getProgress();
                        logAnxiety.setAnxietyLevel(level);
                        break;
                    case "Anxiety Event: Describe the conditions surrounding the event":
                        EditText entry1 = (EditText) v.findViewById(R.id.entry);
                        logAnxiety.setAnxietyEvent(entry1.getText().toString());
                        break;
                    case "Specific Worry: describe the worry that arose from the conditions you describe above.":
                        EditText entry2 = (EditText) v.findViewById(R.id.entry);
                        logAnxiety.setSpecificWorry(entry2.getText().toString());
                        break;
                    case "Possible Triggers":
                        EditText entry3 = (EditText) v.findViewById(R.id.entry);
                        logAnxiety.setTriggers(entry3.getText().toString());
                        break;
                    case "What did you do or not do because of the worry":
                        EditText entry4 = (EditText) v.findViewById(R.id.entry);
                        logAnxiety.setActionTaken(entry4.getText().toString());
                        break;
                    case "Outcome of the situation":
                        EditText entry5 = (EditText) v.findViewById(R.id.entry);
                        logAnxiety.setOutcome(entry5.getText().toString());
                        break;
                }
                break;
            case "Relaxation Log":
                switch(tag){
                    case "Thoughts, emotions, worries BEFORE relaxation:":
                        EditText entry1 = (EditText) v.findViewById(R.id.entry);
                        logRelaxation.setThoughts(entry1.getText().toString());
                        break;
                    case "Select a Relaxation Exercise:":
                        ListView list = (ListView) v.findViewById(R.id.singlechoices);
                        int position = list.getCheckedItemPosition();
                        if(position != -1){
                            String selected = list.getItemAtPosition(position).toString();
                            logRelaxation.setRelaxationExercise(selected);
                        }
                        break;
                    case "Anxiety Level:":
                        SeekBar progress = (SeekBar) v.findViewById(R.id.seekBar);
                        int level = progress.getProgress();
                        logRelaxation.setAnxietyLevel(level);
                        break;
                }
                break;
            case "Challenge Log":
                switch(tag){
                    case "Date of anxiety":
                        DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth()+1;
                        int year = datePicker.getYear();
                        String date = month + "/" + day + "/" + year;
                        logChallenge.setDate(date);
                        break;
                    case "Time of Anxiety Event":
                        TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);
                        int hour = timePicker.getCurrentHour();
                        int min = timePicker.getCurrentMinute();
                        String time = hour + ":" + min;
                        logChallenge.setTime(time);
                        break;
                    case "Anxiety Level":
                        SeekBar progress = (SeekBar) v.findViewById(R.id.seekBar);
                        int level = progress.getProgress();
                        logChallenge.setAnxietyLevel(level);
                        break;
                    case "Anxiety Event: Describe the conditions surrounding the event":
                        EditText entry1 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setAnxietyEvent(entry1.getText().toString());
                        break;
                    case "Specific Worry: describe the worry that arose from the conditions you describe above.":
                        EditText entry2 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setSpecificWorry(entry2.getText().toString());
                        break;
                    case "Possible Triggers":
                        EditText entry3 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setTriggers(entry3.getText().toString());
                        break;
                    case "What did you do or not do because of the worry":
                        EditText entry4 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setActionTaken(entry4.getText().toString());
                        break;
                    case "Outcome of the situation":
                        EditText entry5 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setOutcome(entry5.getText().toString());
                        break;
                    case "Unhealthy Assumptions":
                        ListView list = (ListView) v.findViewById(R.id.choices);
                        SparseBooleanArray checked = list.getCheckedItemPositions();
                        List<String> selectedItems = new ArrayList<String>();
                        for(int i = 0; i < list.getCount(); i++){
                            if(checked.get(i)){
                                selectedItems.add(list.getItemAtPosition(i).toString());
                            }
                        }
                        String assumptions = TextUtils.join(",", selectedItems.toArray());
                        logChallenge.setAssumptions(assumptions);
                        break;
                    case "Challenges to your Unhealthy Assumptions":
                        EditText entry6 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setChallenges(entry6.getText().toString());
                        break;
                    case "Unhealthy Core Beliefs":
                        ListView list2 = (ListView) v.findViewById(R.id.choices);
                        SparseBooleanArray checked2 = list2.getCheckedItemPositions();
                        List<String> selectedItems2 = new ArrayList<String>();
                        for(int i = 0; i < list2.getCount(); i++){
                            if(checked2.get(i)){
                                selectedItems2.add(list2.getItemAtPosition(i).toString());
                            }
                        }
                        String beliefs = TextUtils.join(",", selectedItems2.toArray());
                        logChallenge.setCoreBeliefs(beliefs);
                        break;
                    case "Alternate way of viewing the situation":
                        EditText entry7 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setAlternateView(entry7.getText().toString());
                        break;
                }
                break;
            case "Exposure Log":
                switch(tag){
                    case "Worry situation:":
                        EditText entry1 = (EditText) v.findViewById(R.id.entry);
                        logExposure.setWorrySituation(entry1.getText().toString());
                        break;
                    case "Worst possible outcome":
                        EditText entry4 = (EditText) v.findViewById(R.id.entry);
                        logExposure.setWorstOutcome(entry4.getText().toString());
                        break;
                    case "SUDS prior to exposure": SeekBar progress1 = (SeekBar) v.findViewById(R.id.seekBar);
                        int level1 = progress1.getProgress();
                        logExposure.setSUDSPrior(level1);
                        break;
                    case "SUDS MAXIMUM during exposure":
                        SeekBar progress2 = (SeekBar) v.findViewById(R.id.seekBar);
                        int level2 = progress2.getProgress();
                        logExposure.setSUDSMax(level2);
                        break;
                    case "SUDS AFTER thinking of alternatives":
                        SeekBar progress3 = (SeekBar) v.findViewById(R.id.seekBar);
                        int level3 = progress3.getProgress();
                        logExposure.setSUDSAfter(level3);
                        break;
                    case "SUDS at the end of the exercise":
                        SeekBar progress4 = (SeekBar) v.findViewById(R.id.seekBar);
                        int level4 = progress4.getProgress();
                        logExposure.setSUDSEnd(level4);
                        break;
                    case "Symptons during the exercise":
                        EditText entry2 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setAlternateView(entry2.getText().toString());
                        break;
                    case "Alternative outcomes":
                        EditText entry3 = (EditText) v.findViewById(R.id.entry);
                        logChallenge.setAlternateView(entry3.getText().toString());
                        break;
                }
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
    }
    private void createLogClass(String log){
        switch (log){
            case "Anxiety Monitoring Log": logAnxiety = new LogAnxietyMonitoring(this);
                layouts = logAnxiety.getResources();
                headers = logAnxiety.getQuestions();
                break;
            case "Relaxation Log": logRelaxation = new LogRelaxation(this);
                layouts = logRelaxation.getResources();
                headers = logRelaxation.getQuestions();
                break;
            case "Challenge Log": logChallenge = new LogChallenge(this);
                layouts = logChallenge.getResources();
                headers = logChallenge.getQuestions();
                break;
            case "Exposure Log": logExposure = new LogExposure(this);
                layouts = logExposure.getResources();
                headers = logExposure.getQuestions();
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
    }


    public void displayClosingDialog(View v){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Are you sure you want to exit?");
        mBuilder.setMessage("We will save your current progress so you can continue right from where you left off");
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean(getString(R.string.saved_log), true);
                editor.apply();
                goToLogPicker();
            }
        });
        mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void goToLogPicker(){
        Intent close = new Intent(this, LogPicker.class);
        switch(log){
            case "Anxiety Monitoring Log": logAnxiety.insertToDB();
                break;
            case "Relaxation Log": logRelaxation.insertToDB();
                break;
            case "Challenge Log": logChallenge.insertToDB();
                break;
            case "Exposure Log": logExposure.insertToDB();
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
        close.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(close);
        finish();
    }

    @Override
    public void onBackPressed(){
        if(mViewPager.getCurrentItem() != 0){
            mViewPager.setCurrentItem(itr-1);
        }
        else{
            View placeholder= new View(this);  //This is just to fulfill method requirements
            displayClosingDialog(placeholder);
        }
    }
}
