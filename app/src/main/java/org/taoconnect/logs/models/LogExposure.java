package org.taoconnect.logs.models;

import org.taoconnect.logs.databases.InitialSchema;

/**
 * Created by croxx219 on 4/5/17.
 */

public class LogExposure {
    public String tableName = InitialSchema.TABLE_NAME_EXPOSURE_LOG;
    private String worrySituation;
    private String worstOutcome;
    private int SUDSPrior;
    private int SUDSMax;
    private int SUDSAfter;
    private int SUDSEnd;
    private String symptons;
    private String alternative;

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
