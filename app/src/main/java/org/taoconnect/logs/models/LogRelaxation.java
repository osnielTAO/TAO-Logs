package org.taoconnect.logs.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.taoconnect.logs.controllers.LogPicker;
import org.taoconnect.logs.controllers.Login;
import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.databases.MySQLiteHelper;
import org.taoconnect.logs.tools.R;

/**
 * Created by croxx219 on 4/5/17.
 */

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

    //TODO: Change this to private
    public static final String INIT_TEMP = "CREATE TABLE IF NOT EXISTS " + InitialSchema.TABLE_NAME_RELAX_LOG + "Temp "
            + "( " + InitialSchema._ID + " INTEGER PRIMARY KEY, "
            + InitialSchema.THOUGHTS_BEFORE +  " TEXT,"
            + InitialSchema.ANXIETY_LEVEL + " INTEGER,"
            + InitialSchema.RELAXATION_EXERCISE + " TEXT)";


    private final String DROP_TEMP_TABLE = "DROP TABLE IF EXISTS " + InitialSchema.TABLE_NAME_RELAX_LOG + "Temp ";

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
    public void insertToTempDB() {
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        db.execSQL(INIT_TEMP);
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
        db.execSQL(DROP_TEMP_TABLE);
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

    public void setRelaxationExercise(String relaxationExercise) {
        this.relaxationExercise = relaxationExercise;
    }

    public void setAnxietyLevel(int anxietyLevel) {
        this.anxietyLevel = anxietyLevel;
    }


}
