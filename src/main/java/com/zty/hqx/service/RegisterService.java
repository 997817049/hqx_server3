package com.zty.hqx.service;

import com.zty.hqx.dao.CollectDao;
import com.zty.hqx.dao.UserDao;
import com.zty.hqx.classify.CodeMsg;
import com.zty.hqx.model.Result;
import com.zty.hqx.model.User;
import com.zty.hqx.util.CaptchaPool;
import com.zty.hqx.util.FMD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class RegisterService {

    @Autowired
    UserDao userDao;

    @Autowired
    CollectDao collectDao;

    /**
     * 根据 邮箱 注册用户
     * @param mail     邮箱号
     * @param fromPass 密码密文
     * @return 成功返回用户id
     */
    public Result<Boolean> registerUser(@NotNull String mail, @NotNull String fromPass, @NotNull String captcha) {

        String cap = CaptchaPool.getCaptcha(mail);
        if (!captcha.equals(cap)) {
            return Result.error(CodeMsg.CAPTCHA_NOT_TRUE);
        }

        String DBpass = FMD5Util.formPassToDBPass(fromPass);
        User user = userDao.getUserByMail(mail);
        if (user != null) {
            return Result.error(CodeMsg.MAIL_ALREADY_REGISTE);
        }

        user = new User();
        user.setMail(mail);
        user.setPassword(DBpass);

        if (userDao.insertUser(user)) {
            CaptchaPool.remove(mail);
            // 注册成功时  为该用户生成 collect表
            collectDao.creatTable(user.getId());
            return Result.success(true);
        }
        return Result.error(CodeMsg.REGISTER_ERR);
    }

}
