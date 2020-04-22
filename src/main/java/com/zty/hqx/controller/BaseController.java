package com.zty.hqx.controller;

import com.alibaba.fastjson.JSONObject;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.model.BaseModel;
import com.zty.hqx.model.Result;
import com.zty.hqx.service.BaseService;
import com.zty.hqx.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.DecimalMin;
import java.util.List;

@Controller
@Validated
@CacheConfig(cacheNames = "hqx")
public class BaseController {
    @Autowired
    BaseService baseService;
    @Autowired
    HistoryService historyService;

    /**
     * 修改字段
     */
    @RequestMapping("/update/base")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key = "'manage:base'"),
            @CacheEvict(key = "'app:base:hot'"),
            @CacheEvict(key = "'app:base:around'"),
            @CacheEvict(key = "'app:base:num'"),
            @CacheEvict(key="'app:base:content:id_'+#id"),
            @CacheEvict(key = "'collect:base:null'"),
            @CacheEvict(key = "'search:base:null'")
    })
    public boolean updateBase(@DecimalMin("0") int id,
                              @RequestParam("title") String title,
                              @RequestParam("province") String province,
                              @RequestParam("city") String city,
                              @RequestParam("pic") String picUrl,
                              @RequestParam("html") String htmlUrl) {
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
    @Caching(evict = {
            @CacheEvict(key = "'manage:base'"),
            @CacheEvict(key = "'app:base'"),
            @CacheEvict(key = "'collect:base:null'"),
            @CacheEvict(key = "'search:base:null'")
    })
    public void deleteBase(@RequestParam("list") List<Integer> list) {
        for (int id : list) {
            baseService.deleteBase(id);
        }
    }

    /**
     * 上传基地信息
     */
    @RequestMapping("/upload/base")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key = "'manage:base'"),
            @CacheEvict(key = "'app:base:hot'"),
            @CacheEvict(key = "'app:base:around'"),
            @CacheEvict(key = "'app:base:num'"),
            @CacheEvict(key = "'search:base:null'")
    })
    public boolean upLoadBase(@RequestParam("title") String title,
                              @RequestParam("province") String province,
                              @RequestParam("city") String city,
                              @RequestParam("pic") String picUrl,
                              @RequestParam("html") String htmlUrl) {
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
        return baseService.isTitleAvailable(title);
    }

    /**
     * 获取指定基地的详细内容
     * @param id 基地编号
     * */
    @RequestMapping(value = "/base/content")
    @ResponseBody
    @Cacheable(key="'app:base:content:id_'+#id")
    public Result<BaseModel> getBaseContent(int userId, int id) {
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
    @Cacheable(key="'manage:base:page_'+#page + '_limit_' + #limit + '_key_' + #key")
    public String getManageBase(int userId, int page, int limit, String key) {
        List<BaseModel> list;// = baseService.getBase(page, limit);
        int num = (page - 1) * limit;
        if (key != null && !key.isEmpty()) {
            list = baseService.getBaseByKey(num, limit, key);
        } else {
            list = baseService.getBase(num, limit);
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
    @Cacheable(key="'app:base:num:num_' + #num + '_limit_' + #limit")
    public Result<List<BaseModel>> getNumBase(int userId, int num, int limit) {
        List<BaseModel> list = baseService.getBase(num, limit);
        return Result.success(list);
    }

    /**
     * app获取hot base记录
     */
    @RequestMapping(value = "/base/hot")
    @ResponseBody
    @Cacheable(key="'app:base:hot:num_' + #num + '_limit_' + #limit")
    public Result<List<BaseModel>> getHotBase(int userId, int num, int limit) {
        List<BaseModel> list = baseService.getHotBase(num, limit);
        return Result.success(list);
    }

    /**
     * app获取around base记录
     */
    @RequestMapping(value = "/base/around")
    @ResponseBody
    @Cacheable(key="'app:base:around:province_'+#province + '_city_' + #city + '_num_' + #num + '_limit_' + #limit")
    public Result<List<BaseModel>> getAddressBase(int userId, String province, String city, int num, int limit) {
        List<BaseModel> list = baseService.getBaseByAddress(province, city, num, limit);
        return Result.success(list);
    }
}
