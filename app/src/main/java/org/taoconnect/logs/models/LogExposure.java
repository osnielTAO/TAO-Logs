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

    private final String INIT_TEMP = "CREATE TABLE IF NOT EXISTS " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp "
            + "( " + InitialSchema._ID + " INTEGER PRIMARY KEY, "
            + InitialSchema.WORRY_SITUATION +  " TEXT,"
            + InitialSchema.WORST_OUTCOME + " TEXT,"
            + InitialSchema.SUDS_PRIOR + " INTEGER,"
            + InitialSchema.SUDS_MAX + " INTEGER,"
            + InitialSchema.SUDS_AFTER+ " INTEGER,"
            + InitialSchema.SUDS_END + " INTEGER,"
            + InitialSchema.SYMPTONS_DURING + " TEXT,"
            + InitialSchema.ALTERNATIVE_OUTCOMES + " TEXT)";

    private final String COPY_TABLE_TO_PERMANENT = "INSERT INTO " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + " SELECT * FROM " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp ";

    private final String DROP_TEMP_TABLE = "DROP TABLE IF EXISTS " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp ";

    public int[] getResources() {
        return resources;
    }
    public String[] getQuestions() {
        return questions;
    }
    public LogExposure(Context context) {
        this.context = context;
    }

    @Override
    public void insertToTempDB() {
        MySQLiteHelper mHelper = new MySQLiteHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();

        db.execSQL(INIT_TEMP);
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

        db.insert(InitialSchema.TABLE_NAME_EXPOSURE_LOG,null,values);
        db.execSQL(DROP_TEMP_TABLE);
        db.close();
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
