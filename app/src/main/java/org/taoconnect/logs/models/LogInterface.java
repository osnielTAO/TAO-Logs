package org.taoconnect.logs.models;

import java.util.ArrayList;

/**
 * This is an interface for all the models to use. It gives the common method to be used by each model. Gets called in Questionary.java
 */

interface LogInterface {
    int[] getResources() ;  // Returns the layouts in the object
    String[] getQuestions() ;
    void insertToTempDB();
    void insertToPermanentDB();
    boolean hasTempTable(); // Used to add the continue option in LogPicker
    ArrayList<String> getColValues(); // Gets the values from the temp table to fill out the views when continue gets called
    long getTimestampDB(); // Gets the timestamp from the temporal table
}
