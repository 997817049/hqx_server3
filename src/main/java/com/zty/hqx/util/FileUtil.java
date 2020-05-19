package com.zty.hqx.util;

import com.zty.hqx.model.FileModel;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

/**
 * 进行文件操作 写入或删除
 * */
public class FileUtil {
    @Value("${hqx.staticUrl}")
    private String staticUrl;

//    @Value("${hqx.absoluteStaticUrl}")
    private static final String rootPath = "/funyoo_project/hqx_app/hqx_static/";

    public static void writeVideo(FileModel fileModel) throws IOException {
        File file = new File(rootPath + fileModel.getPath());
        System.out.println("写入视频的路径：" + rootPath + fileModel.getPath());
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        } else if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file, true);

        for (int i = 0; i < fileModel.getChunks(); i++) {
            byte[] buf = fileModel.getFile(i + "");
            System.out.println(buf);
            fos.write(buf);
        }
        fos.close();
    }

    /**
     * @param path  相对路径
     * @param bytes 文件内容
     * @return 写入的文件的相对路径
     */
    public static void write(String path, byte[] bytes) throws IOException {
        //生成该路径
        File file = new File(rootPath + path);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        //写入文件
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bytes);
        fos.close();
    }

    /**
     * 保存字节流至文件
     * @param filePath 文件
     * @param bytes   字节流
     * @return        文件全路径
     */
    public static String saveFileByBytes(String filePath, byte[] bytes) {
        File targetFile = new File(filePath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return filePath;
    }

    /**
     * 删除指定URL的文件
     * @param filePath 文件路径
     * @return
     */
    public static boolean removeFile(String filePath) {
        File targetFile = new File((filePath));
        if (targetFile.exists()) {
            return targetFile.delete();
        }
        return false;
    }

    /**
     * 删除指定文件
     * */
    public static boolean deleteFile(String path){
        File file = new File(path);// 读取
        //exists 文件或文件夹
        //isFile() 存在&只能是文件
        if(!file.isFile()){
            System.out.println("文件不存在:" + file);
            return false;
        }
        return file.delete();// 删除
    }
}
