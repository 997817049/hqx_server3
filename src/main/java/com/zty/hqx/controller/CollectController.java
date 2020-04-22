package com.zty.hqx.controller;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.annotation.IsModel;
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
@CacheConfig(cacheNames = "hqx")
public class CollectController {
    @Autowired
    CollectService collectService;

    /**
     * 收藏
     * @param userId 用户信息 暂用用id代替，todo 需要验证
     * */
    @RequestMapping(value = "/collect/apply")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key="'collect:' + #model + ':'+ #part + ':userId_'+#userId"),
            @CacheEvict(key="'app:study:exam:recent'"),//, condition = "#part eq 'exam'"
            @CacheEvict(key="'app:study:exam:special'"),
            @CacheEvict(key="'app:study:exam:info'")//id_id
    })
    public void applyCollect(int userId, @IsModel String model, String part, int id, String progress) {
        System.out.println(part);
        System.out.println();
        int modelId = EModel.getEnumFromString(model.toUpperCase()).getType();
        int partId = 0;
        if(modelId == EModel.STUDY.getType()){
            partId = EStudyPart.getEnumFromString(part.toUpperCase()).getType();
        }
        collectService.setCollect(new CollectModel(userId, modelId, partId, id, progress));
    }

    /**
     * 取消收藏
     * @param userId 用户信息 暂用用id代替，todo 需要验证
     * */
    @RequestMapping(value = "/collect/cancel")
    @ResponseBody
    @Caching(evict = {
        @CacheEvict(key="'collect:' + #model + ':'+ #part + ':userId_'+#userId"),
        @CacheEvict(key="'app:study:exam:recent'"),//, condition = "#part eq 'exam'"
        @CacheEvict(key="'app:study:exam:special'"),
        @CacheEvict(key="'app:study:exam:info'")//id_id
    })
    public void cancelCollect(int userId, @IsModel String model, String part, int id) {
        int modelId = EModel.getEnumFromString(model.toUpperCase()).getType();
        int partId = 0;
        if(modelId == EModel.STUDY.getType()){
            partId = EStudyPart.getEnumFromString(part.toUpperCase()).getType();
        }
        collectService.deleteCollect(new CollectModel(userId, modelId, partId, id, null));
    }

    /**
     * 获取指定类别的全部收藏
     * @param userId 用户信息 暂用用id代替，todo 需要验证
     * */
    @RequestMapping(value = "/collect/load")
    @ResponseBody
    @Cacheable(key="'collect:' + #model + ':'+ #part + ':userId_'+#userId")
    public Result<String> getCollect(int userId, @IsModel String model, String part) {
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
        String rs = JSON.toJSONString(list);
        return Result.success(rs);
    }
}
