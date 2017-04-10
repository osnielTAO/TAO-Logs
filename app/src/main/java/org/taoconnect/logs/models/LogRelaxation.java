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

public class LogRelaxation implements LogInterface {
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
    public void insertToDB() {
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InitialSchema.THOUGHTS_BEFORE, getThoughts());
        values.put(InitialSchema.RELAXATION_EXERCISE, getRelaxationExercise());
        values.put(InitialSchema.ANXIETY_LEVEL, getAnxietyLevel());

        long newRow = db.insert(getTableName(), null, values);
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
