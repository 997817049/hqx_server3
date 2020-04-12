package com.zty.hqx;

import com.alibaba.fastjson.JSONArray;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.dao.*;
import com.zty.hqx.model.*;
import com.zty.hqx.service.CountService;
import com.zty.hqx.util.ZMd5Util;
import com.zty.hqx.util.ZtyUtil;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.print.Book;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zty.hqx.util.ZtyUtil.getRandomName;

@SpringBootTest
class HqxApplicationTests {
    @Autowired
    StudyExamDao examDao;
    @Autowired
    StudyBookDao bookDao;
    @Autowired
    StudyVideoDao videoDao;
    @Autowired
    CountDao countDao;
    @Autowired
    CountService countService;
    @Autowired
    BaseDao baseDao;

    @Test
    void Test7() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(df.parse("2020-03-30"));
        cal.add(Calendar.DATE, - 1);
        Date d = cal.getTime();
        //插入的时间
        String time = df.format(d);
        System.out.println("插入时间" + time);
    }

    @Test
    void Test6() throws ParseException {
        //全部数据
        List<BaseModel> list = baseDao.getAllBaseCount();
        List<ExamModel> list1 = examDao.getAllExamCount();
        List<BookModel> list2 = bookDao.getAllBookCount();
        List<VideoModel> list3 = videoDao.getAllVideoCount(EStudyPart.TELEPLAY.getEnglish());
        List<VideoModel> list4 = videoDao.getAllVideoCount(EStudyPart.FILM.getEnglish());
        List<VideoModel> list5 = videoDao.getAllVideoCount(EStudyPart.VARIETY.getEnglish());
        List<VideoModel> list6 = videoDao.getAllVideoCount(EStudyPart.DOCUMENTARY.getEnglish());
        List<VideoModel> list7 = videoDao.getAllVideoCount(EStudyPart.DRAMA.getEnglish());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Random random = new Random();
        for(int i = 1; i < 31; i++){
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse("2020-03-30"));
            cal.add(Calendar.DATE, - i);
            Date d = cal.getTime();
            //插入的时间
            String time = df.format(d);
            System.out.println("插入时间" + time);

            for(BaseModel model : list) {
                int num = random.nextInt(1000) % (1000 + 1);
                countDao.insertCount(EModel.BASE.getType(), 0, 0, model.getId(), time, num);
            }
            for(ExamModel model : list1) {
                int num = random.nextInt(1000) % (1000 + 1);
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.EXAM.getType(), model.getLabel().getNum() , model.getId(), time, num);
            }
            for(BookModel model : list2) {
                int num = random.nextInt(1000) % (1000 + 1);
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.BOOK.getType(), model.getLabel().getNum() , model.getId(), time, num);
            }
            for(VideoModel model : list3) {
                int num = random.nextInt(1000) % (1000 + 1);
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.TELEPLAY.getType(), model.getLabel().getNum() , model.getId(), time, num);
            }
            for(VideoModel model : list4) {
                int num = random.nextInt(1000) % (1000 + 1);
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.FILM.getType(), model.getLabel().getNum() , model.getId(), time, num);
            }
            for(VideoModel model : list5) {
                int num = random.nextInt(1000) % (1000 + 1);
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.VARIETY.getType(), model.getLabel().getNum() , model.getId(), time, num);
            }
            for(VideoModel model : list6) {
                int num = random.nextInt(1000) % (1000 + 1);
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.DOCUMENTARY.getType(), model.getLabel().getNum() , model.getId(), time, num);
            }
            for(VideoModel model : list7) {
                int num = random.nextInt(1000) % (1000 + 1);
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.DRAMA.getType(), model.getLabel().getNum() , model.getId(), time, num);
            }
        }

        //随机count


        countDao.updateAllCount("base");
        //更新 study 浏览量
        for (EStudyPart part : EStudyPart.values()){
            countDao.updateAllCount(part.getEnglish());
        }
    }

    @Test
    void Test5(){
        for(int i = 0; i < 20; i++){
            String newName = getRandomName();
            System.out.println(System.currentTimeMillis());
        }
    }

    @Test
    void Test4(){
//        String str1 = "{\"id\":0,\"title\":\"标题\",\"label\":{\"num\":2},\"collect\":false,\"count\":0,\"time\":12,}";
        String str = "{\"id\":11,\"title\":\"新时代公民道德建设实施纲要专项测试k接口\", \"label\":\"2\", \"count\":0,\"time\":12, \"collect\":false}";
//        ExamModel examModel = (ExamModel) JSONObject.parse(str1);
//        ExamModel examModel = new ExamModel("标题", new ClassifyModel(2), 12);
//        System.out.println(examModel);
        ExamModel examModel1 = (ExamModel) JSONObject.toBean(JSONObject.fromObject(str), ExamModel.class);
        System.out.println(examModel1);
//        System.out.println((ExamModel) JSONObject.parse(rs));
    }

    @Test
    void test3(){
        String str = "[{\"num\":\"2\",\"videoUrl\":\"video158349131623985.mp4\"}]";
        List<VideoContentModel> videoList = JSONArray.parseArray(str, VideoContentModel.class);
        System.out.println(videoList);
    }

    @Test
    void test2(){
        VideoModel model = new VideoModel("标题", new ClassifyModel(2), "演员", 1, "简介", "images/15833995293606210.jpg");
        videoDao.insertVideo("variety", model);
        System.out.println(model);
    }

    @Test
    void test1() {
        BookModel model = new BookModel("标题", new ClassifyModel(2), "作者", "简介", "images/study/literature/1.jpeg", "文件路径");
        bookDao.insertBook(model);
    }

    @Test
    void contextLoads() {
        String md5 = ZMd5Util.getFileMD5("E:\\graduationProject\\hqx\\resource\\images\\15833840337405410.bmp");
        System.out.println(md5);
    }
}
