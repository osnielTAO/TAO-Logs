package org.taoconnect.logs.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.taoconnect.logs.controllers.LogPicker;
import org.taoconnect.logs.controllers.Login;
import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.tools.R;

import java.util.ArrayList;

/**
 * Created by croxx219 on 4/5/17.

public static void readDatabase(){
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor firstSet = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_RELAX_LOG, null);
        List<Cursor> myCursors = new ArrayList<Cursor>();
        myCursors.add(firstSet);

        for(Cursor section : myCursors) {
        if(section.getCount() > 0) {
        String[] columns = section.getColumnNames();
        section.moveToFirst();
        List<String> values = new ArrayList<String>();
        do{
        if(section.getString(section.getColumnIndex(columns[1])) != null)
        Log.e("Values", section.getString(section.getColumnIndex(columns[1])));
        if(section.getInt(section.getColumnIndex(columns[2])) != 0)
        Log.e("Values", String.valueOf(section.getInt(section.getColumnIndex(columns[2]))));
        if(section.getString(section.getColumnIndex(columns[3])) != null)
        Log.e("Values", section.getString(section.getColumnIndex(columns[3])));
        } while(section.moveToNext());
        }
        section.close();
        }
        db.close();
        }*/
public class LogRelaxation implements LogInterface {
    private long rowId = 1;
    private Context context;
    private String thoughts;
    private String relaxationExercise;
    private int anxietyLevel;
    private String tableName = InitialSchema.TABLE_NAME_RELAX_LOG;
    private String[] questions = {"Thoughts, emotions, worries BEFORE relaxation:",
            "Anxiety Level:",
            "Select a Relaxation Exercise:"};

    private int[] resources={R.layout.longresponse_questionary,
            R.layout.slider_questionary,
            R.layout.single_choice_questionary,
    };

    public int[] getResources() {
        return resources;
    }
    public String[] getQuestions() {
        return questions;
    }

    public LogRelaxation(Context context) {
        this.context = context;
    }

    @Override
    public ArrayList<String> getColValues(){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getReadableDatabase();

        ArrayList<String> values = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_RELAX_LOG + "Temp", null);
        String[] columns = cursor.getColumnNames();
        while (cursor.moveToNext()) {
            for(int i = 1; i< columns.length; i++){
                values.add(cursor.getString(cursor.getColumnIndex(columns[i])));
            }
        }
        db.close();
        return values;
    }
    @Override
    public boolean hasTempTable(){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_RELAX_LOG + "Temp", null);
        int rows = cursor.getCount();

        return (rows != 0); // Returns false if table is empty, true otherwise
    }
    @Override
    public void insertToTempDB() {
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InitialSchema._ID, rowId);  // Use the same rowId so it can get updated
        values.put(InitialSchema.THOUGHTS_BEFORE, getThoughts());
        values.put(InitialSchema.RELAXATION_EXERCISE, getRelaxationExercise());
        values.put(InitialSchema.ANXIETY_LEVEL, getAnxietyLevel());

        rowId = db.replace(InitialSchema.TABLE_NAME_RELAX_LOG + "Temp ", null, values);
        db.close();
    }

    @Override
    public void insertToPermanentDB(){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InitialSchema.THOUGHTS_BEFORE, getThoughts());
        values.put(InitialSchema.RELAXATION_EXERCISE, getRelaxationExercise());
        values.put(InitialSchema.ANXIETY_LEVEL, getAnxietyLevel());

        db.insert(InitialSchema.TABLE_NAME_RELAX_LOG,null,values);
        db.execSQL("DELETE FROM " + InitialSchema.TABLE_NAME_RELAX_LOG + "Temp");
        db.close();
    }

    public String getThoughts() {
        return thoughts;
    }

    public String getRelaxationExercise() {
        return relaxationExercise;
    }

    public int getAnxietyLevel() {
        return anxietyLevel;
    }

    public String getTableName() {
        return tableName;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public void setRelaxationExercise(String relaxationExercise) {this.relaxationExercise = relaxationExercise;}

    public void setAnxietyLevel(int anxietyLevel) {
        this.anxietyLevel = anxietyLevel;
    }


}
