package com.zty.hqx.controller;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.service.UserService;
import com.zty.hqx.classify.CodeMsg;
import com.zty.hqx.model.Result;
import com.zty.hqx.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userMsg")
public class UserMsgController {

    @Autowired
    public UserService userService;

    /**
     * 更新用户信息
     * @param userMsgJson 用户信息json
     * @return
     */
    @RequestMapping("/update")
    public Result<Boolean> updateUserMsg(String userMsgJson) {
        User user = JSON.parseObject(userMsgJson, User.class);
        boolean result = userService.updateUser(user);
        if (result) {
            return Result.success(true);
        } else {
            return Result.error(CodeMsg.DATABASE_UNKNOW_ERROR);
        }
    }
}
