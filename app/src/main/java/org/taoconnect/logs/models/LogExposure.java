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
    public void insertToDB() {
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

        long newRow = db.insert(getTableName(), null, values);
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
