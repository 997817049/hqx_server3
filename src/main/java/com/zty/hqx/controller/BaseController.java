package com.zty.hqx.controller;

import com.alibaba.fastjson.JSONObject;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.model.BaseModel;
import com.zty.hqx.model.Result;
import com.zty.hqx.service.BaseService;
import com.zty.hqx.service.HistoryService;
import com.zty.hqx.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.DecimalMin;
import java.util.List;

@Controller
@Validated
public class BaseController {
    @Autowired
    BaseService baseService;
    @Autowired
    HistoryService historyService;
    @Autowired
    RedisUtil redisUtil;

    /**
     * 修改字段
     */
    @RequestMapping("/update/base")
    @ResponseBody
    public boolean updateBase(@DecimalMin("0") int id,
                              @RequestParam("title") String title,
                              @RequestParam("province") String province,
                              @RequestParam("city") String city,
                              @RequestParam("pic") String picUrl,
                              @RequestParam("html") String htmlUrl) {
        BaseModel baseModel = new BaseModel(id, title, province, city, picUrl, htmlUrl);
        //更新数据
        baseService.updateBase(baseModel);
        //删除缓存
         redisUtil.removePattern("hqx:manage:base*",
                 "hqx:app:base:hot*",
                 "hqx:app:base:around*",
                "hqx:app:base:num*",
                 "hqx:app:base:content:id_" + id + "*",
                "hqx:collect:base:null*",
                 "hqx:search:base:null*");
        return true;
    }

    /**
     * 删除某一记录
     */
    @RequestMapping("/delete/base")
    @ResponseBody
    public void deleteBase(@RequestParam("list") List<Integer> list) {
        for (int id : list) {
            baseService.deleteBase(id);
        }
        //删除缓存
        redisUtil.removePattern("hqx:manage:base*", "hqx:app:base*",
                "hqx:collect:base:null*", "hqx:search:base:null*");
    }

    /**
     * 上传基地信息
     */
    @RequestMapping("/upload/base")
    @ResponseBody
    public boolean upLoadBase(@RequestParam("title") String title,
                              @RequestParam("province") String province,
                              @RequestParam("city") String city,
                              @RequestParam("pic") String picUrl,
                              @RequestParam("html") String htmlUrl) {
        BaseModel baseModel = new BaseModel(0, title, province, city, picUrl, htmlUrl);
        //插入数据库
        baseService.insertBase(baseModel);
        //删除缓存
        redisUtil.removePattern("hqx:manage:base*",
                "hqx:app:base:hot*",
                "hqx:app:base:around*",
                "hqx:app:base:num*",
                "hqx:search:base:null*");
        return true;
    }

    /**
     * 判断该名称是否存在 可用
     * @param title 新景点的名称
     * */
    @RequestMapping("/test/base")
    @ResponseBody
    public boolean isTitleAvailable(String title){
        return baseService.isTitleAvailable(title);
    }

    /**
     * 获取指定基地的详细内容
     * @param id 基地编号
     * */
    @RequestMapping(value = "/base/content")
    @ResponseBody
    public Result<BaseModel> getBaseContent(int userId, int id) {
        String redisKey = "hqx:app:base:content:id_" + id;
        //更新count
        baseService.updateBaseCount(id);
        //插入历史记录
        historyService.insertHistory(userId, EModel.BASE.getType(), 0, id);
        //redis获取值
        Result<BaseModel> rs = null;
        try {
            rs = (Result<BaseModel>) redisUtil.get(redisKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(rs != null){
            return rs;
        }
        //数据库获取值
        BaseModel model = baseService.getBaseById(userId, id);
        if(model == null){
            rs = Result.error();
        } else {
            rs = Result.success(model);
        }
        redisUtil.set(redisKey, rs);
        return rs;
    }

    /**
     * 控制端获取基地数据
     * */
    @RequestMapping(value = "/manage/base")
    @ResponseBody
    public String getManageBase(int page, int limit, String key) {
        String redisKey = "hqx:manage:base:page_" + page + "_limit_" + limit + "_key_" + key;
        //redis获取值
        String rs = null;
        try {
            rs = (String) redisUtil.get(redisKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<BaseModel> list;// = baseService.getBase(page, limit);
        int num = (page - 1) * limit;
        if (key != null && !key.isEmpty()) {
            list = baseService.getBaseByKey(num, limit, key);
        } else {
            list = baseService.getBaseByNum(num, limit);
        }

        JSONObject obj = new JSONObject();
        //前台通过key值获得对应的value值
        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count", baseService.getBaseCount());
        obj.put("data", list);
        rs = obj.toJSONString();
        //存入数据库
        redisUtil.set(redisKey, rs);
        return rs;
    }

    /**
     * app获取对应的base记录
     * @param num 当前已查看的记录编号
     */
    @RequestMapping(value = "/base")
    @ResponseBody
    public Result<List<BaseModel>> getNumBase(int userId, int num, int limit) {
        String redisKey = "hqx:app:base:num:num_" + num + "_limit_" + limit;
        //redis获取值
        Result<List<BaseModel>> rs = getRedisCache(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<BaseModel> list = baseService.getBase(num, limit);
        rs = Result.success(list);
        //存入数据库
        redisUtil.set(redisKey, rs);
        return rs;
    }

    /**
     * app获取hot base记录
     */
    @RequestMapping(value = "/base/hot")
    @ResponseBody
    public Result<List<BaseModel>> getHotBase(int userId, int limit) {
        String redisKey = "hqx:app:base:hot:limit_" + limit;
        //redis获取值
        Result<List<BaseModel>> rs = getRedisCache(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<BaseModel> list = baseService.getHotBase(limit);
        rs = Result.success(list);
        //存入数据库
        redisUtil.set(redisKey, rs);
        return rs;
    }

    /**
     * app获取around base记录
     */
    @RequestMapping(value = "/base/around")
    @ResponseBody
    public Result<List<BaseModel>> getAddressBase(int userId, String province, String city, int limit) {
        String redisKey = "hqx:app:base:around:province_" + province + "_city_" + city + "_limit_" + limit;
        //redis获取值
        Result<List<BaseModel>> rs = getRedisCache(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<BaseModel> list = baseService.getBaseByAddress(province, city, limit);
        rs = Result.success(list);
        //存入数据库
        redisUtil.set(redisKey, rs);
        return rs;
    }

    private Result<List<BaseModel>> getRedisCache(String redisKey){
        //redis获取值
        Result<List<BaseModel>> rs = null;
        try {
            rs = (Result<List<BaseModel>>) redisUtil.get(redisKey);
        } catch (Exception e){
            e.printStackTrace();
        }
        return rs;
    }
}
