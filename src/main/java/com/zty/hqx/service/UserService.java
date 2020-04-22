package com.zty.hqx.service;

import com.zty.hqx.dao.UserDao;
import com.zty.hqx.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public boolean updateUser(User user) {
        return userDao.updateUserMess(user);
    }

    public User getUserById(Integer stuId) {
        return userDao.getUserById(stuId);
    }

}
