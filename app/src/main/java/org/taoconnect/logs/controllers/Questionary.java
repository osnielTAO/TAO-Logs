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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.taoconnect.logs.models.LogAnxietyMonitoring;
import org.taoconnect.logs.models.LogChallenge;
import org.taoconnect.logs.models.LogExposure;
import org.taoconnect.logs.models.LogRelaxation;
import org.taoconnect.logs.tools.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Questionary extends AppCompatActivity {

    private static int count;  //Number of questions
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static ViewPager mViewPager;
    private static String log;
    private static String action;
    private static LogAnxietyMonitoring logAnxiety;
    private static LogChallenge logChallenge;
    private static LogExposure logExposure;
    private static LogRelaxation logRelaxation;
    private static int[] layouts;
    private static String[] headers;
    private static List<View> views = new ArrayList<View>();      // Holds all the view objects for every fragment created
    private static int itr = 0;
    private static int prevPos=0;
    private static boolean movingLeft = false;
    private boolean dateClicked = false;
    private boolean timeClicked = false;
    private boolean isPreviousFilled;
    private boolean canSwipe = false;
    private boolean usingLevel0= false;

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
            // Each rootView has its own id to determine which layout holds the widget that needs to be saved/updated
            int section = getArguments().getInt(ARG_SECTION_NUMBER);
            if(section == count){
                rootView = inflater.inflate(R.layout.submit_questionary, container, false);
                int id = R.layout.submit_questionary;
                rootView.setId(Math.abs(id));
            }
            else {
                rootView = inflater.inflate(layouts[section-1], container, false);
                rootView.setId(layouts[section-1]);
                if(layouts[section-1] == R.layout.single_choice_questionary){   // A single choice listview
                    ListView list = (ListView) rootView.findViewById(R.id.singlechoices);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(),android.R.layout.simple_list_item_single_choice,
                            getResources().getStringArray(R.array.singleChoiceRelaxationLog));
                    list.setAdapter(adapter);
                    list.setSelection(0);
                } else if (layouts[section-1] == R.layout.multiple_choice_questionary) {   // A multiple choice listview
                    ArrayAdapter<String> adapter;
                    ListView list = (ListView) rootView.findViewById(R.id.choices);
                    if(headers[section-1].equals("Unhealthy Assumptions")) {
                       adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_multiple_choice,
                                getResources().getStringArray(R.array.unhealthyAssumptions));
                        list.setAdapter(adapter);
                    }
                    else if(headers[section-1].equals("Unhealthy Core Beliefs")){       // A multiple choice listview
                        adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_multiple_choice,
                                getResources().getStringArray(R.array.unhealthyCoreBeliefs));
                        list.setAdapter(adapter);
                    }

                }
                TextView header = (TextView) rootView.findViewById(R.id.header);     // Question header
                header.setText(headers[section-1]);

            }

            if(views.size() != 0) { // To avoid duplicates when cycling back/moving left
                boolean isThere = false;
                for (int position = 0; position < views.size(); position++){  // Going back or overriding some view
                    if (views.get(position).getId() == rootView.getId()) {
                        views.set(position, rootView);
                        isThere = true;
                    }
                }
                if(!isThere){
                    views.add(rootView);
                    Log.e("Event", "Added " + rootView.getId());
                    int id = R.layout.submit_questionary;
                    if(action.equals("Continue") && rootView.getId() != Math.abs(id)){  // Avoid working with the last layout
                        goToLastView(log, itr);
                    }
                }
            }
            else{  // Start: list is initially empty
                views.add(rootView);
                if(action.equals("Continue")){
                    goToLastView(log, itr);
                }
            }

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
        count = extras.getInt("Count");     // Total number of questions
        log = extras.getString("Log");
        action = extras.getString("Action");
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
                // Not at the first screen
                if(position != 0){
                    isPreviousFilled = isFulfilled(position-1);
                }
                prevPos = itr;
                itr = position;
                if(!movingLeft && !action.equals("Continue")) { //Swiping left, moving to the right of the screen
                    // Don't save if action is continue. I am manually swiping to fill in the views, writing here in that case overrides the
                    // table values necessary to fill the views.
                    writeToTable(false);
                }

                if(prevPos>itr){
                    movingLeft = true;
                }
                else{
                    movingLeft = false;
                }

                // Don't let user move to next view
                if(!isPreviousFilled){
                    mViewPager.setCurrentItem(itr-1);
                }
                else{  // Save information entered by the user
                    setParameters(position, log);
                }


            }
        });
        itr=0;  // Restart static variable with every call to this activity
    }

    // TODO: Figure out swiping in datepicker, timepicker and seekbar
    public void setDateAsClicked(View v){
        dateClicked = true;
    }

    public void setTimeAsClicked(View v){
        timeClicked = true;
    }

    // An error message
    private void showAnim(View v){
        Animation a = AnimationUtils.loadAnimation(this, R.anim.shake);
        a.reset();
        TextView error = (TextView) v.findViewById(R.id.errorMessage);
        error.setVisibility(View.VISIBLE);
        error.clearAnimation();
        error.startAnimation(a);
    }

    // This methods is needed to stop the user from moving to the next one until the view is filled
    private boolean isFulfilled(int currentPos){
        View v = views.get(currentPos);
        int layout = v.getId();

        switch(layout){
            case R.layout.datepicker_questionary:
                if(!dateClicked){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                    mBuilder.setTitle("Enter a date");
                    mBuilder.setMessage("Do you want to use today's date?");
                    mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            canSwipe = true;
                        }
                    });
                    mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getBaseContext(), "Select a date", Toast.LENGTH_SHORT).show();
                            canSwipe = false;
                            dialog.cancel();
                       }
                    });
                    mBuilder.setCancelable(false);
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
                break;
            case R.layout.timepicker_questionary:
                if(!timeClicked){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                    mBuilder.setTitle("Enter a time");
                    mBuilder.setMessage("Do you want to use the current time?");
                    mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            canSwipe = true;
                        }
                    });
                    mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getBaseContext(), "Select a time", Toast.LENGTH_SHORT).show();
                            canSwipe = false;
                            dialog.cancel();
                        }
                    });
                    mBuilder.setCancelable(false);
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
                break;
            case R.layout.longresponse_questionary:
                EditText entry1 = (EditText) v.findViewById(R.id.entry);
                if(entry1.getText().toString().matches("")){
                    canSwipe = false;
                    showAnim(v);
                }
                else{
                    TextView errorText = (TextView) v.findViewById(R.id.errorMessage);
                    errorText.setVisibility(View.INVISIBLE);
                    canSwipe = true;
                }
                break;
            case R.layout.multiple_choice_questionary:
                ListView multipleList = (ListView) v.findViewById(R.id.choices);
                SparseBooleanArray checked = multipleList.getCheckedItemPositions();
                if(checked == null) //Nothing selected
                {
                    canSwipe = false;
                    showAnim(v);
                }
                else{
                    TextView errorText = (TextView) v.findViewById(R.id.errorMessage);
                    errorText.setVisibility(View.INVISIBLE);
                    canSwipe = true;
                }
                break;
            case R.layout.single_choice_questionary:
                ListView singleList = (ListView) v.findViewById(R.id.singlechoices);
                int pos = singleList.getCheckedItemPosition();
                if(pos == -1) //Nothing selected
                {
                    canSwipe = false;
                    showAnim(v);
                }
                else{
                    TextView errorText = (TextView) v.findViewById(R.id.errorMessage);
                    errorText.setVisibility(View.INVISIBLE);
                    canSwipe = true;
                }
                break;
            case R.layout.short_response_questionary:
                EditText entry2 = (EditText) v.findViewById(R.id.entry);
                if(entry2.getText().toString().matches("")){
                    canSwipe = false;
                    showAnim(v);
                }
                else{
                    TextView errorText = (TextView) v.findViewById(R.id.errorMessage);
                    errorText.setVisibility(View.INVISIBLE);
                    canSwipe = true;
                }
                break;
            case R.layout.slider_questionary:  //TODO: Give exclusivity/focus to the dialog
                SeekBar progress = (SeekBar) v.findViewById(R.id.seekBar);
                if(progress.getProgress() == 0 && !usingLevel0){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                    mBuilder.setTitle("Select your level");
                    mBuilder.setMessage("Do you want to use level 0?");
                    mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            usingLevel0 = true;
                            canSwipe = true;
                        }
                    });
                    mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getBaseContext(), "Select a level", Toast.LENGTH_SHORT).show();
                            canSwipe = false;
                            dialog.cancel();
                        }
                    });
                    mBuilder.setCancelable(false);
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
                else canSwipe = true;
                break;
        }
        return canSwipe;
    }

    // Saves user input onswipe to the correspondent object
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
                        logExposure.setSymptons(entry2.getText().toString());
                        break;
                    case "Alternative outcomes":
                        EditText entry3 = (EditText) v.findViewById(R.id.entry);
                        logExposure.setAlternative(entry3.getText().toString());
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

    // Instantiate the correct object depending on the intent sent
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

    // If the action was continue, this method populates all the views the user already entered
    // and opens the next view to be filled.
    private static void goToLastView(String log, int pos){
        ArrayList<String> colValues = new ArrayList<String>();
        switch (log){
            case "Anxiety Monitoring Log": colValues = logAnxiety.getColValues();
                break;
            case "Relaxation Log": colValues = logRelaxation.getColValues();
                break;
            case "Challenge Log": colValues = logChallenge.getColValues();
                break;
            case "Exposure Log": colValues = logExposure.getColValues();
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
        fillWidgets(colValues.get(pos));
    }

    // Manually fills the widget for the view from which this method was called. itr determines the position of such view
    private static void fillWidgets(String entry){
            View v = views.get(itr);
            TextView header = (TextView) v.findViewById(R.id.header);
            String tag = header.getText().toString();
            Log.e("Event", tag);
            switch (log) {
                case "Anxiety Monitoring Log":
                    switch (tag) {
                        case "Date of anxiety":
                            String[] date = entry.split("/");
                            DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
                            datePicker.updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                            break;
                        case "Time of Anxiety Event":
                            String[] time = entry.split(":");
                            TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);
                            timePicker.setCurrentHour(Integer.parseInt(time[0]));
                            timePicker.setCurrentMinute(Integer.parseInt(time[1]));
                            break;
                        case "Anxiety Level":
                            SeekBar progress = (SeekBar) v.findViewById(R.id.seekBar);
                            progress.setProgress(Integer.parseInt(entry));
                            break;
                        case "Anxiety Event: Describe the conditions surrounding the event":
                            EditText entry1 = (EditText) v.findViewById(R.id.entry);
                            entry1.setText(entry);
                            break;
                        case "Specific Worry: describe the worry that arose from the conditions you describe above.":
                            EditText entry2 = (EditText) v.findViewById(R.id.entry);
                            entry2.setText(entry);
                            break;
                        case "Possible Triggers":
                            EditText entry3 = (EditText) v.findViewById(R.id.entry);
                            entry3.setText(entry);
                            break;
                        case "What did you do or not do because of the worry":
                            EditText entry4 = (EditText) v.findViewById(R.id.entry);
                            entry4.setText(entry);
                            break;
                        case "Outcome of the situation":
                            EditText entry5 = (EditText) v.findViewById(R.id.entry);
                            entry5.setText(entry);
                            break;
                    }
                    break;
                case "Relaxation Log":
                    switch (tag) {
                        case "Thoughts, emotions, worries BEFORE relaxation:":
                            EditText entry1 = (EditText) v.findViewById(R.id.entry);
                            entry1.setText(entry);
                            Log.e("Event", "Trying to add: " + entry);
                            break;
                        case "Anxiety Level:":
                            SeekBar progress = (SeekBar) v.findViewById(R.id.seekBar);
                            progress.setProgress(Integer.parseInt(entry));
                            break;
                        case "Select a Relaxation Exercise:":
                            ListView list = (ListView) v.findViewById(R.id.singlechoices);
                            for(int i =0; i < list.getCount(); i++){
                                if(list.getItemAtPosition(i).toString().equals(entry)){
                                    list.setItemChecked(i, true);
                                }
                            }
                            break;
                    }
                    break;
                case "Challenge Log":
                    switch (tag) {
                        case "Date of anxiety":
                            String[] date = entry.split("/");
                            DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
                            datePicker.updateDate(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                            break;
                        case "Time of Anxiety Event":
                            String[] time = entry.split(":");
                            TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);
                            timePicker.setCurrentHour(Integer.parseInt(time[0]));
                            timePicker.setCurrentMinute(Integer.parseInt(time[1]));
                            break;
                        case "Anxiety Level":
                            SeekBar progress = (SeekBar) v.findViewById(R.id.seekBar);
                            progress.setProgress(Integer.parseInt(entry));
                            break;
                        case "Anxiety Event: Describe the conditions surrounding the event":
                            EditText entry1 = (EditText) v.findViewById(R.id.entry);
                            entry1.setText(entry);
                            break;
                        case "Specific Worry: describe the worry that arose from the conditions you describe above.":
                            EditText entry2 = (EditText) v.findViewById(R.id.entry);
                            entry2.setText(entry);
                            break;
                        case "Possible Triggers":
                            EditText entry3 = (EditText) v.findViewById(R.id.entry);
                            entry3.setText(entry);
                            break;
                        case "What did you do or not do because of the worry":
                            EditText entry4 = (EditText) v.findViewById(R.id.entry);
                            entry4.setText(entry);
                            break;
                        case "Outcome of the situation":
                            EditText entry5 = (EditText) v.findViewById(R.id.entry);
                            entry5.setText(entry);
                            break;
                        case "Unhealthy Assumptions":
                            ListView list = (ListView) v.findViewById(R.id.choices);
                            String[] selectedItems = entry.split(",");
                            for(int i =0; i < list.getCount(); i++){
                                for(String item : selectedItems) {
                                    if (list.getItemAtPosition(i).toString().equals(item)) {
                                        list.setItemChecked(i, true);
                                    }
                                }
                            }
                            break;
                        case "Challenges to your Unhealthy Assumptions":
                            EditText entry6 = (EditText) v.findViewById(R.id.entry);
                            entry6.setText(entry);
                            break;
                        case "Unhealthy Core Beliefs":
                            ListView list2 = (ListView) v.findViewById(R.id.choices);
                            String[] selected = entry.split(",");
                            for(int i =0; i < list2.getCount(); i++){
                                for(String item : selected) {
                                    if (list2.getItemAtPosition(i).toString().equals(item)) {
                                        list2.setItemChecked(i, true);
                                    }
                                }
                            }
                            break;
                        case "Alternate way of viewing the situation":
                            EditText entry7 = (EditText) v.findViewById(R.id.entry);
                            entry7.setText(entry);
                            break;
                    }
                    break;
                case "Exposure Log":
                    switch (tag) {
                        case "Worry situation:":
                            EditText entry1 = (EditText) v.findViewById(R.id.entry);
                            entry1.setText(entry);
                            break;
                        case "Worst possible outcome":
                            EditText entry4 = (EditText) v.findViewById(R.id.entry);
                            entry4.setText(entry);
                            break;
                        case "SUDS prior to exposure":
                            SeekBar progress1 = (SeekBar) v.findViewById(R.id.seekBar);
                            progress1.setProgress(Integer.parseInt(entry));
                            break;
                        case "SUDS MAXIMUM during exposure":
                            SeekBar progress2 = (SeekBar) v.findViewById(R.id.seekBar);
                            progress2.setProgress(Integer.parseInt(entry));
                            break;
                        case "SUDS AFTER thinking of alternatives":
                            SeekBar progress3 = (SeekBar) v.findViewById(R.id.seekBar);
                            progress3.setProgress(Integer.parseInt(entry));
                            break;
                        case "SUDS at the end of the exercise":
                            SeekBar progress4 = (SeekBar) v.findViewById(R.id.seekBar);
                            progress4.setProgress(Integer.parseInt(entry));
                            break;
                        case "Symptons during the exercise":
                            EditText entry2 = (EditText) v.findViewById(R.id.entry);
                            entry2.setText(entry);
                            break;
                        case "Alternative outcomes":
                            EditText entry3 = (EditText) v.findViewById(R.id.entry);
                            entry3.setText(entry);
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
            mViewPager.setCurrentItem(itr+1);
    }

    // This methods writes to temp table if boolean is false otherwise it writes to permanent table
    private void writeToTable(boolean isPermanent){
        switch (log) {
            case "Anxiety Monitoring Log":
                if(!isPermanent)
                    logAnxiety.insertToTempDB();
                else logAnxiety.insertToPermanentDB();
                break;
            case "Relaxation Log":
                if(!isPermanent)
                    logRelaxation.insertToTempDB();
                else logRelaxation.insertToPermanentDB();
                break;
            case "Challenge Log":
                if(!isPermanent)
                    logChallenge.insertToTempDB();
                else logChallenge.insertToPermanentDB();
                break;
            case "Exposure Log":
                if(!isPermanent)
                    logExposure.insertToTempDB();
                else logExposure.insertToPermanentDB();
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

    // Submit button listener
    public void submitLog(View v){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Are you ready to submit?");
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putBoolean(getString(R.string.saved_log), false);
                editor.apply();
                writeToTable(true);
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
        writeToTable(true);
    }

    // Cancel button listener
    public void dismissLog(View v){
        goToLogPicker();
    }
    // Red X/Close button listener
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
                writeToTable(false);
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

    // Shuts down this activity and moves to LogPicker
    private void goToLogPicker(){
        Intent close = new Intent(this, LogPicker.class);
        close.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        views.clear();
        startActivity(close);
        finish();
    }

    /** If the user press the back key it moves to the previous view. OnPageSelected will make the necessary
     * changes whent his happens
     */
    @Override
    public void onBackPressed(){
        if(mViewPager.getCurrentItem() != 0){
            mViewPager.setCurrentItem(itr-1);
        }
        else{  // User is at the very first view
            View placeholder= new View(this);  //This is just to fulfill method requirements
            displayClosingDialog(placeholder);
        }
    }
}
