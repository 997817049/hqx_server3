package com.zty.hqx.controller;

import com.zty.hqx.model.BookModel;
import com.zty.hqx.model.Result;
import com.zty.hqx.model.VideoModel;
import com.zty.hqx.service.HistoryService;
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
@CacheConfig(cacheNames = "hqx")
public class HistoryController {
    @Autowired
    HistoryService historyService;

    @RequestMapping(value = "/history/study/video")
    @ResponseBody
    @Cacheable(key="'history:' + #part + ':' + #userId + ':num_' + #num + '_limit_'+ #limit")
    public Result<List<VideoModel>> getVideoHistory(int userId, String part, int num, int limit) {
        List<VideoModel> list = historyService.getVideoHistory(userId, part, num, limit);
        return Result.success(list);
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
    @Cacheable(key="'history:book:userId_'+#userId")
    public Result<BookModel> getRecentRead(int userId, int limit) {
        BookModel bookModel = historyService.getBookHistory(userId, 0, limit);
        return Result.success(bookModel);
    }
}
