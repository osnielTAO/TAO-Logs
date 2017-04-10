package org.taoconnect.logs.models;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.tools.R;

/**
 * Created by croxx219 on 4/5/17.
 */

public class LogAnxietyMonitoring implements LogInterface {
    private String tableName = InitialSchema.TABLE_NAME_ANX_MON_LOG;
    private String dateSelected;
    private String timeSelected;
    private int anxietyLevel;
    private String anxietyEvent;
    private String specificWorry;
    private String triggers;
    private String actionTaken;
    private String outcome;
    private Context context;
    private String[] questions = {"Date of anxiety",
                                  "Time of Anxiety Event",
                                  "Anxiety Level",
                                  "Anxiety Event: Describe the conditions surrounding the event",
                                  "Specific Worry: describe the worry that arose from the conditions you describe above.",
                                  "Possible Triggers:",
                                  "What did you do or not do because of the worry",
                                  "Outcome of the situation:"};
    private int[] resources={R.layout.datepicker_questionary,
            R.layout.timepicker_questionary,
            R.layout.slider_questionary,
            R.layout.longresponse_questionary,
            R.layout.longresponse_questionary,
            R.layout.short_response_questionary,
            R.layout.short_response_questionary,
            R.layout.short_response_questionary};

    public int[] getResources() {
        return resources;
    }

    public String[] getQuestions() {
        return questions;
    }

    public LogAnxietyMonitoring(Context context){
        this.context = context;
    }

    public static LogAnxietyMonitoring newInstance(Context context, boolean isFromDB){
        LogAnxietyMonitoring log = null;
        if(isFromDB){
            return log;
            //TODO:Pull from database
        }
        else {
            log = new LogAnxietyMonitoring(context);
            return log;
        }
    }

    @Override
    public void insertToDB() {
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InitialSchema.DATE_SELECTED, getDateSelected());
        values.put(InitialSchema.TIME_SELECTED, getTimeSelected());
        values.put(InitialSchema.ANXIETY_LEVEL, getAnxietyLevel());
        values.put(InitialSchema.ANXIETY_EVENT, getAnxietyEvent());
        values.put(InitialSchema.SPECIFIC_WORRY, getSpecificWorry());
        values.put(InitialSchema.TRIGGERS, getTriggers());
        values.put(InitialSchema.ACTION_TAKEN, getActionTaken());
        values.put(InitialSchema.OUTCOME, getOutcome());

        long newRow = db.insert(getTableName(), null, values);
    }

    public String getDateSelected() {
        return dateSelected;
    }

    public String getTimeSelected() {
        return timeSelected;
    }

    public int getAnxietyLevel() {
        return anxietyLevel;
    }

    public String getAnxietyEvent() {
        return anxietyEvent;
    }

    public String getSpecificWorry() {
        return specificWorry;
    }

    public String getTriggers() {
        return triggers;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setDate(String date){
        this.dateSelected = date;
    }
    public void setTime(String time){
        this.timeSelected = time;
    }
    public void setAnxietyLevel(int level){
        this.anxietyLevel = level;
    }
    public void setAnxietyEvent(String event){
        this.anxietyEvent = event;
    }
    public void setSpecificWorry(String worry){
        this.specificWorry = worry;
    }
    public void setTriggers(String triggers){
        this.triggers = triggers;
    }
    public void setActionTaken(String action){
        this.actionTaken = action;
    }
    public void setOutcome(String outcome){
        this.outcome = outcome;
    }

    public String getTableName(){return tableName;}

}
