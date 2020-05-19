package com.zty.hqx.controller;

import com.zty.hqx.model.Result;
import com.zty.hqx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    UserService userService;

    /**
     * 登录成功转到基地界面
     * */
    @PostMapping("/hqx/login")
    @ResponseBody
    public Result<Integer> dealLogin(String mail, String password){
        Integer userId = userService.doLogin(mail, password);
        if(userId != null){
            return Result.success(userId);
        }
        return Result.error();
    }
}
