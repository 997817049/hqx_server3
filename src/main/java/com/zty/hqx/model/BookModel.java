package com.zty.hqx.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 书籍和电影
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookModel implements Serializable {
    private int id;//编号
    private String title;//标题
    private ClassifyModel label;//分类
    private String author;//作者
    private String synopsis;//简介
    private String picUrl;//图片路径
    private String fileUrl;//外链
    private int count;
    private boolean isCollect;//是否被收藏
    private String progress;//进度

    public BookModel(String title, ClassifyModel label, String author, String synopsis, String picUrl, String fileUrl) {
        this.title = title;
        this.label = label;
        this.author = author;
        this.synopsis = synopsis;
        this.picUrl = picUrl;
        this.fileUrl = fileUrl;
    }

    public BookModel(int id, String title, ClassifyModel label, String author, String synopsis, String picUrl, String fileUrl) {
        this.id = id;
        this.title = title;
        this.label = label;
        this.author = author;
        this.synopsis = synopsis;
        this.picUrl = picUrl;
        this.fileUrl = fileUrl;
    }

    public BookModel() {
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

    public ClassifyModel getLabel() {
        return label;
    }

    public int getLabelNum() {
        return label.getNum();
    }

//    public void setLabel(int label) {
//        this.label = new ClassifyModel(label);
//    }

    public void setLabel(ClassifyModel label) {
        this.label = label;
    }

    public void updateClassify(ClassifyModel label) {
        this.label = label;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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
        return "BookModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", label=" + label +
                ", author='" + author + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", isCollect=" + isCollect +
                ", progress='" + progress + '\'' +
                '}';
    }
}
