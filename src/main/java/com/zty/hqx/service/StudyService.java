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
    CollectDao collectDao;
    @Autowired
    ClassifyDao classifyDao;
    @Autowired
    ResourceDao resourceDao;
    @Autowired
    UserDao userDao;

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
            case BOOK: return JSON.toJSONString(getBookByKey(userId, word, num, limit));
            default: return JSON.toJSONString(getVideoByKey(userId, part, word, num, limit));
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
        List<ExamModel> list = examDao.getExamByTime(limit);
        list = dealExamClassify(list);
        return dealExamCollect(userId, list);
    }

    public List<ExamModel> getExamByLabel(int userId, int num, int limit, int label){
        List<ExamModel> list = examDao.getExamByLabel(num, limit, label);
        list = dealExamClassify(list);
        return dealExamCollect(userId, list);
    }

    public List<ExamModel> getExamByKey(int userId, String key, int num, int limit){
        List<ExamModel> list = examDao.getExamByTitle(key, num, limit);
        list = dealExamClassify(list);
        return dealExamCollect(userId, list);
    }

    public List<ExamModel> getExamById(int userId, int num, int limit){
        List<ExamModel> list = examDao.getExamById(num, limit);
        list = dealExamClassify(list);
        return dealExamCollect(userId, list);
    }

    public ExamModel getExam(int userId, int id){
        ExamModel model = examDao.getExam(id);
        if(model != null){
            model = dealExamClassify(model);
            model = dealExamCollect(userId, model);
        }
        return model;
    }

    public List<QuestionModel> getExamContent(int userId, int id){
        examDao.updateExamCount(id);
        return examDao.getExamContent(id);
    }

    private List<ExamModel> dealExamClassify(List<ExamModel> list){
        for (int i = 0; i < list.size(); i++) {
            list.set(i, dealExamClassify(list.get(i)));
        }
        return list;
    }

    private ExamModel dealExamClassify(ExamModel model){
        ClassifyModel classifyModel = classifyDao.getClassifyByNum(model.getLabel().getNum(), EStudyPart.EXAM.getEnglish());
        model.updateClassify(classifyModel);
        return model;
    }

    private List<ExamModel> dealExamCollect(int userId, List<ExamModel> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, dealExamCollect(userId, list.get(i)));
        }
        return list;
    }

    private ExamModel dealExamCollect(int userId, ExamModel model) {
        User user = userDao.getUserById(userId);
        if(user.getStatus() != 1) return model;
        String progress = collectDao.isCollect(new CollectModel(userId, EModel.STUDY, EStudyPart.EXAM, model.getId(), null));
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
        } catch (Exception e){}
        String file = ZtyUtil.dealSqlPathToFile(model.getFileUrl());
        FileUtil.deleteFile(file);
    }

    public void insertBook(BookModel model){
        bookDao.insertBook(model);
    }

    //获取书籍
    public List<BookModel> getBookByTime(int userId, int limit){
        List<BookModel> list = bookDao.getBookByTime(limit);
        list = dealBookClassify(list);
        return dealBookCollect(userId, list);
    }

    public List<BookModel> getBookByCount(int userId, int limit){
        List<BookModel> list = bookDao.getBookByCount(limit);
        list = dealBookClassify(list);
        return dealBookCollect(userId, list);
    }

    public List<BookModel> getBookByLabel(int userId, int num, int limit, int label){
        List<BookModel> list = bookDao.getBookByLabel(num, limit, label);
        list = dealBookClassify(list);
        return dealBookCollect(userId, list);
    }

    public List<BookModel> getBookByKey(int userId, String key, int num, int limit){
        List<BookModel> list = bookDao.getBookByTitle(key, num, limit);
        list = dealBookClassify(list);
        return dealBookCollect(userId, list);
    }

    public List<BookModel> getBookById(int userId, int num, int limit){
        List<BookModel> list = bookDao.getBookById(num, limit);
        list = dealBookClassify(list);
        return dealBookCollect(userId, list);
    }

    public BookModel getBook(int userId, int id){
        BookModel model = bookDao.getBook(id);
        System.out.println(model);
        if(model != null) {
            model = dealBookClassify(model);
            model = dealBookCollect(userId, model);
            //todo 应该在content处
            bookDao.updateBookCount(id);
        }
        return model;
    }

    private List<BookModel> dealBookClassify(List<BookModel> list){
        for (int i = 0; i < list.size(); i++) {
            list.set(i, dealBookClassify(list.get(i)));
        }
        return list;
    }

    private BookModel dealBookClassify(BookModel model){
        ClassifyModel classifyModel = classifyDao.getClassifyByNum(model.getLabelNum(), EStudyPart.BOOK.getEnglish());
        model.updateClassify(classifyModel);
        return model;
    }

    private List<BookModel> dealBookCollect(int userId, List<BookModel> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, dealBookCollect(userId, list.get(i)));
        }
        return list;
    }

    private BookModel dealBookCollect(int userId, BookModel model) {
        User user = userDao.getUserById(userId);
        if(user.getStatus() != 1) return model;
        String progress = collectDao.isCollect(new CollectModel(userId, EModel.STUDY, EStudyPart.BOOK, model.getId(), null));
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
        } catch (Exception e){}
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

    public List<VideoModel> getVideoByTime(int userId, EStudyPart part, int limit){
        List<VideoModel> list = videoDao.getVideoByTime(part.getEnglish(), limit);
        list = dealVideoClassify(part.getEnglish(), list);
        return dealVideoListCollect(userId, part, list);
    }

    public List<VideoModel> getVideoByCount(int userId, EStudyPart part, int limit){
        List<VideoModel> list = videoDao.getVideoByCount(part.getEnglish(), limit);
        list = dealVideoClassify(part.getEnglish(), list);
        return dealVideoListCollect(userId, part, list);
    }

    public List<VideoModel> getVideoByLabel(int userId, EStudyPart part, int num, int limit, int label){
        List<VideoModel> list = videoDao.getVideoByLabel(part.getEnglish(), num, limit, label);
        list = dealVideoClassify(part.getEnglish(), list);
        return dealVideoListCollect(userId, part, list);
    }

    public List<VideoModel> getVideoByKey(int userId, EStudyPart part, String key, int num, int limit){
        List<VideoModel> list = videoDao.getVideoByTitle(part.getEnglish(), key, num, limit);
        list = dealVideoClassify(part.getEnglish(), list);
        return dealVideoListCollect(userId, part, list);
    }

    public List<VideoModel> getVideoById(int userId, EStudyPart part, int num, int limit) {
        List<VideoModel> list = videoDao.getVideoById(part.getEnglish(),num, limit);
        list = dealVideoClassify(part.getEnglish(), list);
        return dealVideoListCollect(userId, part, list);
    }

    public VideoModel getVideo(int userId, EStudyPart part, int id){
        VideoModel model = videoDao.getVideo(part.getEnglish(), id);
        System.out.println(model);
        if(model != null) {
            dealVideoClassify(part.getEnglish(), model);
            model = dealVideoModelCollect(userId, part, model);
        }
        return model;
    }

    public List<VideoContentModel> getVideoContent(EStudyPart part, int id){
        videoDao.updateVideoCount(part.getEnglish(), id);
        return videoDao.getVideoContent(part.getEnglish(), id);
    }

    private List<VideoModel> dealVideoClassify(String subEnglish, List<VideoModel> list){
        for (int i = 0; i < list.size(); i++) {
            list.set(i, dealVideoClassify(subEnglish, list.get(i)));
        }
        return list;
    }

    private VideoModel dealVideoClassify(String subEnglish, VideoModel model){
        ClassifyModel classifyModel = classifyDao.getClassifyByNum(model.getLabelNum(), subEnglish);
        model.updateClassify(classifyModel);
        return model;
    }

    private List<VideoModel> dealVideoListCollect(int userId, EStudyPart part, List<VideoModel> list) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, dealVideoModelCollect(userId, part, list.get(i)));
        }
        return list;
    }

    private VideoModel dealVideoModelCollect(int userId, EStudyPart part, VideoModel videoModel) {
        User user = userDao.getUserById(userId);
        if(user.getStatus() != 1) return videoModel;
        String progress = collectDao.isCollect(new CollectModel(userId, EModel.STUDY, part, videoModel.getId(), null));
        if(progress != null){
            videoModel.setCollect(true);
            videoModel.setProgress(progress);
        }
        return videoModel;
    }
}
