package com.zty.hqx.controller;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.util.RedisUtil;
import com.zty.hqx.validator.IsModel;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.model.*;
import com.zty.hqx.service.BaseService;
import com.zty.hqx.service.SearchService;
import com.zty.hqx.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class SearchController {
    @Autowired
    SearchService searchService;
    @Autowired
    BaseService baseService;
    @Autowired
    StudyService studyService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 删除某个用户搜索的全部词语
     * */
    @RequestMapping(value = "/delete/history_word")
    @ResponseBody
    public Result<Boolean> deleteHistoryWord(int userId){
        searchService.deleteHistoryWord(userId);
        return Result.success(true);
    }

    @RequestMapping(value = "/search/history_word")
    @ResponseBody
    public Result<List<String>> getHistoryWord(int userId) {
        List<String> list = searchService.getHistoryWord(userId, 5);
        if(list == null){
            return Result.error();
        }
        return Result.success(list);
    }

    @RequestMapping(value = "/search/hotword")
    @ResponseBody
    public Result<List<String>> getHotWord(int userId) {
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
    public Result<String> dealSearch(int userId, @NotBlank String word, @IsModel String model, String part, int num, int limit) {
        String redisKey = "hqx:search:" + model + ":" + part + ":" + word + ":num_" + num + "_limit_" + limit;
        Result<String> rs = (Result<String>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        EModel emodel = EModel.getEnumFromString(model.toUpperCase());
        if(limit == 3){
            searchService.updateWordCount(userId, word);
        }
        String str;
        switch (emodel){
            case BASE: str = JSON.toJSONString(baseService.getBaseByKey(num, limit, word));break;
            case STUDY:
                EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
                str = studyService.getStudyByKey(userId, word, epart, num, limit);
                break;
            default: return Result.error();
        }
        rs = Result.success(str);
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }
}
