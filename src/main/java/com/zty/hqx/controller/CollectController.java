package com.zty.hqx.controller;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.annotation.IsModel;
import com.zty.hqx.classify.CodeMsg;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.model.*;
import com.zty.hqx.service.ArticleReadSevice;
import com.zty.hqx.service.CollectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CollectService collectService;

    @Autowired
    public ArticleReadSevice articleService;

    /**
     * 收藏
     * @param userId 用户信息 暂用用id代替，todo 需要验证
     * */
    @RequestMapping(value = "/collect/apply")
    @ResponseBody
    @CacheEvict(value="collect", key="'userId_'+#userId + '_' + #model + '_'+ #part")
    public void dealCollect(int userId, @IsModel String model, String part, int id, String progress) {
        logger.info(userId + "用户收藏了" + model + "的" + id);
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
    @CacheEvict(value="collect", key="'userId_'+#userId + '_' + #model + '_'+ #part")
    public void cancelCollect(int userId, @IsModel String model, String part, int id) {
        logger.info(userId + "用户取消收藏了" + part + "的" + id);
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
    @Cacheable(value="collect", key="'userId_'+#userId + '_' + #model + '_'+ #part")
    public Result<String> getCollect(int userId, @IsModel String model, String part) {
        logger.info(userId + "用户获取收藏" + model + "的" + part);
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

//-----------------------------------收藏 fy --------------------------------------------------------

    /**
     * 查询收藏列表
     * @param id  用户id 以此确定表
     * @param num 索引
     * @return
     */
    @RequestMapping("/collection/list")
    public Result<List> getCollections(Integer id, Integer num) {
        List<CollectionModel> articles;
        articles = collectService.getCollections(id, num);
        if (articles == null || articles.size() == 0) {
            return Result.error(CodeMsg.NO_COLLECTION_DATA);
        }
        return Result.success(articles);
    }

    /**
     * 收藏文章
     * @param uid   用户id
     * @param part  文章类目
     * @param artId 文章编号
     * @return
     */
    @RequestMapping("/collection/doCollect")
    public Result<Boolean> doCollect(Integer uid, String part, Integer artId) {
        Article article = articleService.getArticleById(part, artId);
        if (article == null) {
            return Result.error(CodeMsg.NO_COLLECT_TARGET);
        }
        boolean result = collectService.doCollect(uid, part, article);
        if (result) {
            return Result.success(result);
        } else {
            return Result.error(CodeMsg.DATABASE_UNKNOW_ERROR);
        }
    }

    /**
     * 取消收藏文章
     * @param uid   用户id
     * @param part  文章类目
     * @param artId 文章编号
     * @return
     */
    @RequestMapping("/collection/removeCollect")
    public Result<Boolean> removeCollect(Integer uid, String part, Integer artId) {
        boolean result = collectService.removeCollect(uid, part, artId);
        if (result) {
            return Result.success(result);
        } else {
            return Result.error(CodeMsg.DATABASE_UNKNOW_ERROR);
        }
    }

    /**
     * 判断文章是否已经被收藏
     * @param uid    用户id
     * @param part   文章类目
     * @param artId  文章id
     * @return
     */
    @RequestMapping("/collection/isCollected")
    public Result<Boolean> IsCollected(Integer uid, String part, Integer artId) {
        boolean result = collectService.isCollected(uid, part, artId);
        return Result.success(result);
    }
}
