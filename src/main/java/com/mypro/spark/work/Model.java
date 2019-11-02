package com.mypro.spark.work;

import java.io.Serializable;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/3/25
 */
public class Model implements Serializable {

    private static final long serialVersionUID = 10082938294703L;
    private String appId;
    private String quotaId;
    private String source = "rap";
    private String log_date;
    private Double result;
    private String from_source;

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

    public String getLog_date() {
        return log_date;
    }

    public void setLog_date(String log_date) {
        this.log_date = log_date;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public String getFrom_source() {
        return from_source;
    }

    public void setFrom_source(String from_source) {
        this.from_source = from_source;
    }

}
