package com.zty.hqx.controller;

import com.zty.hqx.validator.IsMultipartFile;
import com.zty.hqx.model.*;
import com.zty.hqx.service.ResourceService;
import com.zty.hqx.util.FileFactory;
import com.zty.hqx.util.FileUtil;
import com.zty.hqx.util.Md5Util;
import com.zty.hqx.util.ZtyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Objects;

/**
 * 控制台上传视频，图片等  写入磁盘 返回路径
 */
@Controller
@Validated
public class UpLoadController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${hqx.staticUrl}")
    private String staticUrl;
    @Value("${hqx.absoluteStaticUrl}")
    private static String absoluteUrl;

    @Autowired
    ResourceService resourceService;

    /**
     * 上传书籍
     * todo 进行md5校验
     * 随机生成相对路径，写入指定文件夹
     * 生成路径 staticPath = staticUrl + relativePath
     * 将staticPath存入数据库书籍资源表
     * @return staticPath
     */
    @RequestMapping("/upload/book")
    @ResponseBody
    public Result<String> upLoadBook(@NotBlank(message = "md5value不为空")
                                     @RequestParam("md5") String md5,
                                     @RequestParam("book")
                                     @IsMultipartFile MultipartFile file) {
        logger.info("上传book");
        String path = resourceService.isExistBook(md5);
        if(path != null) return Result.success(path);
        try {
            //随机生成一个存储路径 相对路径
            String relativePath = "book/" + ZtyUtil.creatName(Objects.requireNonNull(file.getOriginalFilename()));
            //相对路径 写入文件
            FileUtil.write(relativePath, file.getBytes());
            relativePath = staticUrl + relativePath;
            //数据库存入静态路径
            resourceService.insertBook(new ResourceModel(md5, relativePath));
            return Result.success(relativePath);
        } catch (IOException e) {
            logger.error("获取文件内容失败");
            return Result.error();
        }
    }

    /**
     * 上传html
     * 随机生成相对路径，写入指定文件夹
     * @return staticUrl + 相对路径
     */
    @RequestMapping("/upload/html")
    @ResponseBody
    public Result<String> upLoadHtml(@RequestParam("html")String file) {
        logger.info("上传html");
        //随机生成一个存储路径
        String htmlUrl = "htmls/base/" + ZtyUtil.getRandomName() + ".html";
        //写入文件
        try {
            FileUtil.write(htmlUrl, file.getBytes());
            logger.info("返回图片路径：" + htmlUrl);
            return Result.success(staticUrl + htmlUrl);
        } catch (IOException e) {
            return Result.error();
        }
    }

    /**
     * 上传图片
     * todo 进行md5校验
     * 随机生成相对路径，写入指定文件夹
     * 生成路径 staticPath = staticUrl + relativePath
     * 将staticPath存入数据库书籍资源表
     * @return staticPath
     */
    @RequestMapping("/upload/pic")
    @ResponseBody
    public Result<String> upLoadImage(@NotBlank(message = "md5value不为空")
                                       @RequestParam("md5") String md5,
                                       @RequestParam("pic")
                                       @IsMultipartFile MultipartFile file) {
        logger.info("上传图片" + md5);
        String path = resourceService.isExistPic(md5);
        if(path != null) {
            logger.info("图片已存在：" + path);
            return Result.success(path);
        }
        try {
            path = "ztyImage/" + ZtyUtil.creatName(Objects.requireNonNull(file.getOriginalFilename()));
            FileUtil.write(path, file.getBytes());//在指定路径下写入文件 名称随机 返回存入路径
            path = staticUrl + path;
            resourceService.insertPic(new ResourceModel(md5, path));
            return Result.success(path);
        } catch (IOException e) {
            return Result.error();
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
        String path = resourceService.isExistVideo(md5value);
        if(path != null) {
            System.out.println("视频已存在 路径" + path);
            return Result.success(path);
        }
        FileModel fileModel = FileFactory.getFileModel(md5value);
        //存储正在传输列表
        if (fileModel == null) {
            path = "video/" + ZtyUtil.creatName(name);
            fileModel = new FileModel(chunks, md5value, path);
            FileFactory.setFileModel(md5value, fileModel);
        }
        //将收到的片段存入
        try {
            fileModel.addFile(chunk + "", file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果不完整则返回 完整 则写入
        if(!fileModel.isFull()) return Result.success(null);

        logger.info("开始写入视频" + name);
        //todo 做md5校验
        //写入文件
        try {
            FileUtil.writeVideo(fileModel);
            ResourceModel resourceModel = new ResourceModel(md5value, staticUrl + fileModel.getPath());
            resourceService.insertVideo(resourceModel);
            path = staticUrl + resourceModel.getUrl();
            return Result.success(path);
        } catch (IOException e) {
            return Result.error();
        } finally {
            FileFactory.removeFileModel(md5value);
            logger.info("写入视频结束");
        }
    }
}
