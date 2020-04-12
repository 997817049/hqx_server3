package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 试卷
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamModel implements Serializable {
    private int id;
    private String title;
    private ClassifyModel label;
    private int count;//题量
    private int time;
    private boolean isCollect;//是否做过
    private String progress;//成绩

    public ExamModel(String title, ClassifyModel label, int time) {
        this.title = title;
        this.label = label;
        this.time = time;
    }

    public ExamModel(int id, String title, ClassifyModel label, int time) {
        this.id = id;
        this.title = title;
        this.label = label;
        this.time = time;
    }

    public ExamModel() {
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

    public void setLabel(int label) {
        this.label = new ClassifyModel(label);
    }

    public void updateClassify(ClassifyModel label) {
        this.label = label;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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
        return "ExamModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", label=" + label +
                ", count=" + count +
                ", time=" + time +
                ", isCollect=" + isCollect +
                ", progress='" + progress + '\'' +
                '}';
    }
}
