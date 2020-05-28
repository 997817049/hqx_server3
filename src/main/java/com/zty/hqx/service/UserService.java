package com.zty.hqx.service;

import com.zty.hqx.dao.UserDao;
import com.zty.hqx.model.User;
import com.zty.hqx.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    public Integer doLogin(String mail, String password){
        System.out.println(password);
        User user = userDao.getUserByMail(mail);
        if (user != null && user.getStatus() == 0) {
            String DBpass = user.getPassword();
            String pass = Md5Util.formPassToDBPass(password);
            System.out.println(DBpass + "**" + pass);
            if (DBpass.equals(pass)) {
                return user.getId();
            }
        }

        return null;
    }
}
