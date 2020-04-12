package com.zty.hqx.config;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.dao.*;
import com.zty.hqx.model.BaseModel;
import com.zty.hqx.model.BookModel;
import com.zty.hqx.model.ExamModel;
import com.zty.hqx.model.VideoModel;
import com.zty.hqx.util.ZtyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务
 * 每天1点 计算当天的总浏览量
 *
 * 暂不使用
 * */
@Configuration
//@EnableScheduling
public class TimerTask {
    @Autowired
    StudyExamDao examDao;
    @Autowired
    StudyBookDao bookDao;
    @Autowired
    StudyVideoDao videoDao;
    @Autowired
    CountDao countDao;
    @Autowired
    BaseDao baseDao;

    //昨天时间
    private String yesterdayTime = ZtyUtil.getYesterday();

    /**
     * 获取每一项当天的浏览量 存入count 表中
     * 清空每一项的浏览量
     * 每天1点执行
     * */
    @Scheduled(cron = "0 0 1 * * ? *")
    private void configureTasks() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        //基地
        List<BaseModel> list = baseDao.getAllBaseCount();
        for(BaseModel model : list) {
            countDao.insertCount(EModel.BASE.getType(), 0, 0, model.getId(), yesterdayTime,  model.getCount());
        }

        //试卷
        List<ExamModel> list1 = examDao.getAllExamCount();
        for(ExamModel model : list1) {
            countDao.insertCount(EModel.STUDY.getType(), EStudyPart.EXAM.getType(), model.getLabel().getNum() , model.getId(), yesterdayTime,  model.getCount());
        }

        //书籍
        List<BookModel> list2 = bookDao.getAllBookCount();
        for(BookModel model : list2) {
            countDao.insertCount(EModel.STUDY.getType(), EStudyPart.BOOK.getType(), model.getLabel().getNum() , model.getId(), yesterdayTime,  model.getCount());
        }

        //视频
        setVideoCount(EStudyPart.TELEPLAY);
        setVideoCount(EStudyPart.FILM);
        setVideoCount(EStudyPart.VARIETY);
        setVideoCount(EStudyPart.DOCUMENTARY);
        setVideoCount(EStudyPart.DRAMA);
        //更新当天浏览量
        updateAllCount();
    }

    //更新当天浏览量
    private void updateAllCount(){
        //更新 基地 浏览量
        countDao.updateAllCount("base");
        //更新 study 浏览量
        for (EStudyPart part : EStudyPart.values()){
            countDao.updateAllCount(part.getEnglish());
        }
    }

    //获取视频的浏览量 并存储
    private void setVideoCount(EStudyPart videoPart){
        List<VideoModel> list = videoDao.getAllVideoCount(videoPart.getEnglish());
        for(VideoModel model : list) {
            countDao.insertCount(EModel.STUDY.getType(), videoPart.getType(), model.getLabel().getNum() , model.getId(), yesterdayTime,  model.getCount());
        }
    }

}
