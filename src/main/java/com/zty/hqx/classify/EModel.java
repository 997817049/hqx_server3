package com.zty.hqx.classify;

public enum EModel {
    MESS(0, "消息"),
    STUDY(1, "学习"),
    NEWS(2, "新闻"),
    BASE(3, "基地"),
    MY(4, "我的");

    private int type;    //类型
    private String msg;    //描述

    EModel(int type, String msg) {
        this.type = type;
        this.msg = msg;
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

    @Override
    public String toString() {
        return "EDocumentaryClassify{" +
                "type=" + type +
                ", msg='" + msg + '\'' +
                '}';
    }
}
