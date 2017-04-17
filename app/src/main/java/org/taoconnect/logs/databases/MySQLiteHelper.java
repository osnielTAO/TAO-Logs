package org.taoconnect.logs.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by croxx219 on 4/3/17.
 */

public final class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InitialSchema.TABLE_NAME_ANX_MON_LOG  +
            "; DROP TABLE IF EXISTS " + InitialSchema.TABLE_NAME_EXPOSURE_LOG +
            "; DROP TABLE IF EXISTS " + InitialSchema.TABLE_NAME_RELAX_LOG+
            "; DROP TABLE IF EXISTS " + InitialSchema.TABLE_NAME_CHALLENGE_LOG;

        // If you change the database schema, you must increment the database version.
   // private FeedReaderDbHelper mHelper = new FeedReaderDbHelper(getContext());
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "10.db";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(InitialSchema.CREATE_EXPOSURE_LOG);
        db.execSQL(InitialSchema.CREATE_RELAX_LOG);
        db.execSQL(InitialSchema.CREATE_CHALLENGE_LOG);
        db.execSQL(InitialSchema.CREATE_ANX_MON_LOG);
        db.execSQL(InitialSchema.CREATE_ANX_MON_LOG_TMP);
        db.execSQL(InitialSchema.CREATE_CHALLENGE_LOG_TMP);
        db.execSQL(InitialSchema.CREATE_EXPOSURE_LOG_TMP);
        db.execSQL(InitialSchema.CREATE_RELAX_LOG_TEMP);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
