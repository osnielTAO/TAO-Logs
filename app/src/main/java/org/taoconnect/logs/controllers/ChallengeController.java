package org.taoconnect.logs.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.models.LogChallenge;
import org.taoconnect.logs.models.LogRelaxation;

/**
 * Created by croxx219 on 4/5/17.
 */

public class ChallengeController{
    Context context;
    public void ChallengeController(Context context){
        this.context = context;
    }
    public void insert(LogChallenge log){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InitialSchema.DATE_SELECTED, log.getDateSelected());
        values.put(InitialSchema.TIME_SELECTED, log.getTimeSelected());
        values.put(InitialSchema.ANXIETY_LEVEL, log.getAnxietyLevel());
        values.put(InitialSchema.ANXIETY_EVENT, log.getAnxietyEvent());
        values.put(InitialSchema.SPECIFIC_WORRY, log.getSpecificWorry());
        values.put(InitialSchema.TRIGGERS, log.getTriggers());
        values.put(InitialSchema.ACTION_TAKEN, log.getActionTaken());
        values.put(InitialSchema.OUTCOME, log.getOutcome());
        values.put(InitialSchema.UNH_ASSUMPTIONS, log.getAssumptions());
        values.put(InitialSchema.CHALLENGES, log.getChallenges());
        values.put(InitialSchema.CORE_BELIEVES, log.getCoreBelieves());
        values.put(InitialSchema.ALTERNATE_VIEW, log.getAlternateView());

        long newRow = db.insert(log.getTableName(), null, values);

    }


}
