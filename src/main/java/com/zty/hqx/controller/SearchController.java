package com.zty.hqx.controller;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.annotation.IsModel;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.model.*;
import com.zty.hqx.service.BaseService;
import com.zty.hqx.service.SearchService;
import com.zty.hqx.service.StudyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Controller
public class SearchController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SearchService searchService;
    @Autowired
    BaseService baseService;
    @Autowired
    StudyService studyService;

    /**
     * 删除某个用户搜索的全部词语
     * */
    @RequestMapping(value = "/delete/history_word")
    @ResponseBody
    public Result<Boolean> deleteHistoryWord(int userId){
        logger.info("删除历史查找词汇" + userId);
        searchService.deleteHistoryWord(userId);
        return Result.success(true);
    }

    @RequestMapping(value = "/search/history_word")
    @ResponseBody
    public Result<List<String>> getHistoryWord(int userId) {
        logger.info("获取历史查找词汇" + userId);
        List<String> list = searchService.getHistoryWord(userId);
        if(list == null){
            return Result.error();
        }
        return Result.success(list);
    }

    @RequestMapping(value = "/search/hotword")
    @ResponseBody
    public Result<List<String>> getHotWord(int userId) {
        logger.info("获取热点词汇");
        List<String> list = searchService.getHotWord();
        if(list == null){
            return Result.error();
        }
        return Result.success(list);
    }

    /**
     * 根据word在某个model的某个part中查找相关内容
     * @param limit 查找几个
     *              limit = 3, 第一个查找页面，第一次查找，点击更多 会到达第二个查找界面，显示更多相关信息
     *                          所以只有在第一个查找页面记录查找次数。
     *              limit = n，第二个查找页面，
     * */
    @RequestMapping(value = "/search")
    @ResponseBody
    @Cacheable(value="search", key="'word_'+ #word + '_' + #model + '_' + #part + '_num_' + #num + '_limit_' + #limit")
    public Result<String> dealSearch(int userId, @NotBlank String word, @IsModel String model, String part, int num, int limit) {
        logger.info("查找" + word + "在" + model + "**" + part + "中");
        EModel emodel = EModel.getEnumFromString(model.toUpperCase());
        if(limit == 3){
            searchService.updateWordCount(userId, word);
        }
        String rs;
        switch (emodel){
            case BASE: rs = JSON.toJSONString(baseService.getBaseByKey(userId, num, limit, word));break;
            case STUDY:
                EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
                rs = studyService.getStudyByKey(userId, word, epart, num, limit);
                break;
            default: return Result.error();
        }
        return Result.success(rs);
    }
}
