package com.zty.hqx.controller;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.model.ClassifyModel;
import com.zty.hqx.model.CountModel;
import com.zty.hqx.model.ExamModel;
import com.zty.hqx.model.Result;
import com.zty.hqx.service.ClassifyService;
import com.zty.hqx.service.CountService;
import com.zty.hqx.service.StudyService;
import com.zty.hqx.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 控制端 获取浏览量 生成统计图和榜单
 * */
@Controller
public class CountController {
    @Autowired
    ClassifyService classifyService;
    @Autowired
    StudyService studyService;
    @Autowired
    CountService countService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 获取每一个part的每一个label的浏览排行榜 前十或者后十
     * 以及总浏览排行榜的前十或后十
     * @param part 哪一个部分
     * @param time 时间期限 周/月/年
     * @param type 0为top， 1为bottom
     * */
    @RequestMapping("/manage/chart")
    @ResponseBody
    public Result<HashMap<String, List<String>>> getChart(String model, String part, String time, int type){
        String redisKey = "hqx:manage:statistic:chart:" + model + ":" + part + ":" + time + "_" + type;
        Result<HashMap<String, List<String>>> rs = (Result<HashMap<String, List<String>>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        HashMap<String, List<String>> hashMap = countService.getChart(model, part, time, type);
        rs = Result.success(hashMap);
        redisUtil.set(redisKey, rs);
        return rs;
    }

    /**
     * 获取每一个part的每一个label的浏览量
     * @param part 哪一个部分
     * @param time 时间期限 周/月/年
     * */
    @RequestMapping("/manage/count")
    @ResponseBody
    public Result<HashMap<String, List<CountModel>>> getCount(String model, String part, String time){
        String redisKey = "hqx:manage:statistic:count:" + model + ":" + part + ":" + time;
        Result<HashMap<String, List<CountModel>>> rs = (Result<HashMap<String, List<CountModel>>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        EModel emodel = EModel.getEnumFromString(model.toUpperCase());
        HashMap<String, List<CountModel>> hashMap = new HashMap<>();
        switch (emodel){
            case BASE: hashMap = countService.getBaseCount(time);break;
            case STUDY: hashMap = countService.getStudyCount(part, time);break;
        }
        rs = Result.success(hashMap);
        redisUtil.set(redisKey, rs);
        return rs;
    }

    /**
     * 获取指定一个part的每一个label的浏览量对比
     * @param part 哪一个部分
     * @param time 时间期限 周/月/年
     *             这段时间的每个标签的总浏览量
     * */
    @RequestMapping("/manage/contrast")
    @ResponseBody
    public Result<List<HashMap<String, Integer>>> getContrastCount(String part, String time){
        String redisKey = "hqx:manage:statistic:contrast:study:" + part + ":" + time;
        Result<List<HashMap<String, Integer>>> rs = (Result<List<HashMap<String, Integer>>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<HashMap<String, Integer>> list;
        try {
            list = countService.getContrastCount(epart, time);
            rs = Result.success(list);
        } catch (ParseException e) {
            rs = Result.error();
        }
        redisUtil.set(redisKey, rs);
        return rs;
    }
}
