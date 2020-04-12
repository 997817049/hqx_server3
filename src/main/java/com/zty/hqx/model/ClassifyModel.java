package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassifyModel implements Serializable {
    private Integer num;    //类型
    private String msg;    //描述
    private String english;    //描述

    public ClassifyModel(Integer num, String msg, String english) {
        this.num = num;
        this.msg = msg;
        this.english = english;
    }

    public ClassifyModel(Integer num) {
        this.num = num;
    }

    public ClassifyModel() {
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    @Override
    public String toString() {
        return "ClassifyModel{" +
                "num=" + num +
                ", msg='" + msg + '\'' +
                ", english='" + english + '\'' +
                '}';
    }
}
