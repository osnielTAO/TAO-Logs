package org.taoconnect.logs.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.tools.R;

import java.util.ArrayList;

/**
 * Created by croxx219 on 4/5/17.
 */

public class LogChallenge implements LogInterface {
    private Context context;
    private long rowId = 1;
    private final String tableName = InitialSchema.TABLE_NAME_CHALLENGE_LOG;
    private String dateSelected;
    private String timeSelected;
    private int anxietyLevel;
    private String anxietyEvent;
    private String specificWorry;
    private String actionTaken;
    private String outcome;
    private String assumptions;
    private String challenges;
    private String coreBelieves;
    private String triggers;
    private long timestamp;
    private String alternateView;
    private String[] questions = {"Date of anxiety",
            "Time of Anxiety Event",
            "Anxiety Level",
            "Anxiety Event: Describe the conditions surrounding the event",
            "Specific Worry: describe the worry that arose from the conditions you describe above.",
            "Possible Triggers:",
            "What did you do or not do because of the worry",
            "Outcome of the situation:",
            "Unhealthy Assumptions",
            "Challenges to your Unhealthy Assumptions",
            "Unhealthy Core Beliefs",
            "Alternate way of viewing the situation"};

    private int[] resources={R.layout.datepicker_questionary,
            R.layout.timepicker_questionary,
            R.layout.slider_questionary,
            R.layout.longresponse_questionary,
            R.layout.longresponse_questionary,
            R.layout.short_response_questionary,
            R.layout.short_response_questionary,
            R.layout.short_response_questionary,
            R.layout.multiple_choice_questionary,
            R.layout.short_response_questionary,
            R.layout.multiple_choice_questionary,
            R.layout.short_response_questionary};


    public int[] getResources() {
        return resources;
    }
    public String[] getQuestions() {
        return questions;
    }

    public LogChallenge(Context context){
        this.context = context;
    }

    @Override
    public void insertToTempDB() {
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InitialSchema._ID, rowId);  // Use the same rowId so it can get updated
        values.put(InitialSchema.DATE_SELECTED, getDateSelected());
        values.put(InitialSchema.TIME_SELECTED, getTimeSelected());
        values.put(InitialSchema.ANXIETY_LEVEL, getAnxietyLevel());
        values.put(InitialSchema.ANXIETY_EVENT, getAnxietyEvent());
        values.put(InitialSchema.SPECIFIC_WORRY, getSpecificWorry());
        values.put(InitialSchema.TRIGGERS, getTriggers());
        values.put(InitialSchema.ACTION_TAKEN, getActionTaken());
        values.put(InitialSchema.OUTCOME, getOutcome());
        values.put(InitialSchema.UNH_ASSUMPTIONS, getAssumptions());
        values.put(InitialSchema.CHALLENGES, getChallenges());
        values.put(InitialSchema.CORE_BELIEVES, getCoreBelieves());
        values.put(InitialSchema.ALTERNATE_VIEW, getAlternateView());
        values.put(InitialSchema.TIMESTAMP, getTimestamp());


        rowId = db.insert(InitialSchema.TABLE_NAME_CHALLENGE_LOG + "Temp ", null, values);

        db.close();
    }
    @Override
    public ArrayList<String> getColValues(){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ArrayList<String> values = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_CHALLENGE_LOG + "Temp", null);
        String[] columns = cursor.getColumnNames();
        while (cursor.moveToNext()) {
            for(int i = 1; i< columns.length - 1; i++){
                values.add(cursor.getString(cursor.getColumnIndex(columns[i])));
            }
        }

        return values;
    }
    @Override
    public long getTimestampDB() {
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + InitialSchema.TIMESTAMP + " FROM " + InitialSchema.TABLE_NAME_CHALLENGE_LOG + "Temp", null);
        cursor.moveToFirst();
        db.close();
        return cursor.getLong(cursor.getColumnIndex(InitialSchema.TIMESTAMP));
    }

    @Override
    public void insertToPermanentDB(){
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
        values.put(InitialSchema.UNH_ASSUMPTIONS, getAssumptions());
        values.put(InitialSchema.CHALLENGES, getChallenges());
        values.put(InitialSchema.CORE_BELIEVES, getCoreBelieves());
        values.put(InitialSchema.ALTERNATE_VIEW, getAlternateView());
        values.put(InitialSchema.TIMESTAMP, getTimestamp());

        db.insert(InitialSchema.TABLE_NAME_CHALLENGE_LOG,null,values);
        db.execSQL("DELETE FROM " + InitialSchema.TABLE_NAME_CHALLENGE_LOG + "Temp");
        db.close();
    }
    @Override
    public boolean hasTempTable(){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_CHALLENGE_LOG + "Temp", null);
        int rows = cursor.getCount();

        return (rows != 0); // Returns false if table is empty, true otherwise
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTriggers() {
        return triggers;
    }

    public void setTriggers(String triggers) {
        this.triggers = triggers;
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

    public String getActionTaken() {
        return actionTaken;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getAssumptions() {
        return assumptions;
    }

    public String getChallenges() {
        return challenges;
    }

    public String getCoreBelieves() {
        return coreBelieves;
    }

    public String getAlternateView() {
        return alternateView;
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
    public void setActionTaken(String action){
        this.actionTaken = action;
    }
    public void setOutcome(String outcome){
        this.outcome = outcome;
    }
    public void setAssumptions(String assumptions){
        this.assumptions = assumptions;
    }
    public void setChallenges(String challenges){
        this.challenges = challenges;
    }
    public void setCoreBeliefs(String coreBelieves){
        this.coreBelieves = coreBelieves;
    }
    public void setAlternateView(String alternative){
        this.alternateView = alternative;
    }
    public String getTableName(){
        return tableName;
    }
}
