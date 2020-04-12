package com.zty.hqx.controller;

import com.zty.hqx.model.BookModel;
import com.zty.hqx.model.Result;
import com.zty.hqx.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 处理历史记录
 * */
@Controller
public class HistoryController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    HistoryService historyService;

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
    @Cacheable(value="history_book", key="'userId_'+#userId")
    public Result<BookModel> getRecentRead(int userId, int limit) {
        logger.info("book获取" + userId + "的历史记录");
        BookModel bookModel = historyService.getBookRecentHistory(userId, limit);
        return Result.success(bookModel);
    }
}
