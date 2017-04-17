package org.taoconnect.logs.models;

import android.database.sqlite.SQLiteDatabase;

/**
 * This is an interface for all the models to use. It gives the common method to be used by each model. Gets calle in Questionary.java
 */

interface LogInterface {
    int[] getResources() ;
    String[] getQuestions() ;
    void insertToTempDB();
    void insertToPermanentDB();
    boolean hasTempTable();
}
