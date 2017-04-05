package org.taoconnect.logs.models;


import org.taoconnect.logs.controllers.ChallengeController;
import org.taoconnect.logs.databases.InitialSchema;

/**
 * Created by croxx219 on 4/5/17.
 */

public class LogAnxietyMonitoring {
    private String tableName = InitialSchema.TABLE_NAME_ANX_MON_LOG;
    private String dateSelected;
    private String timeSelected;
    private int anxietyLevel;
    private String anxietyEvent;
    private String specificWorry;
    private String triggers;
    private String actionTaken;
    private String outcome;

    public String getDateSelected() {
        return dateSelected;
    }

    public String getTimeSelected() {
        return timeSelected;
    }

    public int getAnxietyLevel() {
        return anxietyLevel;
    }

    public String getAnxietyEvent() {
        return anxietyEvent;
    }

    public String getSpecificWorry() {
        return specificWorry;
    }

    public String getTriggers() {
        return triggers;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setDate(String date){
        this.dateSelected = date;
    }
    public void setTime(String time){
        this.timeSelected = time;
    }
    public void setAnxietyLevel(int level){
        this.anxietyLevel = level;
    }
    public void setAnxietyEvent(String event){
        this.anxietyEvent = event;
    }
    public void setSpecificWorry(String worry){
        this.specificWorry = worry;
    }
    public void setTriggers(String triggers){
        this.triggers = triggers;
    }
    public void setActionTaken(String action){
        this.actionTaken = action;
    }
    public void setOutcome(String outcome){
        this.outcome = outcome;
    }

    public String getTableName(){return tableName;}

}
