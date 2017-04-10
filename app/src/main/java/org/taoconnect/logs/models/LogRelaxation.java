package org.taoconnect.logs.models;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.tools.R;

/**
 * Created by croxx219 on 4/5/17.
 */

public class LogRelaxation extends LogParent{
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
