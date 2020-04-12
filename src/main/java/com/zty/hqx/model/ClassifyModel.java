package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClassifyModel implements Serializable {
    private int num;    //类型
    private String msg;    //描述
    private String english;    //描述

    public ClassifyModel(int num, String msg, String english) {
        this.num = num;
        this.msg = msg;
        this.english = english;
    }

    public ClassifyModel(int num) {
        this.num = num;
    }

    public ClassifyModel() {
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
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
