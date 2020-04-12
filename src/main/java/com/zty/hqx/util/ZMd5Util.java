package com.zty.hqx.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 对文件进行md5校验
 **/
public class ZMd5Util {
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
        byte buffer[] = new byte[1024];
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
}
