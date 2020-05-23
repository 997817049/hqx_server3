package com.zty.hqx.controller;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.util.RedisUtil;
import com.zty.hqx.validator.IsModel;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.model.*;
import com.zty.hqx.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 处理全部收藏
 * */
@Controller
@Validated
public class CollectController {
    @Autowired
    CollectService collectService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 收藏
     * @param userId 用户信息 暂用用id代替，todo 需要验证
     * */
    @RequestMapping(value = "/collect/apply")
    @ResponseBody
    public Result<Boolean> applyCollect(int userId, @IsModel String model, String part, int id, String progress) {
        int modelId = EModel.getEnumFromString(model.toUpperCase()).getType();
        int partId = 0;
        if(modelId == EModel.STUDY.getType()){
            partId = EStudyPart.getEnumFromString(part.toUpperCase()).getType();
        } else {
            progress = "0";
        }
        collectService.setCollect(new CollectModel(userId, modelId, partId, id, progress));
        dealRedis(userId, model, part, id);
        return Result.success(true);
    }

    /**
     * 取消收藏
     * @param userId 用户信息 暂用用id代替，todo 需要验证
     * */
    @RequestMapping(value = "/collect/cancel")
    @ResponseBody
    public void cancelCollect(int userId, @IsModel String model, String part, int id) {
        int modelId = EModel.getEnumFromString(model.toUpperCase()).getType();
        int partId = 0;
        if(modelId == EModel.STUDY.getType()){
            partId = EStudyPart.getEnumFromString(part.toUpperCase()).getType();
        }
        collectService.deleteCollect(new CollectModel(userId, modelId, partId, id, null));
        dealRedis(userId, model, part, id);
    }

    /**
     * 获取指定类别的全部收藏
     * @param userId 用户信息 暂用用id代替，todo 需要验证
     * */
    @RequestMapping(value = "/collect/load")
    @ResponseBody
    public Result<String> getCollect(int userId, @IsModel String model, String part) {
        String redisKey = "hqx:collect:" + model + ":" + part + ":userId_" + userId;
        Result<String> rs = (Result<String>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        EModel emodel = EModel.getEnumFromString(model.toUpperCase());
        List list = null;
        switch (emodel){
            case BASE:
                list = collectService.getBaseCollect(userId);
                break;
            case STUDY:
                EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
                list = collectService.getStudyCollect(userId,epart);
                break;
        }
        String str = JSON.toJSONString(list);
        rs = Result.success(str);
        redisUtil.set(redisKey, rs);
        return rs;
    }

    private void dealRedis(int userId, String model, String part, int id){
        if(model.equals("base")){
            redisUtil.remove("hqx:app:base:content:id_" + id);
        } else if(part.equals("exam")){
            redisUtil.removePattern("hqx:app:study:exam:recent*",
                    "hqx:app:study:exam:special*");
        }
        redisUtil.removePattern("hqx:app:study:" + part + ":info:id_" + id + "*",
                "hqx:collect:" + model + ":" + part + ":userId_" + userId + "*");
    }

}
