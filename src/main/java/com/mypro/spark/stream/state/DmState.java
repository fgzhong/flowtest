package com.mypro.spark.stream.state;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mypro.spark.stream.model.DataBean;
import com.mypro.spark.stream.DataType;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/8/8
 */

public class DmState implements Serializable {

    private final static FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm", Locale.CHINA);
    private final static long OFF_TIME = 1000 * 60 * 5;

    private String web;
    private String achorId;
    private String uid;
    private String version;
    private int isOffline;  // 当为3时，下线
    private Map<String, Integer> userMap = Maps.newHashMap();
    private Map<String, Integer> state = Maps.newHashMap();
    private int count;
    private long lastTime;

    public DmState(String version) {
        this.version = version;
    }

    public void init() {
        if (!state.isEmpty()) {
            long now = System.currentTimeMillis();
            List<String> removeKey = Lists.newArrayList();
            for (Map.Entry<String, Integer> key2value : state.entrySet()) {
                try {
                    if (now - DATE_FORMAT.parse(key2value.getKey()).getTime() > OFF_TIME) {
                        removeKey.add(key2value.getKey());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            for (String key : removeKey) {
                state.remove(key);
            }
        }
    }

    public void add(DataBean dataBean) {
        if (web == null) {
            this.web = dataBean.getWeb();
            this.achorId = dataBean.getAchorId();
            this.uid = dataBean.getUid();
        }
        if (dataBean.getType().equals(DataType.offonline)) {
            isOffline++;
        } else {
            add(dataBean.getTime(), dataBean.getUserId());
        }
    }

    public void add(long time, String userId) {
        count++;
        if (!userMap.containsKey(userId)) {
            userMap.put(userId, 1);
        }
        String timeFormat = DATE_FORMAT.format(time);
        if (state.containsKey(timeFormat)) {
            state.put(timeFormat, state.get(timeFormat)+1);
        } else {
            state.put(timeFormat, 1);
        }
    }

    public boolean isOffline() {
        return isOffline >= 3;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getAchorId() {
        return achorId;
    }

    public void setAchorId(String achorId) {
        this.achorId = achorId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(int isOffline) {
        this.isOffline = isOffline;
    }

    public Map<String, Integer> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, Integer> userMap) {
        this.userMap = userMap;
    }

    public Map<String, Integer> getState() {
        return state;
    }

    public void setState(Map<String, Integer> state) {
        this.state = state;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
