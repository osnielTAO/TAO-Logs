package org.taoconnect.logs.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.models.LogAnxietyMonitoring;
import org.taoconnect.logs.models.LogChallenge;

/**
 * Created by croxx219 on 4/5/17.
 */

public class AnxietyMonitoringController {
    Context context;
    public void ChallengeController(Context context){
        this.context = context;
    }
    public void insert(LogAnxietyMonitoring log) {
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

        long newRow = db.insert(log.getTableName(), null, values);
    }
}
