package com.zty.hqx.model;

/**
 * 电视中每一集视频
 */
public class VideoContentModel {
    private int num;//集数
    private String videoUrl;//视频路径

    public VideoContentModel() {
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public String toString() {
        return "VideoContentModel{" +
                "num=" + num +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
