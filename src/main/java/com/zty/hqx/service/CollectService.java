package com.zty.hqx.service;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.config.AppConfig;
import com.zty.hqx.dao.BaseDao;
import com.zty.hqx.dao.CollectDao;
import com.zty.hqx.dao.CollectionDao;
import com.zty.hqx.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    CollectionDao collectionDao;

    @Autowired
    AppConfig appConfig;

    public void setCollect(CollectModel model) {
        //表是否存在
        int isExist = collectDao.tableIsExixt(model.getUserId());
        //不存在  建表
        if (isExist == 0) {
            collectDao.creatTable(model.getUserId());
        }
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

    /**
     * 查询用户收藏
     * @param id  用户id 以此确认在哪里查表
     * @param num 索引
     * @return
     */
    public List<CollectionModel> getCollections(Integer id, Integer num) {
        List<CollectionModel> articles;
        // 判断是否存在用户收藏表
        int isExist = collectionDao.tableIsExixt(id);
        if (isExist == 0) {
            return null;
        }
        if (num == null){
            articles = collectionDao.getCollections(id);
        } else if (num == 0) {
            articles = collectionDao.getCollectionsLimit(id, appConfig.getCollectionOnceReadNum());
        } else {
            articles = collectionDao.getCollectionsByNum(id, num, appConfig.getCollectionOnceReadNum());
        }
        return articles;
    }

    /**
     * 收藏文章
     * @param id       用户id
     * @param part     文章类目
     * @param article  文章实例
     * @return
     */
    public boolean doCollect(Integer id, String part, Article article) {
        int isExist = collectionDao.tableIsExixt(id);
        if (isExist == 0) {
            collectionDao.buildTable(id);
        }
        boolean result = collectionDao.doCollect(id, part, article);
        return result;
    }

    /**
     * 判断文章是否已经被收藏过
     * @param uid   用户id
     * @param part  文章类目
     * @param artId 文章id
     * @return
     */
    public boolean isCollected(Integer uid, String part, Integer artId) {
        Integer id = null;
        try {
            id = collectionDao.isCollected(uid, part, artId);
        } catch (Throwable throwable) {
            return false;
        }
        if (id == null || id < 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 移除收藏文章
     * @param uid   用户id
     * @param part  文章类目
     * @param artId 文章id
     * @return
     */
    public boolean removeCollect(Integer uid, String part, Integer artId) {
        boolean result = false;
        try {
            result = collectionDao.removeCollect(uid, part, artId);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
        return result;
    }
}
