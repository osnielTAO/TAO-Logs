package org.taoconnect.logs.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.solver.SolverVariable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.taoconnect.logs.models.LogAnxietyMonitoring;
import org.taoconnect.logs.models.LogChallenge;
import org.taoconnect.logs.models.LogExposure;
import org.taoconnect.logs.models.LogParent;
import org.taoconnect.logs.models.LogRelaxation;
import org.taoconnect.logs.tools.R;

public class Questionary extends AppCompatActivity {

    private static int count;  //Number of questions
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String log;
    private LogParent logClass;
    private static int[] layouts;
    private static String[] headers;

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }


        // Total number of questions to be displayed
        @Override
        public int getCount() {
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "SECTION " + position;
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
                Log.e("Reached end", "");
                rootView = inflater.inflate(R.layout.submit_questionary, container, false);
            }
            else {
                rootView = inflater.inflate(layouts[section-1], container, false);
                if(layouts[section-1] == R.layout.single_choice_questionary){
                    ListView list = (ListView) rootView.findViewById(R.id.choices);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(),android.R.layout.simple_list_item_single_choice,
                            getResources().getStringArray(R.array.singleChoiceRelaxationLog));
                    list.setAdapter(adapter);
                } else if (layouts[section-1] == R.layout.multiple_choice_questionary) {
                    ArrayAdapter<String> adapter;
                    ListView list = (ListView) rootView.findViewById(R.id.choices);
                    if(headers[section-1] == "Unhealthy Assumptions") {
                       adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_multiple_choice,
                                getResources().getStringArray(R.array.unhealthyAssumptions));
                        list.setAdapter(adapter);
                    }
                    else if(headers[section-1] == "Unhealthy Core Beliefs"){
                        adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_multiple_choice,
                                getResources().getStringArray(R.array.unhealthyAssumptions));
                        list.setAdapter(adapter);
                    }

                }
                TextView header = (TextView) rootView.findViewById(R.id.header);
                header.setText(headers[section-1]);

            }
            return rootView;
        }
    }

    private ViewPager mViewPager;

    /** This method only gets called once **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionary);

        Intent receivedIntent = getIntent();
        Bundle extras = receivedIntent.getExtras();
        count = extras.getInt("Count");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        log = extras.getString("Log");
        createLogClass(log);
        layouts = logClass.getResources();
        headers = logClass.getQuestions();
    }

    private void createLogClass(String log){
        switch (log){
            case "Anxiety Monitoring Log": logClass = new LogAnxietyMonitoring();
                break;
            case "Relaxation Log": logClass = new LogRelaxation();
                break;
            case "Challenge Log": logClass = new LogChallenge();
                break;
            case "Exposure Log": logClass = new LogExposure();
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

    //TODO: save progress locally before going back
    private void goToLogPicker(){
        Intent close = new Intent(this, LogPicker.class);
        close.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(close);
        finish();
    }
}
