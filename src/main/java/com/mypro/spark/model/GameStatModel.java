package com.mypro.spark.model;

import java.util.Date;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/11
 */
public class GameStatModel {

    private String appId;
    private String quotaId;
    private String source = "rap";
    private String logDate;
    private String result;
    private String fromSource;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(String quotaId) {
        this.quotaId = quotaId;
    }

    public String getSource() {
        return source;
    }

    public String getlogDate() {
        return logDate;
    }

    public void setlogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }
}
