package com.mypro.spark.stream.model;

import java.io.Serializable;

/**
 * @author fgzhong
 * @description: ${description}
 * @since 2019/8/21
 */
public class UnionKey implements Serializable {

    private final static int OUT_TIME = 1000 * 60;

    private int version;
    private String uid;
    private String hash;
    private Long time;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean equals(UnionKey other) {
        if (this.version != other.getVersion()) {
            if (this.getUid().equals(other.getUid())) {
                if (this.getHash().equals(other.getHash())) {
                    long t = this.getTime() - other.getTime();
                    if (-1 * OUT_TIME < t && t < OUT_TIME) {
                        return true;
                    }
                }
            }
        } else if (this.getUid().equals(other.getUid())
                    && this.getTime().equals(other.getTime())
                    && this.getHash().equals(other.getHash())) {
                return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (uid+hash).hashCode();
    }
}
