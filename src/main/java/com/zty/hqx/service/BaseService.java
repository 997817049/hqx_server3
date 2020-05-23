package com.zty.hqx.service;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.dao.*;
import com.zty.hqx.model.BaseModel;
import com.zty.hqx.model.CollectModel;
import com.zty.hqx.util.FileUtil;
import com.zty.hqx.util.ZtyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    CountDao countDao;
    @Autowired
    CollectService collectService;
    @Autowired
    HistoryService historyService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void updateBaseCount(int id){
        baseDao.updateBaseCount(id);
    }

    public void updateBase(BaseModel baseModel) {
        BaseModel oldModel = baseDao.getBaseById(baseModel.getId());
        String oldPic = oldModel.getPicUrl();
        if(!oldPic.equals(baseModel.getPicUrl())){
            try {
                //删除图片
                resourceDao.deletePic(oldPic);
                String path = ZtyUtil.dealSqlPathToFile(oldPic);
                boolean rs = FileUtil.deleteFile(path);
                logger.info("删除pic文件" + (rs ? "成功" : "失败") + "[" + path + "]");
            } catch (Exception ignored) {
                logger.warn("删除pic外链异常");
            }
        }
        String oldHtml = oldModel.getHtmlUrl();
        if(!oldHtml.equals(baseModel.getHtmlUrl())){
            //删除原来的html文件
            String path = ZtyUtil.dealSqlPathToFile(oldHtml);
            boolean rs = FileUtil.deleteFile(path);
            logger.info("删除html文件" + (rs ? "成功" : "失败") + "[" + path + "]");
        }
        baseDao.updateBase(baseModel);
    }

    public boolean deleteBase(int id) {
        BaseModel baseModel = baseDao.getBaseById(id);
        //删除记录
        baseDao.deleteBase(id);
        String pic = baseModel.getPicUrl();
        String html = baseModel.getHtmlUrl();
        try {
            //删除图片外链
            resourceDao.deletePic(pic);
            //删除图片文件
            String path = ZtyUtil.dealSqlPathToFile(pic);
            boolean rs = FileUtil.deleteFile(path);
            logger.info("删除pic文件" + (rs ? "成功" : "失败") + "[" + path + "]");
        } catch (Exception ignored) {
            logger.warn("删除pic外链异常");
        }finally {
            //删除htm文件
            String path = ZtyUtil.dealSqlPathToFile(html);
            boolean rs = FileUtil.deleteFile(path);
            logger.info("删除html文件" + (rs ? "成功" : "失败") + "[" + path + "]");
        }

        //删除全部收藏
        collectService.deleteAllCollect(EModel.BASE, null, id);
        //删除全部历史记录
        historyService.deleteAllHistory(EModel.BASE.getType(), 0, id);
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

    public List<BaseModel> getHotBase(int limit) {
        String time = ZtyUtil.getYesterday();
        return countDao.getBaseHot(time, limit);
    }

    public List<BaseModel> getBaseByAddress(String province, String city, int limit) {
        return baseDao.getBaseByAddress(province, city, limit);
    }

    public List<BaseModel> getBaseByNum(int num, int limit) {
        return baseDao.getBaseByNum(num, limit);
    }

    public List<BaseModel> getBase(int num, int limit) {
        return baseDao.getBase(num, limit);
    }

    public List<BaseModel> getBaseByKey(int num, int limit, String key) {
        return baseDao.getBaseByKey(key, num, limit);
    }

    private BaseModel dealBaseCollect(int userId, BaseModel baseModel) {
        String progress = collectDao.isBaseCollect(new CollectModel(userId, EModel.BASE, null, baseModel.getId(), null));
        if(progress != null){
            baseModel.setCollect(true);
        }
        return baseModel;
    }
}
