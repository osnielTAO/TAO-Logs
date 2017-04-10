package org.taoconnect.logs.models;

import org.taoconnect.logs.databases.InitialSchema;
import org.taoconnect.logs.tools.R;

/**
 * Created by croxx219 on 4/5/17.
 */

public class LogChallenge extends LogParent{
    private String tableName = InitialSchema.TABLE_NAME_CHALLENGE_LOG;
    private String dateSelected;
    private String timeSelected;
    private int anxietyLevel;
    private String anxietyEvent;
    private String specificWorry;
    private String actionTaken;
    private String outcome;
    private String assumptions;
    private String challenges;
    private String coreBelieves;
    private String triggers;
    private String alternateView;
    private String[] questions = {"Date of anxiety",
            "Time of Anxiety Event",
            "Anxiety Level",
            "Anxiety Event: Describe the conditions surrounding the event",
            "Specific Worry: describe the worry that arose from the conditions you describe above.",
            "Possible Triggers:",
            "What did you do or not do because of the worry",
            "Outcome of the situation:",
            "Unhealthy Assumptions",
            "Challenges to your Uhealthy Assumptions",
            "Unhealthy Core Beliefs",
            "Alterante way of viewing the situation"};
    private int[] resources={R.layout.datepicker_questionary,
            R.layout.timepicker_questionary,
            R.layout.slider_questionary,
            R.layout.longresponse_questionary,
            R.layout.longresponse_questionary,
            R.layout.short_response_questionary,
            R.layout.short_response_questionary,
            R.layout.short_response_questionary,
            R.layout.multiple_choice_questionary,
            R.layout.short_response_questionary,
            R.layout.multiple_choice_questionary,
            R.layout.short_response_questionary};


    public int[] getResources() {
        return resources;
    }
    public String[] getQuestions() {
        return questions;
    }

    public String getTriggers() {
        return triggers;
    }

    public void setTriggers(String triggers) {
        this.triggers = triggers;
    }

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

    public String getActionTaken() {
        return actionTaken;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getAssumptions() {
        return assumptions;
    }

    public String getChallenges() {
        return challenges;
    }

    public String getCoreBelieves() {
        return coreBelieves;
    }

    public String getAlternateView() {
        return alternateView;
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
    public void setActionTaken(String action){
        this.actionTaken = action;
    }
    public void setOutcome(String outcome){
        this.outcome = outcome;
    }
    public void setAssumptions(String assumptions){
        this.assumptions = assumptions;
    }
    public void setChallenges(String challenges){
        this.challenges = challenges;
    }
    public void setCoreBelieves(String coreBelieves){
        this.coreBelieves = coreBelieves;
    }
    public void setAlternateView(String alternative){
        this.alternateView = alternative;
    }
    public String getTableName(){
        return tableName;
    }
}
