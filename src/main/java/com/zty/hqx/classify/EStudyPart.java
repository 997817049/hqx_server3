package com.zty.hqx.classify;

public enum EStudyPart{
    EXAM(0, "答题", "exam"),
    BOOK(1, "书籍", "book"),
    TELEPLAY(2, "电视", "teleplay"),
    FILM(3, "电影", "film"),
    VARIETY(4, "综艺", "variety"),
    DOCUMENTARY(5, "纪录片", "documentary"),
    DRAMA(6, "戏剧", "drama");

    private int type;    //类型
    private String msg;    //描述
    private String english;    //描述

    EStudyPart(int type, String msg, String english) {
        this.type = type;
        this.msg = msg;
        this.english = english;
    }

    public static EStudyPart getEnumFromString(String string) {
        if (string != null) {
            return Enum.valueOf(EStudyPart.class, string.trim());
        }
        return null;
    }

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public String getEnglish() {
        return english;
    }

    @Override
    public String toString() {
        return "EStudyPart{" +
                "type=" + type +
                ", msg='" + msg + '\'' +
                ", english='" + english + '\'' +
                '}';
    }
}
