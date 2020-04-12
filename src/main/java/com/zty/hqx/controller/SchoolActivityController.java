package com.zty.hqx.controller;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.service.SchActivityService;
import com.zty.hqx.service.UserService;
import com.zty.hqx.classify.CodeMsg;
import com.zty.hqx.model.Result;
import com.zty.hqx.model.SchoolActivityModel;
import com.zty.hqx.model.User;
import com.zty.hqx.util.cachePool.CacheKeys;
import com.zty.hqx.util.cachePool.CachePoolInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/SchoolActivity")
public class SchoolActivityController {

    @Autowired
    SchActivityService actSrv;

    @Autowired
    UserService userService;

    @Autowired
    CachePoolInterface cachePool;

    private Logger logger = LogManager.getLogger(SchoolActivityController.class);

    @RequestMapping("/getActivities")
    public Result<List<SchoolActivityModel>> getActivities() {
        List<SchoolActivityModel> activities = null;
        String listStr;
        if ((listStr = (String) cachePool.get(CacheKeys.SCHOOL_ACTIVITY_LIST)) != null) {
            return Result.success(JSON.parseObject(listStr, List.class));
        }

        logger.info("[DO_SQL]" + "[getActivities]");
        activities = actSrv.getActivities();
        if (activities == null || activities.size() == 0) {
            return Result.error(CodeMsg.NO_DATA_ERROR);
        }
        cachePool.put(CacheKeys.SCHOOL_ACTIVITY_LIST, JSON.toJSONString(activities));
        return Result.success(activities);
    }

    @RequestMapping("/build")
    public Result<Boolean> build() {
        // TODO 创建活动 创建时会先将活动创建到活动列表中 再通过活动id 创建活动参与表

        return Result.success(true);
    }

    /**
     * 参加活动
     * @param id    活动id
     * @param stuId 学生id
     * @return
     */
    @RequestMapping("/join")
    public Result<Boolean> join(Integer id, Integer stuId) {
        // 参加活动
        User user = userService.getUserById(stuId);
        boolean result = actSrv.join(id, user);
        return Result.success(result);
    }

    @RequestMapping("/isJoin")
    public Result<Boolean> isJoin(Integer id, Integer stuId) {
        // 参加活动
        boolean result = actSrv.isJoin(id, stuId);
        return Result.success(result);
    }

    @RequestMapping("/cancel")
    public Result<Boolean> cancel(Integer id, Integer stuId) {
        // 取消参加活动
        boolean result = actSrv.cancel(id, stuId);
        return Result.success(true);
    }

}
