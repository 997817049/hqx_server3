package com.zty.hqx.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 对文件进行md5校验
 **/
public class Md5Util {
    private static final String salt = "97funyoo23";

    /**
     * 获取指定路径的文件的md5校验值
     * @param path 文件路径
     * @return
     * */
    public static String getFileMD5(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return null;
        }
        // 创建MessageDigest对象，添加MD5处理
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            // 读取图片
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger bigInt = new BigInteger(1, digest.digest());
        // 返回16进制表示形式
        return bigInt.toString(16);
    }

    public static String getFileMD5(byte[] file) {
        // 创建MessageDigest对象，添加MD5处理
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(file, 0, file.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        // 返回16进制表示形式
        return bigInt.toString(16);
    }

    /**
     * 对密码进行md5
     * */
    public static String formPassToDBPass(String formPass) {
        // 取余运算 防止下标越界
        int length = salt.length();
        String str = "" + salt.charAt(5 % length) + salt.charAt(0)
                + formPass + salt.charAt(4 % length) + salt.charAt(7 % length);
        return md5(str);
    }

    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }
}
