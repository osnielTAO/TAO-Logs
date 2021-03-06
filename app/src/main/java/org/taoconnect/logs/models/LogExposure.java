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

public class LogExposure implements LogInterface {
    private Context context;
    private long rowId = 1;
    public String tableName = InitialSchema.TABLE_NAME_EXPOSURE_LOG;
    private String worrySituation;
    private String worstOutcome;
    private int SUDSPrior;
    private int SUDSMax;
    private int SUDSAfter;
    private int SUDSEnd;
    private long timestamp;
    private String symptons;
    private String alternative;
    private String[] questions ={"Worry situation:",
            "Worst possible outcome",
            "SUDS prior to exposure",
            "SUDS MAXIMUM during exposure",
            "SUDS AFTER thinking of alternatives",
            "SUDS at the end of the exercise",
            "Symptons during the exercise",
            "Alternative outcomes",};

    private int[] resources={R.layout.short_response_questionary,
            R.layout.short_response_questionary,
            R.layout.slider_questionary,
            R.layout.slider_questionary,
            R.layout.slider_questionary,
            R.layout.slider_questionary,
            R.layout.longresponse_questionary,
            R.layout.longresponse_questionary,
    };

    public int[] getResources() {
        return resources;
    }
    public String[] getQuestions() {
        return questions;
    }
    public LogExposure(Context context) {
        this.context = context;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public void insertToTempDB() {
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InitialSchema._ID, rowId);  // Use the same rowId so it can get updated
        values.put(InitialSchema.WORRY_SITUATION, getWorrySituation());
        values.put(InitialSchema.WORST_OUTCOME, getWorstOutcome());
        values.put(InitialSchema.SUDS_PRIOR, getSUDSPrior());
        values.put(InitialSchema.SUDS_MAX, getSUDSMax());
        values.put(InitialSchema.SUDS_AFTER, getSUDSAfter());
        values.put(InitialSchema.SUDS_END, getSUDSEnd());
        values.put(InitialSchema.SYMPTONS_DURING, getSymptons());
        values.put(InitialSchema.ALTERNATIVE_OUTCOMES, getAlternative());
        values.put(InitialSchema.TIMESTAMP, getTimestamp());

        rowId = db.insert(InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp ", null, values);

        db.close();
    }

    @Override
    public void insertToPermanentDB(){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InitialSchema.WORRY_SITUATION, getWorrySituation());
        values.put(InitialSchema.WORST_OUTCOME, getWorstOutcome());
        values.put(InitialSchema.SUDS_PRIOR, getSUDSPrior());
        values.put(InitialSchema.SUDS_MAX, getSUDSMax());
        values.put(InitialSchema.SUDS_AFTER, getSUDSAfter());
        values.put(InitialSchema.SUDS_END, getSUDSEnd());
        values.put(InitialSchema.SYMPTONS_DURING, getSymptons());
        values.put(InitialSchema.ALTERNATIVE_OUTCOMES, getAlternative());
        values.put(InitialSchema.TIMESTAMP, getTimestamp());

        db.insert(InitialSchema.TABLE_NAME_EXPOSURE_LOG,null,values);
        db.execSQL("DELETE FROM " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp");
        db.close();
    }


    @Override
    public ArrayList<String> getColValues(){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        ArrayList<String> values = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp", null);
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

        Cursor cursor = db.rawQuery("SELECT " + InitialSchema.TIMESTAMP + " FROM " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp", null);
        cursor.moveToFirst();
        db.close();
        cursor.close();
        return cursor.getLong(cursor.getColumnIndex(InitialSchema.TIMESTAMP));
    }

    @Override
    public boolean hasTempTable(){
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp", null);
        int rows = cursor.getCount();

        return (rows != 0); // Returns false if table is empty, true otherwise
    }

    public String getWorrySituation() {
        return worrySituation;
    }

    public String getWorstOutcome() {
        return worstOutcome;
    }

    public int getSUDSPrior() {
        return SUDSPrior;
    }

    public int getSUDSMax() {
        return SUDSMax;
    }

    public int getSUDSAfter() {
        return SUDSAfter;
    }

    public int getSUDSEnd() {
        return SUDSEnd;
    }

    public String getSymptons() {
        return symptons;
    }

    public String getAlternative() {
        return alternative;
    }

    public String getTableName() {
        return tableName;
    }

    public void setWorrySituation(String worrySituation) {
        this.worrySituation = worrySituation;
    }

    public void setWorstOutcome(String worstOutcome) {
        this.worstOutcome = worstOutcome;
    }

    public void setSUDSPrior(int SUDSPrior) {
        this.SUDSPrior = SUDSPrior;
    }

    public void setSUDSMax(int SUDSMax) {
        this.SUDSMax = SUDSMax;
    }

    public void setSUDSAfter(int SUDSAfter) {
        this.SUDSAfter = SUDSAfter;
    }

    public void setSUDSEnd(int SUDSEnd) {
        this.SUDSEnd = SUDSEnd;
    }

    public void setSymptons(String symptons) {
        this.symptons = symptons;
    }

    public void setAlternative(String alternative) {
        this.alternative = alternative;
    }

}
