package org.taoconnect.logs.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.models.LogExposure;
import org.taoconnect.logs.models.LogRelaxation;

/**
 * Created by croxx219 on 4/5/17.
 */

public class ExposureController {
    Context context;
    public void ExposureController(Context context){
        this.context = context;
    }
    public void insert(LogExposure log){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InitialSchema.WORRY_SITUATION, log.getWorrySituation());
        values.put(InitialSchema.WORST_OUTCOME, log.getWorstOutcome());
        values.put(InitialSchema.SUDS_PRIOR, log.getSUDSPrior());
        values.put(InitialSchema.SUDS_MAX, log.getSUDSMax());
        values.put(InitialSchema.SUDS_AFTER, log.getSUDSAfter());
        values.put(InitialSchema.SUDS_END, log.getSUDSEnd());
        values.put(InitialSchema.SYMPTONS_DURING, log.getSymptons());
        values.put(InitialSchema.ALTERNATIVE_OUTCOMES, log.getAlternative());

        long newRow = db.insert(log.getTableName(), null, values);

    }
}
