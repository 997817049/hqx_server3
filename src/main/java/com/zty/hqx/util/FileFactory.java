package com.zty.hqx.util;

import com.zty.hqx.model.FileModel;

import java.util.HashMap;

/**
 * 文件分块传输时
 * 存储已传输完的和正在传输的视频信息
 */
public class FileFactory {
    //正在传输中的文件《md5value 文件》
    private static final HashMap<String, FileModel> transferMap = new HashMap();

    public static FileModel getFileModel(String md5value) {
        return transferMap.get(md5value);
    }

    public static FileModel setFileModel(String md5value, FileModel fileModel) {
        return transferMap.put(md5value, fileModel);
    }

    public static void removeFileModel(String md5value) {
        transferMap.remove(md5value);
    }

}
