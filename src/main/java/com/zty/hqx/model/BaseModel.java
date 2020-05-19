package com.zty.hqx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Objects;

/**
 * base
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseModel implements Serializable {
    private int id;//编号
    private String title;//标题
    private String picUrl;//图片路径
    private String htmlUrl;//界面路径
    private String province;//省
    private String city;//市
    private int count;
    private boolean isCollect;//是否被收藏

    public BaseModel() {
    }

    public BaseModel(int id, String title, String province, String city, String picUrl, String htmlUrl) {
        this.id = id;
        this.picUrl = picUrl;
        this.title = title;
        this.htmlUrl = htmlUrl;
        this.province = province;
        this.city = city;
        this.count = 0;
    }

    public int getId() {
        return id;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public int getCount() {
        return count;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel baseModel = (BaseModel) o;
        return id == baseModel.id &&
                isCollect == baseModel.isCollect &&
                Objects.equals(title, baseModel.title) &&
                Objects.equals(picUrl, baseModel.picUrl) &&
                Objects.equals(htmlUrl, baseModel.htmlUrl) &&
                Objects.equals(province, baseModel.province) &&
                Objects.equals(city, baseModel.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, picUrl, htmlUrl, province, city, isCollect);
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", count=" + count +
                ", isCollect=" + isCollect +
                '}';
    }
}
