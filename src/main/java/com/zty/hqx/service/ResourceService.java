package com.zty.hqx.service;

import com.zty.hqx.dao.ResourceDao;
import com.zty.hqx.model.ResourceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceService {
    @Autowired
    private ResourceDao resourceDao;

    public String isExistBook(String md5){
        ResourceModel rsModel = resourceDao.getBook(md5);
        if(rsModel == null){
            return null;
        }
        return rsModel.getUrl();
    }

    public String isExistPic(String md5){
        ResourceModel rsModel = resourceDao.getPic(md5);
        if(rsModel == null){
            return null;
        }
        return rsModel.getUrl();
    }

    public String isExistVideo(String md5){
        ResourceModel rsModel = resourceDao.getVideo(md5);
        if(rsModel == null){
            return null;
        }
        return rsModel.getUrl();
    }

    public void insertBook(ResourceModel model) {
        resourceDao.insertBook(model);
    }

    public void insertPic(ResourceModel model) {
        resourceDao.insertPic(model);
    }

    public void insertVideo(ResourceModel model) {
        resourceDao.insertVideo(model);
    }
}
