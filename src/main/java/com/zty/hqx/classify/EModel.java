package com.zty.hqx.classify;

public enum EModel {
    SCHOOL(0, "校园", "school"),
    STUDY(1, "study", "study"),
    NEWS(2, "新闻", "news"),
    BASE(3, "base", "base"),
    MY(4, "我的", "my");

    private int type;    //类型
    private String msg;    //描述
    private String english;

    EModel(int type, String msg, String english) {
        this.type = type;
        this.msg = msg;
        this.english = english;
    }

    public static EModel getEnumFromString(String string) {
        if (string != null) {
            return Enum.valueOf(EModel.class, string.trim());
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
        return "EDocumentaryClassify{" +
                "type=" + type +
                ", msg='" + msg + '\'' +
                '}';
    }
}
