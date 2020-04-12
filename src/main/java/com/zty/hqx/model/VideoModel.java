package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 电视
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoModel implements Serializable {
    private int id;//编号
    private String title;//标题
    private ClassifyModel label;//分类
    private String actor;//演员
    private int num;//集数
    private String synopsis;//简介
    private String picUrl;//图片路径
    private int count;
    private boolean isCollect;//是否被收藏
    private String progress;//进度

    public VideoModel() {
    }

    public VideoModel(String title, ClassifyModel label, String actor, int num, String synopsis, String picUrl) {
        this.title = title;
        this.label = label;
        this.actor = actor;
        this.num = num;
        this.synopsis = synopsis;
        this.picUrl = picUrl;
    }

    public VideoModel(int id, String title, ClassifyModel label, String actor, int num, String synopsis, String picUrl) {
        this.id = id;
        this.title = title;
        this.label = label;
        this.actor = actor;
        this.num = num;
        this.synopsis = synopsis;
        this.picUrl = picUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLabelNum() {
        return label.getNum();
    }

    public ClassifyModel getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = new ClassifyModel(label);
    }

    public void updateClassify(ClassifyModel label) {
        this.label = label;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "VideoModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", label=" + label +
                ", actor='" + actor + '\'' +
                ", num=" + num +
                ", synopsis='" + synopsis + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", isCollect=" + isCollect +
                ", progress='" + progress + '\'' +
                '}';
    }
}
