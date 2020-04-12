package com.zty.hqx.classify;

import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 试题分类
 */
@JSONType(serializeEnumAsJavaBean = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EQuestionClassify {
    FILL_BLANKS(0, "填空题"),
    SINGLE_CHOICE(1, "单选题"),
    MULTIPLE_CHOICE(2, "多选题"),
    ESSAY(3, "论述题");

    private int type;    //类型
    private String msg;    //描述

    EQuestionClassify(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public int getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
