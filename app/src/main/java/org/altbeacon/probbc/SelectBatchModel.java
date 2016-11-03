package org.altbeacon.probbc;

/**
 * Created by Harshad Shinde on 29-01-2016.
 */

public class SelectBatchModel {
    private String batchName;
    private String batchId;
    private String batchAlias;
    private String schoolName;

    public SelectBatchModel(String batchName, String batchId, String batchAlias, String schoolName) {
        this.batchName = batchName;
        this.batchId = batchId;
        this.batchAlias = batchAlias;
        this.schoolName = schoolName;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getBatchAlias() {
        return batchAlias;
    }

    public void setBatchAlias(String batchAlias) {
        this.batchAlias = batchAlias;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
