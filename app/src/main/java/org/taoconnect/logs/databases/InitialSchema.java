package org.taoconnect.logs.databases;

import android.provider.BaseColumns;

/**
 * Created by croxx219 on 4/5/17.
 */

public class InitialSchema implements BaseColumns {

    /*******************ANXIETY LOGS TABLES*************************************/
    public static final String TABLE_NAME_ANX_MON_LOG = "anxietyMonitoringLog";
    public static final String DATE_SELECTED = "dateAnxiety";
    public static final String TIME_SELECTED = "timeAnxiety";
    public static final String ANXIETY_LEVEL = "levelAnxiety";
    public static final String ANXIETY_EVENT = "eventAnxiety";
    public static final String SPECIFIC_WORRY = "specificWorry";
    public static final String TRIGGERS = "possibleTriggers";
    public static final String ACTION_TAKEN = "actionTaken";
    public static final String OUTCOME = "outcome";
    public static final String TIMESTAMP = "timestamp";
    public static final String CREATE_ANX_MON_LOG  = "CREATE TABLE " + InitialSchema.TABLE_NAME_ANX_MON_LOG
                                                    + "( " + InitialSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                    + InitialSchema.DATE_SELECTED  +  " TEXT,"
                                                    + InitialSchema.TIME_SELECTED + " TEXT,"
                                                    + InitialSchema.ANXIETY_LEVEL + " TEXT,"
                                                    + InitialSchema.ANXIETY_EVENT + " TEXT,"
                                                    + InitialSchema.SPECIFIC_WORRY + " TEXT,"
                                                    + InitialSchema.TRIGGERS + " TEXT,"
                                                    + InitialSchema.ACTION_TAKEN + " TEXT,"
                                                    + InitialSchema.OUTCOME + " TEXT,"
                                                    + InitialSchema.TIMESTAMP + " TEXT)";
    public static final String CREATE_ANX_MON_LOG_TMP = "CREATE TABLE " + InitialSchema.TABLE_NAME_ANX_MON_LOG + "Temp "
                                                    + "( " + InitialSchema._ID + " INTEGER PRIMARY KEY, "
                                                    + InitialSchema.TIME_SELECTED + " TEXT,"
                                                    + InitialSchema.ANXIETY_LEVEL + " TEXT,"
                                                    + InitialSchema.ANXIETY_EVENT + " TEXT,"
                                                    + InitialSchema.SPECIFIC_WORRY + " TEXT,"
                                                    + InitialSchema.TRIGGERS + " TEXT,"
                                                    + InitialSchema.ACTION_TAKEN + " TEXT,"
                                                    + InitialSchema.OUTCOME + " TEXT,"
                                                    + InitialSchema.TIMESTAMP + " TEXT)";
    public static final String TABLE_NAME_RELAX_LOG= "relaxationLog";
    public static final String THOUGHTS_BEFORE = "thoughtsBefore";
    /** Reuse ANXIETY_LEVEL **/
    public static final String RELAXATION_EXERCISE = "relaxationExercise";
    public static final String CREATE_RELAX_LOG = "CREATE TABLE " + InitialSchema.TABLE_NAME_RELAX_LOG
                                                    + "( "  + InitialSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                    + InitialSchema.THOUGHTS_BEFORE +  " TEXT,"
                                                    + InitialSchema.ANXIETY_LEVEL + " TEXT,"
                                                    + InitialSchema.RELAXATION_EXERCISE + " TEXT,"
                                                    + InitialSchema.TIMESTAMP + " TEXT)";
    public static final String CREATE_RELAX_LOG_TEMP = "CREATE TABLE " + InitialSchema.TABLE_NAME_RELAX_LOG + "Temp "
                                                        + "( "+ InitialSchema._ID + " INTEGER PRIMARY KEY, "
                                                        + InitialSchema.THOUGHTS_BEFORE +  " TEXT,"
                                                        + InitialSchema.ANXIETY_LEVEL + " TEXT,"
                                                        + InitialSchema.RELAXATION_EXERCISE + " TEXT,"
                                                        + InitialSchema.TIMESTAMP + " TEXT)";

    public static final String TABLE_NAME_CHALLENGE_LOG = "challengeLog";
    /** Reuse DATE_SELECTED, TIME_SELECTED, ANXIETY_LEVEL, ANXIETY_EVENT, SPECIFIC_WORRY, TRIGGERS,
     * ACTION_TAKEN, OUTCOME
     */
    public static final String UNH_ASSUMPTIONS = "unhealthyAssumptions";
    public static final String CHALLENGES = "challengesToAssumptions";
    public static final String CORE_BELIEVES = "unhealthyCoreBeliefs";
    public static final String ALTERNATE_VIEW = "alternateView";
    public static final String CREATE_CHALLENGE_LOG  = "CREATE TABLE " + InitialSchema.TABLE_NAME_CHALLENGE_LOG
                                                    + "( " + InitialSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                    + InitialSchema.DATE_SELECTED  +  " TEXT,"
                                                    + InitialSchema.TIME_SELECTED + " TEXT,"
                                                    + InitialSchema.ANXIETY_LEVEL + " TEXT,"
                                                    + InitialSchema.ANXIETY_EVENT + " TEXT,"
                                                    + InitialSchema.SPECIFIC_WORRY + " TEXT,"
                                                    + InitialSchema.TRIGGERS + " TEXT,"
                                                    + InitialSchema.ACTION_TAKEN + "TEXT,"
                                                    + InitialSchema.OUTCOME + " TEXT,"
                                                    + InitialSchema.UNH_ASSUMPTIONS + " TEXT,"
                                                    + InitialSchema.CHALLENGES + " TEXT,"
                                                    + InitialSchema.CORE_BELIEVES + " TEXT,"
                                                    + InitialSchema.ALTERNATE_VIEW + " TEXT,"
                                                    + InitialSchema.TIMESTAMP + " TEXT)";
    public static final String CREATE_CHALLENGE_LOG_TMP  = "CREATE TABLE " + InitialSchema.TABLE_NAME_CHALLENGE_LOG + "Temp "
                                                    + "( " + InitialSchema._ID + " INTEGER PRIMARY KEY, "
                                                    + InitialSchema.DATE_SELECTED  +  " TEXT,"
                                                    + InitialSchema.TIME_SELECTED + " TEXT,"
                                                    + InitialSchema.ANXIETY_LEVEL + " TEXT,"
                                                    + InitialSchema.ANXIETY_EVENT + " TEXT,"
                                                    + InitialSchema.SPECIFIC_WORRY + " TEXT,"
                                                    + InitialSchema.TRIGGERS + " TEXT,"
                                                    + InitialSchema.ACTION_TAKEN + "TEXT,"
                                                    + InitialSchema.OUTCOME + " TEXT,"
                                                    + InitialSchema.UNH_ASSUMPTIONS + " TEXT,"
                                                    + InitialSchema.CHALLENGES + " TEXT,"
                                                    + InitialSchema.CORE_BELIEVES + " TEXT,"
                                                    + InitialSchema.ALTERNATE_VIEW + " TEXT,"
                                                    + InitialSchema.TIMESTAMP + " TEXT)";
    public static final String TABLE_NAME_EXPOSURE_LOG = "exposureLog";
    public static final String WORRY_SITUATION = "worrySituation";
    public static final String WORST_OUTCOME = "worstOutcome";
    public static final String SUDS_PRIOR = "SUDSPrior";
    public static final String SUDS_MAX = "SUDSMax";
    public static final String SUDS_AFTER = "SUDSAfter";
    public static final String SUDS_END = "SUDSEnd";
    public static final String SYMPTONS_DURING = "symptonsExercise";
    public static final String ALTERNATIVE_OUTCOMES = "alernativeOutcomes";
    public static final String CREATE_EXPOSURE_LOG = "CREATE TABLE " + InitialSchema.TABLE_NAME_EXPOSURE_LOG
                                                    + "( " + InitialSchema._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                    + InitialSchema.WORRY_SITUATION +  " TEXT,"
                                                    + InitialSchema.WORST_OUTCOME + " TEXT,"
                                                    + InitialSchema.SUDS_PRIOR + " TEXT,"
                                                    + InitialSchema.SUDS_MAX + " TEXT,"
                                                    + InitialSchema.SUDS_AFTER+ " TEXT,"
                                                    + InitialSchema.SUDS_END + " TEXT,"
                                                    + InitialSchema.SYMPTONS_DURING + " TEXT,"
                                                    + InitialSchema.ALTERNATIVE_OUTCOMES + " TEXT,"
                                                    + InitialSchema.TIMESTAMP + " TEXT)";
    public static final String CREATE_EXPOSURE_LOG_TMP = "CREATE TABLE " + InitialSchema.TABLE_NAME_EXPOSURE_LOG + "Temp "
                                                    + "( " + InitialSchema._ID + " INTEGER PRIMARY KEY, "
                                                    + InitialSchema.WORRY_SITUATION +  " TEXT,"
                                                    + InitialSchema.WORST_OUTCOME + " TEXT,"
                                                    + InitialSchema.SUDS_PRIOR + " TEXT,"
                                                    + InitialSchema.SUDS_MAX + " TEXT,"
                                                    + InitialSchema.SUDS_AFTER+ " TEXT,"
                                                    + InitialSchema.SUDS_END + " TEXT,"
                                                    + InitialSchema.SYMPTONS_DURING + " TEXT,"
                                                    + InitialSchema.ALTERNATIVE_OUTCOMES + " TEXT,"
                                                    + InitialSchema.TIMESTAMP + " TEXT)";
    /******************************* END ******************************************/


    //TODO: Define schema of these remaining tables
    public static final String TABLE_NAME_FEELING_LOG= "feelingLog";

    public static final String TABLE_NAME_ACTIVATION_LOG = "activationLog";

    public static final String TABLE_NAME_RUMINATION_LOG = "ruminationRecordLog";

    public static final String TABLE_NAME_LETTING_GO_LOG = "lettingGoLog";

    public static final String TABLE_NAME_MINDULNESS_LOG = "mindfulnessLog";

    public static final String TABLE_NAME_RELAXATIONBEH_LOG = "relaxationBehLog";
}
