package com.zty.hqx.controller;

import com.zty.hqx.annotation.IsMultipartFile;
import com.zty.hqx.classify.CodeMsg;
import com.zty.hqx.model.*;
import com.zty.hqx.service.ArticleWriteSevice;
import com.zty.hqx.service.ResourceService;
import com.zty.hqx.util.FileFactory;
import com.zty.hqx.util.FileUtil;
import com.zty.hqx.util.IDUtil;
import com.zty.hqx.util.ZtyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.sql.Date;
import java.util.Objects;

/**
 * 控制台上传视频，图片等  写入磁盘 返回路径
 */
@Controller
public class UpLoadController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${hqx.staticUrl}")
    private String staticUrl;

    @Value("${hqx.absoluteStaticUrl}")
    private String absolutePath;

    @Autowired
    public ArticleWriteSevice writeSevice;

    @Autowired
    ResourceService resourceService;

    /**
     * 上传html
     * @return 存储的相对路径
     */
    @RequestMapping("/upload/book")
    @ResponseBody
    public Result<String> upLoadBook(@NotBlank(message = "md5value不为空")  @RequestParam("md5") String md5,
                                     @RequestParam("book")MultipartFile file) {
        logger.info("上传book");
        //随机生成一个存储路径 相对路径
        String path = "book\\" + ZtyUtil.creatName(Objects.requireNonNull(file.getOriginalFilename()));
        //写入文件
        try {
            //相对路径 写入
            FileUtil.write(path, file.getBytes());
            //数据库存入静态路径
            path = staticUrl + ZtyUtil.dealPathToSql(path);
            resourceService.insertBook(new ResourceModel(md5, path));
        } catch (IOException e) {
            path = null;
        }
//        返回绝对路径
        if(path == null){
            return Result.error();
        } else {
            return Result.success(path);
        }
    }

    /**
     * 上传html
     * @return 存储的相对路径
     */
    @RequestMapping("/upload/html")
    @ResponseBody
    public Result<String> upLoadHtml(@RequestParam("html")String file) {
        logger.info("上传html");
        //随机生成一个存储路径
        String htmlUrl = "htmls\\base\\" + ZtyUtil.getRandomName() + ".html";
        //写入文件
        try {
            FileUtil.write(htmlUrl, file.getBytes());
            System.out.println("返回图片路径：" + htmlUrl);
            htmlUrl = staticUrl + ZtyUtil.dealPathToSql(htmlUrl);
        } catch (IOException e) {
            htmlUrl = null;
        }
        System.out.println("返回html路径：" + htmlUrl);
        if(htmlUrl == null){
            return Result.error();
        } else {
            return Result.success(htmlUrl);
        }
    }

    /**
     * 上传图片
     * @return 存储的相对路径
     */
    @RequestMapping("/upload/pic")
    @ResponseBody
    public Result<String> upLoadImage(@NotBlank(message = "md5value不为空")  @RequestParam("md5") String md5,
                               @RequestParam("pic") MultipartFile file) {
        logger.info("上传图片" + md5);
        if(md5.length() < 32) return Result.error();
        String path = resourceService.isExistPic(md5);
        if(path == null){
            try {
                path = "ztyImage\\" + ZtyUtil.creatName(Objects.requireNonNull(file.getOriginalFilename()));
                FileUtil.write(path, file.getBytes());//在指定路径下写入文件 名称随机 返回存入路径
                path = staticUrl + ZtyUtil.dealPathToSql(path);
                //http://49.4.114.114:81/hqx_static/ztyImage/
                resourceService.insertPic(new ResourceModel(md5, path));
            } catch (IOException e) {
                e.printStackTrace();
                path = null;
            }
        }
        System.out.println("返回图片路径：" + path);
        if(path == null){
            return Result.error();
        } else {
            return Result.success(path);
        }
    }

    /**
     * 接收电影视频资源块 存储 当接收完毕后合并写入文件
     *
     * @param chunks   块数
     * @param md5value 整个文件的md5校验值
     * @param chunk    当前块的块编号
     * @param name     哪一集
     */
    @RequestMapping("/upload/video")
    @ResponseBody
    public Result<String> upLoadVideo(String name, int chunks, int chunk,
                              @NotBlank(message = "md5value不为空") String md5value,
                              @IsMultipartFile MultipartFile file) {
        logger.info("收到视频《" + md5value + "(" + md5value + ")》的第" + chunk + "块文件");
        if(md5value == null || md5value.length() < 32) return Result.error();
        String path = resourceService.isExistVideo(md5value);
        if(path != null) {
            System.out.println("视频已存在 路径" + path);
            return Result.success(path);
        }
        FileModel fileModel = FileFactory.getFileModel(md5value);
        //存储正在传输列表
        if (fileModel == null) {
            path = "video\\" + ZtyUtil.creatName(name);
            fileModel = new FileModel(chunks, md5value, path);
            FileFactory.setFileModel(md5value, fileModel);
        }
        //将收到的片段存入
        try {
            fileModel.addFile(chunk + "", file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果完整 则写入
        if (fileModel.isFull()) {
            logger.info("开始写入视频" + name);
            //todo 做md5校验
            //写入文件
            try {
                FileUtil.writeVideo(fileModel);
                ResourceModel resourceModel = new ResourceModel(md5value, fileModel.getPath());
                resourceService.insertVideo(resourceModel);
                path = staticUrl + ZtyUtil.dealPathToSql(resourceModel.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
                path = null;
            } finally {
                FileFactory.removeFileModel(md5value);
                logger.info("写入视频结束");
            }
        }
        //http://49.4.114.114:81/hqx_static/video/
        System.out.println("视频路径" + path);
        if(path == null){
            return Result.error();
        } else {
            return Result.success(path);
        }
    }

// -----------------------------------------资源上传 fy-----------------------------------------------

    /**
     * 生成并上传HTML文章 并 保存文件信息至数据库
     * @param html       HTML
     * @param part       模块名
     * @param title      文章标题
     * @param type       文章类型
     * @param recommend  推荐形式
     * @param picUrl     图片路径
     * @return
     */
    @RequestMapping("/hqx_update/html")
    @ResponseBody
    public Result<String> setHtmlUrl(String html, String part, String title, String type, Integer recommend, String picUrl) {
        String fileName = IDUtil.getIdByTimeAnd3Random() + ".html";
        String filePath = absolutePath + part + "/html/" + fileName;
        String fileUrl = staticUrl + part + "/html/" + fileName;
        // result包含的是文件全路径名
        String path = FileUtil.saveFileByBytes(filePath, html.getBytes());
        if (path != null) {
            // 生成文章实例
            Article article = new Article();
            article.setTitle(title);
            article.setType(type);
            article.setRecommend(recommend);
            article.setHtmlUrl(fileUrl);
            article.setPicUrl(picUrl);
            article.setDate(new Date(System.currentTimeMillis()).toString());
            // 保存文章信息至数据库 存储进数据库的url应该是静态资源全url
            boolean write = writeSevice.saveArticle(part, article);
            if (!write) {
                boolean remove = FileUtil.removeFile(filePath);
                // 删除失败 记录 TODO
                return Result.error(CodeMsg.DATABASE_UNKNOW_ERROR);
            }
        }
        return Result.success(fileUrl);
    }

    /**
     * 上传图片
     * @param file  图片文件
     * @param part  模块名称
     * @return      包含图片路径
     */
    @RequestMapping("/hqx_update/image")
    @ResponseBody
    public ImgInfo setImgUrl(@RequestParam("myFile") MultipartFile file, String part) {
        String originalFilename = file.getOriginalFilename();
        String tail = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = IDUtil.getIdByTimeAnd3Random() + tail;
        String filePath = absolutePath + part + "/image/" + fileName;
        String fileUrl = staticUrl + part + "/image/" + fileName;

        String path = null;
        try {
            path = FileUtil.saveFileByBytes(filePath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return new ImgInfo(1001, new String[]{"文件上传错误 file update error"});
        }

        String[] values = {fileUrl};

        ImgInfo imgInfo = new ImgInfo(0, values);

        return imgInfo;
    }

}
