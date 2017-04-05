package org.taoconnect.logs.models;

import org.taoconnect.logs.databases.InitialSchema;

/**
 * Created by croxx219 on 4/5/17.
 */

public class LogRelaxation {
    private String thoughts;
    private String relaxationExercise;
    private int anxietyLevel;
    private String tableName = InitialSchema.TABLE_NAME_RELAX_LOG;

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
