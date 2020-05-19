package com.zty.hqx.classify;

import java.io.Serializable;

public enum ECodeMsg implements Serializable {
    NO_DATA (1, "没有找到数据！"),
    PARA_EXCEPTION(2,"参数异常"),
    SQL_EXCEPTION(3, "数据库异常");

    private int code;
    private String msg;

    ECodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ECodeMsg getEnumFromString(String string) {
        if (string != null) {
            return Enum.valueOf(ECodeMsg.class, string.trim());
        }
        return null;
    }

    @Override
    public String toString() {
        return "ECodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
