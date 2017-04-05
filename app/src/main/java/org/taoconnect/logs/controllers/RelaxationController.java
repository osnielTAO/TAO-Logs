package org.taoconnect.logs.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.models.LogRelaxation;

/**
 * Created by croxx219 on 4/5/17.
 */

public class RelaxationController {
    Context context;
    public void RelaxationController(Context context){
        this.context = context;
    }
    public void insert(LogRelaxation log){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InitialSchema.THOUGHTS_BEFORE, log.getThoughts());
        values.put(InitialSchema.RELAXATION_EXERCISE, log.getRelaxationExercise());
        values.put(InitialSchema.ANXIETY_LEVEL, log.getAnxietyLevel());

        long newRow = db.insert(log.getTableName(), null, values);

    }
}
