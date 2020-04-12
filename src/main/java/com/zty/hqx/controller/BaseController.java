package com.zty.hqx.controller;

import com.alibaba.fastjson.JSONObject;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.config.RedisConfig;
import com.zty.hqx.model.BaseModel;
import com.zty.hqx.model.Result;
import com.zty.hqx.service.BaseService;
import com.zty.hqx.service.HistoryService;
import com.zty.hqx.service.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.DecimalMin;
import java.util.List;

@Controller
public class BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    BaseService baseService;
    @Autowired
    HistoryService historyService;

    /**
     * 修改字段
     */
    @RequestMapping("/update/base")
    @ResponseBody
//    @Caching(evict = {
//            @CacheEvict(value={"app_base", "manage_base"}, allEntries = true),
//            @CacheEvict(value={"app_base_content"}, key="'id_'+#id"),
//            @CacheEvict(value="collect", allEntries = true),
//            @CacheEvict(value = "search", allEntries = true)
//    })
    public boolean updateBase(@DecimalMin("0") int id,
                              @RequestParam("title") String title,
                              @RequestParam("province") String province,
                              @RequestParam("city") String city,
                              @RequestParam("pic") String picUrl,
                              @RequestParam("html") String htmlUrl) {
        logger.info("base更新 id=" + id + "的记录");
        BaseModel baseModel = new BaseModel(id, title, province, city, picUrl, htmlUrl);
        //更新数据
        baseService.updateBase(baseModel);
        return true;
    }

    /**
     * 删除某一记录
     */
    @RequestMapping("/delete/base")
    @ResponseBody
//    @Caching(evict = {
//            @CacheEvict(value={"app_base", "manage_base"}, allEntries = true),
//            @CacheEvict(value={"app_base_content"}, allEntries = true),
//            @CacheEvict(value="collect", allEntries = true),
//            @CacheEvict(value = "search", allEntries = true)
//    })
    public void deleteBase(@RequestParam("list") List<Integer> list) {
        logger.info("base删除记录" + list);
        for (int id : list) {
            baseService.deleteBase(id);
        }
    }

    /**
     * 上传基地信息
     */
    @RequestMapping("/upload/base")
    @ResponseBody
//    @Caching(evict = {
//            @CacheEvict(value={"app_base", "manage_base"}, allEntries = true),
//            @CacheEvict(value = "search", allEntries = true)
//    })
    public boolean upLoadBase(@RequestParam("title") String title,
                              @RequestParam("province") String province,
                              @RequestParam("city") String city,
                              @RequestParam("pic") String picUrl,
                              @RequestParam("html") String htmlUrl) {
        logger.info("上传基地信息 title：" + title);
        BaseModel baseModel = new BaseModel(0, title, province, city, picUrl, htmlUrl);
        //插入数据库
        baseService.insertBase(baseModel);
        return true;
    }

    /**
     * 判断该名称是否存在 可用
     * @param title 新景点的名称
     * */
    @RequestMapping("/test/base")
    @ResponseBody
    public boolean isTitleAvailable(String title){
        logger.info("检查base的名称是否可用" + title);
        boolean rs = baseService.isTitleAvailable(title);
        return rs;
    }

    /**
     * 获取指定基地的详细内容
     * @param id 基地编号
     * */
    @RequestMapping(value = "/base/content")
    @ResponseBody
//    @Cacheable(value="app_base_content", key="'id_'+#id")
    public Result<BaseModel> getBaseContent(int userId, int id) {
        logger.info("userId: " + userId + "查看基地 num = " + id);
        BaseModel model = baseService.getBaseById(userId, id);
        historyService.insertHistory(userId, EModel.BASE.getType(), 0, id);
        baseService.updateBaseCount(id);
        return Result.success(model);
    }

    /**
     * 控制端获取基地数据
     * */
    @RequestMapping(value = "/manage/base")
    @ResponseBody
//    @Cacheable(value="manage_base", key="'page_'+#page + '_limit_' + #limit + '_key_' + #key")
    public String getBase(int userId, int page, int limit, String key) {
        logger.info("控制端获取基地数据" + page + "**" + limit + " key = " + key);
        List<BaseModel> list;// = baseService.getBase(page, limit);
        int num = (page - 1) * limit;
        if (key != null && !key.isEmpty()) {
            list = baseService.getBaseByKey(userId, num, limit, key);
        } else {
            list = baseService.getBase(userId, num, limit);
        }

        JSONObject obj = new JSONObject();
        //前台通过key值获得对应的value值
        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count", baseService.getBaseCount());
        obj.put("data", list);
        return obj.toJSONString();
    }

    /**
     * app获取对应的base记录
     * @param num      当前已查看的记录编号
     */
    @RequestMapping(value = "/base")
    @ResponseBody
//    @Cacheable(value="app_base", key="'num_'+#num")
    public Result<List<BaseModel>> getBase(int userId, int num) {
        logger.info("app获取基地数据, num = " + num);
        List<BaseModel> list = baseService.getBase(userId, num, 10);
        return Result.success(list);
    }
}
