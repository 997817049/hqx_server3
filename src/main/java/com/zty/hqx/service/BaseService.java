package com.zty.hqx.service;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.dao.BaseDao;
import com.zty.hqx.dao.CollectDao;
import com.zty.hqx.dao.ResourceDao;
import com.zty.hqx.dao.UserDao;
import com.zty.hqx.model.BaseModel;
import com.zty.hqx.model.CollectModel;
import com.zty.hqx.model.User;
import com.zty.hqx.util.FileUtil;
import com.zty.hqx.util.ZtyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseService {
    @Autowired
    BaseDao baseDao;
    @Autowired
    CollectDao collectDao;
    @Autowired
    ResourceDao resourceDao;
    @Autowired
    UserDao userDao;
    @Autowired
    CollectService collectService;
    @Autowired
    HistoryService historyService;

    public void updateBaseCount(int id){
        baseDao.updateBaseCount(id);
    }

    public void updateBase(BaseModel baseModel) {
        BaseModel oldModel = baseDao.getBaseById(baseModel.getId());
        String oldPic = oldModel.getPicUrl();
        if(!oldPic.equals(baseModel.getPicUrl())){
            //删除原来的图片文件
            FileUtil.deleteFile(ZtyUtil.dealSqlPathToFile(oldPic));
        }
        baseDao.updateBase(baseModel);
    }

    public boolean deleteBase(int id) {
        BaseModel baseModel = baseDao.getBaseById(id);
        baseDao.deleteBase(id);
        //删除外链
        String pic = baseModel.getPicUrl();
        String html = baseModel.getHtmlUrl();
        //删除文件
        try {
            resourceDao.deletePic(pic);
            FileUtil.deleteFile(ZtyUtil.dealSqlPathToFile(pic));
        } catch (Exception e){
            return false;
        } finally {
            System.out.println(ZtyUtil.dealSqlPathToFile(html));
            boolean rs = FileUtil.deleteFile(ZtyUtil.dealSqlPathToFile(html));
            System.out.println(rs);
            //删除全部收藏
            collectService.deleteAllCollect(EModel.BASE, null, id);
            //删除全部历史记录
            historyService.deleteAllHistory(EModel.BASE.getType(), 0, id);
        }
        return true;
    }

    public void insertBase(BaseModel baseModel) {
        baseDao.insertBase(baseModel);
    }

// <---------------------------------------根据要求获取基地信息------------------------------------------->

    public boolean isTitleAvailable(String title){
        BaseModel model = baseDao.isTitleAvailable(title);
        if(model == null) return true;
        return false;
    }

    public int getBaseCount(){
        return baseDao.getBaseCount();
    }

    public BaseModel getBaseById(int userId, int id) {
        BaseModel model = baseDao.getBaseById(id);
        return dealBaseCollect(userId, model);
    }

    public List<BaseModel> getHotBase(int num, int limit) {
        return baseDao.getHotBase(num, limit);
    }

    public List<BaseModel> getBaseByAddress(String province, String city, int num, int limit) {
        return baseDao.getBaseByAddress(province, city, num, limit);
    }

    public List<BaseModel> getBase(int num, int limit) {
        return baseDao.getBase(num, limit);
    }

    public List<BaseModel> getBaseByKey(int num, int limit, String key) {
        List<BaseModel> list = baseDao.getBaseByKey(key, num, limit);
//        for (int i = 0; i < list.size(); i++) {
//            list.set(i, dealBaseCollect(userId, list.get(i)));
//        }
        return list;
    }

    private BaseModel dealBaseCollect(int userId, BaseModel baseModel) {
        User user = userDao.getUserById(userId);
        if(user.getStatus() != 1) return baseModel;
        String progress = collectDao.isBaseCollect(new CollectModel(userId, EModel.BASE, null, baseModel.getId(), null));
        if(progress != null){
            baseModel.setCollect(true);
        }
        return baseModel;
    }
}
