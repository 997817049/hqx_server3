package com.zty.hqx.service;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.config.AppConfig;
import com.zty.hqx.dao.BaseDao;
import com.zty.hqx.dao.CollectDao;
import com.zty.hqx.dao.UserDao;
import com.zty.hqx.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectService {
    @Autowired
    CollectDao collectDao;
    @Autowired
    BaseDao baseDao;
    @Autowired
    StudyService studyService;
    @Autowired
    AppConfig appConfig;
    @Autowired
    UserDao userDao;

    public String isCollect(CollectModel model){
        return collectDao.isCollect(model);
    }


    public void setCollect(CollectModel model) {
        //是否已经被收藏
        String progress = collectDao.isCollect(model);
        //未收藏 擦入
        if(progress == null){
            collectDao.insertCollect(model);
        } else {//已收藏 更新进度
            collectDao.updateCollect(model);
        }
    }

    public void deleteCollect(CollectModel model) {
        if(model.getModel() == EModel.BASE.getType()){
            collectDao.deleteBaseCollect(model);
        } else {
            collectDao.deleteCollect(model);
        }
    }

    //当记录删除时 需要删掉全部相关的收藏
    public void deleteAllCollect(EModel model, EStudyPart part, int id) {
        CollectModel collectModel = new CollectModel();
        collectModel.setModel(model.getType());
        collectModel.setId(id);
        if(model == EModel.BASE){
            collectDao.deleteBaseCollect(collectModel);
        } else {
            collectModel.setPart(part);
            collectDao.deleteAllCollect(collectModel);
        }
    }

    public List<BaseModel> getBaseCollect(int userId) {
        return collectDao.getAllBaseCollect(userId);
    }

    public List getStudyCollect(int userId, EStudyPart part) {
        List list2;
        switch (part){
            case EXAM:list2 = collectDao.getAllExamCollect(userId);break;
            case BOOK:list2 = collectDao.getAllBookCollect(userId);break;
            default:list2 = collectDao.getAllVideoCollect(part.getEnglish(), userId);break;
        }
        return list2;
    }
}
