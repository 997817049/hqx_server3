package com.zty.hqx.model;

import java.util.HashMap;

/**
 * 视频文件分块传输，使用该类进行临时存储
 */
public class FileModel {
    //块编号 文件
    private final HashMap<String, byte[]> map = new HashMap<>();
    private int chunks;//分块数
    private String md5value;
    private String path;//文件存储的相对路径 + 新生成的文件名称  =  path + id.type/集数.type

    public FileModel() {
    }

    public FileModel(int chunks, String md5value, String path) {
        this.chunks = chunks;
        this.md5value = md5value;
        this.path = path;
    }

    public int getChunks() {
        return chunks;
    }

    public String getPath() {
        return path;
    }

    public byte[] getFile(String name) {
        return map.get(name);
    }

    public void addFile(String name, byte[] file) {
        map.put(name, file);
    }

    public boolean isFull() {
        return (map.size() == chunks);
    }

    @Override
    public String toString() {
        return "FileModel{" +
                ", chunks=" + chunks +
                ", md5value='" + md5value + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
