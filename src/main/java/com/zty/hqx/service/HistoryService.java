package com.zty.hqx.service;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.dao.HistoryDao;
import com.zty.hqx.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {
    @Autowired
    HistoryDao historyDao;
    @Autowired
    StudyService studyService;
    @Autowired
    BaseService baseService;
    /**
     * 插入历史记录
     * */
    public void insertHistory(int userId, int modelId, int partId, int id) {
        String time = historyDao.getOneHistory(userId, modelId, partId, id);
        if(time == null){
            historyDao.insertHistory(userId, modelId, partId, id);
        } else {
            historyDao.updateHistory(userId, modelId, partId, id);
        }
    }

    public List<VideoModel> getVideoHistory(int userId, String part, int num, int limit){
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<Integer> idList = historyDao.getHistory(userId, EModel.STUDY.getType(), epart.getType(), num, limit);
        if(idList == null || idList.size() < 1) return null;
        List<VideoModel> modelList = new ArrayList<>();
        for(int id : idList){
            modelList.add(studyService.getVideo(userId, epart, id));
        }
        return modelList;
    }

    public BookModel getBookHistory(int userId, int num, int limit) {
        List<Integer> list = historyDao.getHistory(userId, EModel.STUDY.getType(), EStudyPart.BOOK.getType(), num, limit);
        if(list == null || list.size() < 1) return null;
        return studyService.getBook(userId, list.get(0));
    }

    public List<VideoModel> getVideoHistoryByTime(int userId, String part, String time){
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
         List<Integer> idList = historyDao.getHistoryByTime(userId, EModel.STUDY.getType(), epart.getType(), time);
        if(idList == null || idList.size() < 1) return null;
        List<VideoModel> modelList = new ArrayList<>();
        for(int id : idList){
            modelList.add(studyService.getVideo(userId, epart, id));
        }
        return modelList;
    }

    public void deleteAllHistory(int model, int part, int id){
        if(model == EModel.BASE.getType()){
            historyDao.deleteAllBaseHistory(model, id);
        } else {
            historyDao.deleteAllHistory(model, part, id);
        }
    }
}
