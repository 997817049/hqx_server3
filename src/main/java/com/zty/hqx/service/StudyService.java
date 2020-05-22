package com.zty.hqx.service;

import com.alibaba.fastjson.JSON;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.dao.*;
import com.zty.hqx.model.*;
import com.zty.hqx.util.FileUtil;
import com.zty.hqx.util.ZtyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    CountDao countDao;
    @Autowired
    CollectService collectService;
    @Autowired
    HistoryService historyService;
    @Autowired
    StudyService studyService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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

    public void updateExamContent(int id, ExamContentModel model) {
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

    public void insertExamContent(int id, List<ExamContentModel> list){
        for (ExamContentModel model : list) {
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

    public List<ExamContentModel> getExamContent(int id){
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
        int id = model.getId();
        BookModel oldBook = bookDao.getBook(id);
        String oldPic = oldBook.getPicUrl();
        if(!oldPic.equals(model.getPicUrl())){
            //删除外链
            deletePic(oldPic);
        }
        String oldFile = oldBook.getFileUrl();
        if(!oldFile.equals(model.getFileUrl())){
            //删除书籍
            deleteBook(oldFile);
        }
        bookDao.updateBook(model);
    }

    public void deleteBook(int id){
        BookModel model = bookDao.getBook(id);
        //删除记录
        bookDao.deletebook(id);
        //删除图片
        deletePic(model.getPicUrl());
        //删除书籍
        deleteBook(model.getFileUrl());
        //删除全部收藏
        collectService.deleteAllCollect(EModel.STUDY, EStudyPart.BOOK, id);
        //删除全部历史记录
        historyService.deleteAllHistory(EModel.STUDY.getType(), EStudyPart.BOOK.getType(), id);
    }

    public void insertBook(BookModel model){
        bookDao.insertBook(model);
    }

    //获取书籍
    public List<BookModel> getBookByTime(int limit){
        return bookDao.getBookByTime(limit);
    }

    public List<BookModel> getBookByCount(int userId, int limit){
        String time = ZtyUtil.getYesterday();
        List<Integer> bookId = countDao.getBookHot(time, limit);
        List<BookModel> list = new ArrayList<>();
        for (int id : bookId){
            BookModel model = studyService.getBook(userId, id);
            if(model != null){
                list.add(model);
            }
        }
        return list;
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

    public void updateVideoContent(String part, int id, int oldNum, int newNum, String videoUrl) {
        if(oldNum == 0) {//插入
            videoDao.insertVideoContent(part, id, newNum, videoUrl);
            return;
        }
        //更新
        VideoContentModel videoContentModel = videoDao.getVideoContentByNum(part,id, oldNum);
        String oldVideo = videoContentModel.getVideoUrl();
        if(!oldVideo.equals(videoUrl)){//更新视频
            deleteVideo(oldVideo);
        }
        //更新集数
        videoDao.updateVideoContent(part, id, oldNum, newNum, videoUrl);
    }

    public void updateVideo(String part, VideoModel model) {
        int id = model.getId();
        VideoModel oldVideo = videoDao.getVideo(part, id);
        String oldPic = oldVideo.getPicUrl();
        if(!oldPic.equals(model.getPicUrl())){
            //删除外链
            deletePic(oldPic);
        }
        videoDao.updateVideo(part, model);
    }

    //删除一个视频 并删除其全部内容
    public void deleteVideo(String part, int id){
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        VideoModel model = videoDao.getVideo(part, id);
        List<VideoContentModel> list = videoDao.getVideoContent(part, id);
        System.out.println(list);
        //删除对应的全部视频
        for(VideoContentModel contentModel : list){
            deleteVideo(contentModel.getVideoUrl());
        }
        //删除记录
        videoDao.deleteVideoAllContent(part, id);
        videoDao.deleteVideo(part, id);
        //删除外链
        deletePic(model.getPicUrl());
        //删除全部收藏
        collectService.deleteAllCollect(EModel.STUDY, epart, id);
        //删除全部历史记录
        historyService.deleteAllHistory(EModel.STUDY.getType(), epart.getType(), id);
    }

    //删除一个视频的某一集
    public void deleteVideoContent(String part, int id, int num){
        VideoContentModel model = videoDao.getVideoContentByNum(part, id, num);
        String videoUrl = model.getVideoUrl();
        deleteVideo(videoUrl);
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
        String time = ZtyUtil.getYesterday();
        List<Integer> videoId = countDao.getVideoHot(part.getEnglish(), part.getType(), time, limit);
        List<VideoModel> list = new ArrayList<>();
        for (int id : videoId){
            VideoModel model = videoDao.getVideo(part.getEnglish(), id);
            if(model != null){
                list.add(model);
            }
        }
        return list;
    }

    public List<VideoModel> getVideoByLabel(EStudyPart part, int num, int limit, int label){
        return videoDao.getVideoByLabel(part.getEnglish(), num, limit, label);
    }

    public List<VideoModel> getVideoByKey(EStudyPart part, String key, int num, int limit){
        return videoDao.getVideoByTitle(part.getEnglish(), key, num, limit);
    }

    public List<VideoModel> getVideoByNum(EStudyPart part, int num, int limit) {
        return videoDao.getVideoById(part.getEnglish(), num, limit);
    }

    public VideoModel getVideo(int userId, EStudyPart part, int id){
        VideoModel model = videoDao.getVideo(part.getEnglish(), id);
        if(model != null) {
            model = dealVideoModelCollect(userId, part, model);
        }
        return model;
    }

    public VideoModel manageGetVideo(String part, int id){
        return videoDao.getVideo(part, id);
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

    private void deletePic(String pic){
        //删除图片
        try {
            resourceDao.deletePic(pic);
            String path = ZtyUtil.dealSqlPathToFile(pic);
            boolean rs = FileUtil.deleteFile(path);
            logger.info("删除pic文件" + (rs ? "成功" : "失败") + "\n路径：" + path);
        } catch (Exception ignored) {
            logger.warn("删除pic外链异常");
        }
    }

    private void deleteBook(String book){
        //删除书籍
        try {
            resourceDao.deleteBook(book);
            String path = ZtyUtil.dealSqlPathToFile(book);
            boolean rs = FileUtil.deleteFile(path);
            logger.info("删除book文件" + (rs ? "成功" : "失败") + "\n路径：" + path);
        } catch (Exception ignored) {
            logger.warn("删除book外链异常");
        }
    }

    private void deleteVideo(String video){
        try {
            resourceDao.deleteVideo(video);//删除外链
            String path = ZtyUtil.dealSqlPathToFile(video);
            boolean rs = FileUtil.deleteFile(path);//删资源
            logger.info("删除视频文件" + (rs ? "成功" : "失败") + "\n路径：" + path);
        } catch (Exception ignored){
            logger.warn("删除视频外链异常");
        }
    }
}
