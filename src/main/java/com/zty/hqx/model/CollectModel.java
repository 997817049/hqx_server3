package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectModel implements Serializable {
    private int no;
    private int userId;
    private EModel model;
    private EStudyPart part;
    private int id;
    private String progress;

    public CollectModel() {
    }

    public CollectModel(int userId, EModel model, EStudyPart part, int id, String progress) {
        this.userId = userId;
        this.model = model;
        this.part = part;
        this.id = id;
        this.progress = progress;
    }

    public CollectModel(int userId, int model, int part, int id, String progress) {
        this.userId = userId;
        this.model = EModel.values()[model];
        this.part = EStudyPart.values()[part];
        this.id = id;
        this.progress = progress;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getModel() {
        return model.getType();
    }

    public void setModel(int model) {
        this.model = EModel.values()[model];
    }

    public int getPart() {
        return part.getType();
    }

    public void setPart(EStudyPart part) {
        this.part = part;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "CollectModel{" +
                "userId=" + userId +
                ", part=" + part +
                ", id=" + id +
                ", progress='" + progress + '\'' +
                '}';
    }
}
