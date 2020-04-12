package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountModel implements Serializable {
    String time;
    int count;

    public CountModel() {
    }

    public CountModel(String time, int count) {
        this.time = time;
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CountModel{" +
                "time='" + time + '\'' +
                ", count=" + count +
                '}';
    }
}
