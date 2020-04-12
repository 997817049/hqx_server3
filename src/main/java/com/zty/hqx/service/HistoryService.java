package com.zty.hqx.service;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.dao.HistoryDao;
import com.zty.hqx.model.BookModel;
import com.zty.hqx.model.CollectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {
    @Autowired
    HistoryDao historyDao;
    @Autowired
    StudyService studyService;

    /**
     * @
     * */
    public void insertHistory(int userId, int modelId, int partId, int id) {
        historyDao.insertHistory(userId, modelId, partId, id);
    }

    public BookModel getBookRecentHistory(int userId, int limit) {
        CollectModel collectModel = historyDao.getRecentHistory(userId, EModel.STUDY.getType(), EStudyPart.BOOK.getType(), limit);
        if(collectModel == null) return null;
        BookModel bookModel = studyService.getBook(userId, collectModel.getId());
        bookModel.setCollect(true);
        bookModel.setProgress(collectModel.getProgress());
        return bookModel;
    }
}
