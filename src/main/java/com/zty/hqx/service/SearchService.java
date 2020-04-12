package com.zty.hqx.service;

import com.zty.hqx.dao.SearchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    @Autowired
    SearchDao searchDao;

    public void updateWordCount(int userId, String word) {
        searchDao.updateWordCount(word);//查找词 次数
        searchDao.insertUserWord(userId, word); //每个人查找什么词  存储
    }

    public boolean deleteHistoryWord(int userId){
        return searchDao.deleteHistoryWord(userId);
    }

    public List<String> getHistoryWord(int userId){
        return searchDao.getUserHistoryWord(userId);
    }

    public List<String> getHotWord(){
        return searchDao.getHotWord();
    }
}
