package com.zty.hqx.controller;

import com.zty.hqx.model.BaseModel;
import com.zty.hqx.model.BookModel;
import com.zty.hqx.model.Result;
import com.zty.hqx.model.VideoModel;
import com.zty.hqx.service.HistoryService;
import com.zty.hqx.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 处理历史记录
 * */
@Controller
public class HistoryController {
    @Autowired
    HistoryService historyService;
    @Autowired
    RedisUtil redisUtil;

    @RequestMapping(value = "/history/study/video")
    @ResponseBody
    public Result<List<VideoModel>> getVideoHistory(int userId, String part, int num, int limit) {
        String redisKey = "hqx:history:" + part + ":" + userId + ":num_" + num + "_limit_" + limit;
        //redis获取值
        Result<List<VideoModel>> rs = (Result<List<VideoModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<VideoModel> list = historyService.getVideoHistory(userId, part, num, limit);
        rs = Result.success(list);
        redisUtil.set(redisKey, rs);
        return rs;
    }

    /**
     * 历史记录 获取用户最近读的一本书
     * @param userId 用户id
     * @param limit 获取最近的几本书
     *              limit = 1， 则为最近阅读的一本书
     *              todo limit = n，为获取历史记录
     *              目前只能为1
     * */
    @RequestMapping(value = "/history/study/book")
    @ResponseBody
    public Result<BookModel> getRecentRead(int userId, int limit) {
        String redisKey = "hqx:history:book:userId_" + userId;
        //redis获取值
        Result<BookModel> rs = (Result<BookModel>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        BookModel bookModel = historyService.getBookHistory(userId, 0, limit);
        rs = Result.success(bookModel);
        redisUtil.set(redisKey, rs);
        return rs;
    }
}
