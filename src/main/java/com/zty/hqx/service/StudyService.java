package com.zty.hqx.service;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.dao.*;
import com.zty.hqx.model.*;
import com.zty.hqx.util.FileUtil;
import com.zty.hqx.util.ZtyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyService {
    @Autowired
    StudyExamDao examDao;
    @Autowired
    StudyBookDao bookDao;
    @Autowired
    StudyVideoDao videoDao;
    @Autowired
    ClassifyDao classifyDao;
    @Autowired
    ResourceDao resourceDao;
    @Autowired
    UserDao userDao;
    @Autowired
    CollectService collectService;
    @Autowired
    HistoryService historyService;

// <--------------------------------获取各个part的记录条数-------------------------------------------->

    public int getPartsCount(EStudyPart part) {
        switch (part) {
            case EXAM: return examDao.getExamCount();
            case BOOK: return bookDao.getBookCount();
            default: return videoDao.getVideoCount(part.getEnglish());
        }
    }

    //判断名字手法可用
    public boolean isTitleAvailable(EStudyPart part, String title){
        Object obj;
        switch (part){
            case EXAM: obj = examDao.isTitleAvailable(title);break;
            case BOOK: obj = bookDao.isTitleAvailable(title);break;
            default: obj = videoDao.isTitleAvailable(part.getEnglish(), title);break;
        }
        if(obj == null) return true;
        return false;
    }

//<--------------------------------根据key查找数据----------------------------------------------------->

    public String getStudyByKey(int userId, String word, EStudyPart part, int num, int limit){
        switch (part){
            case EXAM: return JSON.toJSONString(getExamByKey(userId, word, num, limit));
            case BOOK: return JSON.toJSONString(getBookByKey(word, num, limit));
            default: return JSON.toJSONString(getVideoByKey(part, word, num, limit));
        }
    }

// --------------------------------------------试题----------------------------------------------

    public void updateExamContent(int id, QuestionModel model) {
        if(null == examDao.getQuestion(id, model.getNum())){
            examDao.insertExamContent(id, model);
        } else {
            examDao.updateExamContent(id, model);
        }
    }

    public void updateExam(ExamModel model) {
        examDao.updateExam(model);
    }

    public void deleteExam(int id){
        //删除对应的全部题目
        examDao.deleteExamAllContent(id);
        examDao.deleteExam(id);
        //删除全部收藏
        collectService.deleteAllCollect(EModel.STUDY, EStudyPart.EXAM, id);
        //删除全部历史记录
        historyService.deleteAllHistory(EModel.STUDY.getType(), EStudyPart.EXAM.getType(), id);
    }

    public void deleteExamContent(int id, List<Integer> list1){
        for(int i : list1){
            examDao.deleteExamContent(id, i);
        }
    }

    public void insertExam(ExamModel model){
        examDao.insertExam(model);
    }

    public void insertExamContent(int id, List<QuestionModel> list){
        for (QuestionModel model : list) {
            examDao.insertExamContent(id, model);
        }
    }

    //获取试题
    public List<ExamModel> getExamByTime(int userId, int limit){
        return dealExamCollect(userId, examDao.getExamByTime(limit));
    }

    public List<ExamModel> getExamByLabel(int userId, int num, int limit, int label){
        return dealExamCollect(userId, examDao.getExamByLabel(num, limit, label));
    }

    public List<ExamModel> getExamByKey(int userId, String key, int num, int limit){
        int status = userDao.getUserById(userId).getStatus();
        List<ExamModel> list = examDao.getExamByTitle(key, num, limit);
        if(status != 1){
            list = dealExamCollect(userId, list);
        }
        return list;
    }

    //控制端获取数据
    public List<ExamModel> getExamByNum(int num, int limit){
        return examDao.getExamByNum(num, limit);
    }

    public ExamModel getExamById(int userId, int id){
        ExamModel model = examDao.getExamById(id);
        if(model != null){
            model = dealExamCollect(userId, model);
        }
        return model;
    }

    public List<QuestionModel> getExamContent(int id){
        examDao.updateExamCount(id);
        return examDao.getExamContent(id);
    }

    private List<ExamModel> dealExamCollect(int userId, List<ExamModel> list) {
        List<ExamModel> rsList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            rsList.add(dealExamCollect(userId, list.get(i)));
        }
        return rsList;
    }

    private ExamModel dealExamCollect(int userId, ExamModel model) {
        String progress = dealCollect(userId, EStudyPart.EXAM, model.getId());
        if(progress != null){
            model.setCollect(true);
            model.setProgress(progress);
        }
        return model;
    }

// --------------------------------------------书籍----------------------------------------------

    public void updateBook(BookModel model) {
        bookDao.updateBook(model);
    }

    public void deleteBook(int id){
        BookModel model = bookDao.getBook(id);
        bookDao.deletebook(id);
        String pic = model.getPicUrl();
        try {
            resourceDao.deletePic(pic);
            FileUtil.deleteFile(ZtyUtil.dealSqlPathToFile(pic));
        } catch (Exception e){
        } finally {
            String file = ZtyUtil.dealSqlPathToFile(model.getFileUrl());
            FileUtil.deleteFile(file);
            //删除全部收藏
            collectService.deleteAllCollect(EModel.STUDY, EStudyPart.BOOK, id);
            //删除全部历史记录
            historyService.deleteAllHistory(EModel.STUDY.getType(), EStudyPart.BOOK.getType(), id);
        }
    }

    public void insertBook(BookModel model){
        bookDao.insertBook(model);
    }

    //获取书籍
    public List<BookModel> getBookByTime(int limit){
        return bookDao.getBookByTime(limit);
    }

    public List<BookModel> getBookByCount(int limit){
        return bookDao.getBookByCount(limit);
    }

    public List<BookModel> getBookByLabel(int num, int limit, int label){
        return bookDao.getBookByLabel(num, limit, label);
    }

    public List<BookModel> getBookByKey(String key, int num, int limit){
        return bookDao.getBookByTitle(key, num, limit);
    }

    public List<BookModel> getBookByNum(int num, int limit){
        return bookDao.getBookByNum(num, limit);
    }

    public BookModel getBook(int userId, int id){
        BookModel model = bookDao.getBook(id);
        System.out.println(model);
        if(model != null) {
            model = dealBookCollect(userId, model);
        }
        return model;
    }

    private BookModel dealBookCollect(int userId, BookModel model) {
        String progress = dealCollect(userId, EStudyPart.BOOK, model.getId());
        if(progress != null){
            model.setCollect(true);
            model.setProgress(progress);
        }
        return model;
    }

// ---------------------------------------视频----------------------------------------------

    public void updateVideoContentNum(String part, int id, int oldNum, int newNum, String videoUrl) {
        if(oldNum == 0) {//插入
            videoDao.insertVideoContent(part, id, newNum, videoUrl);
        } else {//更新
            videoDao.updateVideoContentNum(part, id, oldNum, newNum, videoUrl);
        }
    }

    public void updateVideo(String part, VideoModel model) {
        videoDao.updateVideo(part, model);
    }

    //删除一个视频 并删除其全部内容
    public void deleteVideo(String part, int id){
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        VideoModel model = videoDao.getVideo(part, id);
        List<VideoContentModel> list = videoDao.getVideoContent(part, id);
        //删除对应的全部视频
        for(VideoContentModel contentModel : list){
            deleteVideoContent(part, id, contentModel);
        }
        videoDao.deleteVideo(part, id);
        String pic = model.getPicUrl();
        try {
            resourceDao.deletePic(pic);
            FileUtil.deleteFile(ZtyUtil.dealSqlPathToFile(pic));
        } catch (Exception e){

        }finally {
            //删除全部收藏
            collectService.deleteAllCollect(EModel.STUDY, epart, id);
            //删除全部历史记录
            historyService.deleteAllHistory(EModel.STUDY.getType(), epart.getType(), id);
        }
    }

    //删除一个视频的某一集
    public void deleteVideoContent(String part, int id, int num){
        VideoContentModel model = videoDao.getVideoContent2(part, id, num);
        deleteVideoContent(part, id, model);
    }

    //删除一个视频的某一集
    public void deleteVideoContent(String part, int id, VideoContentModel model){
        String videoUrl = model.getVideoUrl();
        try {
            resourceDao.deleteVideo(videoUrl);//删除外链
            videoUrl = ZtyUtil.dealSqlPathToFile(videoUrl);
            FileUtil.deleteFile(videoUrl);//删资源
        } catch (Exception e){}
        videoDao.deleteVideoContent(part, id, model.getNum());
    }

    public void insertVideo(String part, VideoModel videoModel){
        videoDao.insertVideo(part, videoModel);
    }

    public void insertVideoContent(String part, int id, List<VideoContentModel> list){
        for(VideoContentModel model : list){
            videoDao.insertVideoContent(part, id, model.getNum(), model.getVideoUrl());
        }
    }

    public List<VideoModel> getVideoByTime(EStudyPart part, int limit){
        return videoDao.getVideoByTime(part.getEnglish(), limit);
    }

    public List<VideoModel> getVideoByCount(EStudyPart part, int limit){
        return videoDao.getVideoByCount(part.getEnglish(), limit);
    }

    public List<VideoModel> getVideoByLabel(EStudyPart part, int num, int limit, int label){
        return videoDao.getVideoByLabel(part.getEnglish(), num, limit, label);
    }

    public List<VideoModel> getVideoByKey(EStudyPart part, String key, int num, int limit){
        return videoDao.getVideoByTitle(part.getEnglish(), key, num, limit);
    }

    public List<VideoModel> getVideoByNum(EStudyPart part, int num, int limit) {
        return videoDao.getVideoById(part.getEnglish(),num, limit);
    }

    public VideoModel getVideo(int userId, EStudyPart part, int id){
        VideoModel model = videoDao.getVideo(part.getEnglish(), id);
        System.out.println(model);
        if(model != null) {
            model = dealVideoModelCollect(userId, part, model);
        }
        return model;
    }

    public List<VideoContentModel> getVideoContent(EStudyPart part, int id){
        videoDao.updateVideoCount(part.getEnglish(), id);
        return videoDao.getVideoContent(part.getEnglish(), id);
    }

    private VideoModel dealVideoModelCollect(int userId, EStudyPart part, VideoModel videoModel) {
        String progress = dealCollect(userId, part, videoModel.getId());
        if(progress != null){
            videoModel.setCollect(true);
            videoModel.setProgress(progress);
        }
        return videoModel;
    }

    private String dealCollect(int userId, EStudyPart part, int id){
        return collectService.isCollect(new CollectModel(userId, EModel.STUDY, part, id, null));
    }
}
