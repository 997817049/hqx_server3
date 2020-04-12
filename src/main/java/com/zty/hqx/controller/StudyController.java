package com.zty.hqx.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zty.hqx.annotation.IsPartName;
import com.zty.hqx.classify.*;
import com.zty.hqx.model.*;
import com.zty.hqx.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
public class StudyController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    StudyService studyService;
    @Autowired
    HistoryService historyService;
    @Autowired
    ClassifyService classifyService;
    @Autowired
    ResourceService resourceService;
    @Autowired
    RedisUtil redisUtil;

    //判断名字是否能用
    @RequestMapping("/test/video")
    @ResponseBody
    public Result<String> isVideoExist(String md5){
        logger.info("检查" + md5 + "的视频是否存在");
        String path = resourceService.isExistVideo(md5);
        if(path != null) {
            return Result.success(path);
        }
        return Result.error();
    }

    //判断名字是否能用
    @RequestMapping("/test/study")
    @ResponseBody
    public boolean isTitleAvailable(String part, String title){
        logger.info("检查" + part + "的名称是否可用" + title);
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        boolean rs = studyService.isTitleAvailable(epart, title);
        return rs;
    }

    //获取分类
    @RequestMapping(value = "/classify/study")
    @ResponseBody
//    @Cacheable(value="study_classify", key="#part")
    public Result<List<ClassifyModel>> getClassify(String part){
        logger.info("获取" + part + "的分类");
        List<ClassifyModel> list = classifyService.getClassify(part);
        return Result.success(list);
    }

// <----------------------------------控制端修改资源----------------------------------------->

    @RequestMapping("/update/study/exam")
    @ResponseBody
//    @Caching(evict = {
//            @CacheEvict(value={"app_study_exam", "manage_study"}, allEntries = true),
//            @CacheEvict(value={"app_study_exam_info"}, key="'id_' + #id"),
//            @CacheEvict(value="collect", allEntries = true),
//            @CacheEvict(value = "search", allEntries = true)
//    })
    public void updateExam(int id, String title, int label, int time) {
        logger.info("更新试卷 id=" + id + "[" + title + "]" + "label: " + label + time + "分钟");
        ExamModel model = new ExamModel(id, title, new ClassifyModel(label), time);
        studyService.updateExam(model);
    }

    @RequestMapping("/update/study/book")
    @ResponseBody
//    @Caching(evict = {
//            @CacheEvict(value={"app_study_book", "manage_study"}, allEntries = true),
//            @CacheEvict(value={"app_study_book_info"}, key="'id_' + #id"),
//            @CacheEvict(value="collect", allEntries = true),
//            @CacheEvict(value = "search", allEntries = true),
//            @CacheEvict(value="history_book", allEntries = true)
//    })
    public void updateBook(int id,
                            @NotBlank(message = "title不能为空")
                           @RequestParam("title") String title,
                           int label,
                           @NotBlank(message = "author不能为空")
                           @RequestParam("author") String author,
                           @NotBlank(message = "synopsis不能为空")
                           @RequestParam("synopsis") String synopsis,
                           @RequestParam("pic") String pic,
                           @RequestParam("file") String file){
        logger.info("更新书籍id=" + id + "["+ title + "]**" + author + "**" + synopsis);
        BookModel model = new BookModel(id, title, new ClassifyModel(label), author, synopsis, pic, file);
        studyService.updateBook(model);
    }

    @RequestMapping("/update/study/video")
    @ResponseBody
//    @Caching(evict = {
//            @CacheEvict(value={"app_study_video", "manage_study"}, allEntries = true),
//            @CacheEvict(value={"app_study_video_info"}, key="#part + '_id_' + #id"),
//            @CacheEvict(value="collect", allEntries = true),
//            @CacheEvict(value = "search", allEntries = true)
//    })
    public void updateVideo(String part, int id,
                            @NotBlank(message = "title不能为空")
                            @RequestParam("title") String title,
                            int label,
                            @NotBlank(message = "actor不能为空")
                            @RequestParam("actor") String actor,
                            int num,
                            @NotBlank(message = "synopsis不能为空")
                            @RequestParam("synopsis") String synopsis,
                            @RequestParam("pic") String pic){
        logger.info("更新视频 " + part + "：id = " + id + "[" + label + "]:" + title);
        VideoModel model = new VideoModel(id, title, new ClassifyModel(label), actor, num, synopsis, pic);
        studyService.updateVideo(part, model);
    }

    @RequestMapping("/update/study/exam/content")
    @ResponseBody
//    @CacheEvict(value="app_study_exam_content", key="'id_' + #id")
    public void updateExamContent(@DecimalMin("0") int id,
                                  @DecimalMin("0") int num,
                                  int type,
                                  String question,
                                  String answer,
                                  String analysis,
                                  String optionA,
                                  String optionB,
                                  String optionC,
                                  String optionD) {
        logger.info("修改id[" + id + "]试卷的第" + num + "题");
        QuestionModel model = new QuestionModel(num, type, question, answer, analysis, optionA, optionB, optionC, optionD);
        studyService.updateExamContent(id, model);
    }

    @RequestMapping("/update/study/video/content")
    @ResponseBody
//    @CacheEvict(value="app_study_video_content", key="#part + '_id_' + #id")
    public void updateTvVideo(String part, int id, int oldNum, int newNum, @NotBlank String videoUrl) {
        logger.info("更新" + part + "中id=" + id + " num=" + oldNum + " 的值" + newNum + "**" + videoUrl);
        studyService.updateVideoContentNum(part, id, oldNum, newNum, videoUrl);
    }

// <-------------------------------------控制端删除资源----------------------------------------->

    @RequestMapping("/delete/study/exam")
    @ResponseBody
//    @CacheEvict(value="app_study_exam_content", key="'id_' + #id")
    public void deleteExamContent(int id,
                                  @NotNull @RequestParam("list") String list) {
        logger.info("删除id=" + id + "试卷");
        List<Integer> list1 = JSONArray.parseArray(list, Integer.class);
        studyService.deleteExamContent(id, list1);
    }

    @RequestMapping("/delete/study/video")
    @ResponseBody
//    @CacheEvict(value="app_study_video_content", key="#part + '_id_' + #id")
    public void deleteVideoContent(String part, int id,
                                   @NotNull @RequestParam("list") String listStr) {
        List<Integer> list = JSONArray.parseArray(listStr, Integer.class);
        for (int num : list){
            studyService.deleteVideoContent(part, id, num);
        }
    }

    @RequestMapping("/delete/study")
    @ResponseBody
    //todo  redis 缓存
//    @Caching(evict = {
//            @CacheEvict(value={"app_study_exam", "app_study_book", "app_study_video", "manage_study"}, allEntries = true),
//            @CacheEvict(value={"app_study_exam_info","app_study_book_info","app_study_video_info"}, allEntries = true),
//            @CacheEvict(value={"app_study_exam_content", "app_study_video_content" }, allEntries = true),
//            @CacheEvict(value="collect", allEntries = true),
//            @CacheEvict(value = "search", allEntries = true)
//    })
    public void delete(@IsPartName @RequestParam("part") String part,
                       @NotNull @RequestParam("list") String listStr) {
        logger.info("删除" + part);
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<Integer> list = JSONArray.parseArray(listStr, Integer.class);
        switch (epart) {
            case EXAM: {
                for(int id : list){
                    studyService.deleteExam(id);
                }
                break;
            }
            case BOOK: {
                for(int id : list){
                    studyService.deleteBook(id);
                }
                break;
            }
            default: {
                for(int id : list){
                    studyService.deleteVideo(part, id);
                }
                break;
            }
        }
    }

    /**
     * manage获取数据
     */
    @RequestMapping(value = "/manage/study")
    @ResponseBody
//    @Cacheable(value="manage_study", key="#part + '_page_'+ #page + '_limit_' + #limit + '_key_' + #key")
    public String manageGetParts(int userId, String part, int page, int limit, String key) {
        logger.info("控制端获取" + part + "数据" + page + "**" + limit + " key = " + key);
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        int num = (page - 1) * limit;
        List list;
        switch (epart){
            case EXAM: {
                if (key == null || key.trim().isEmpty()) list = studyService.getExamById(userId, num, limit);
                else list = studyService.getExamByKey(userId, key, num,limit);
                break;
            }
            case BOOK: {
                if (key == null || key.trim().isEmpty()) list = studyService.getBookById(userId, num, limit);
                else list = studyService.getBookByKey(userId, key, num,limit);
                break;
            }
            default: {
                if (key == null || key.trim().isEmpty()) list = studyService.getVideoById(userId, epart, num, limit);
                else list = studyService.getVideoByKey(userId, epart, key, num, limit);
                break;
            }
        }

        JSONObject obj = new JSONObject();
        //前台通过key值获得对应的value值
        obj.put("code", 0);
        obj.put("msg", "");
        obj.put("count", studyService.getPartsCount(epart));
        obj.put("data", list);
        return obj.toJSONString();
    }

// <----------------------------------------控制端 上传资源------------------------------------------------>

    @RequestMapping("/upload/study/exam")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(value={"app_study_exam", "manage_study"}, allEntries = true),
            @CacheEvict(value = "search", allEntries = true)
    })
    public void uploadExam(@RequestParam("title") String title,
                           @RequestParam("label") int label,
                           @RequestParam("time") int time,
                           @RequestParam("question") String question) {
        logger.info("上传试卷" + title);
        List<QuestionModel> list = JSONArray.parseArray(question, QuestionModel.class);
        ExamModel model = new ExamModel(title, new ClassifyModel(label), time);
        studyService.insertExam(model);
        studyService.insertExamContent(model.getId(), list);
    }

    @RequestMapping("/upload/study/book")
    @ResponseBody
//    @Caching(evict = {
//            @CacheEvict(value={"app_study_book", "manage_study"}, allEntries = true),
//            @CacheEvict(value = "search", allEntries = true)
//    })
    public void uploadBook(@NotBlank(message = "title不能为空")
                           @RequestParam("title") String title,
                           int label,
                           @NotBlank(message = "author不能为空")
                           @RequestParam("author") String author,
                           @NotBlank(message = "synopsis不能为空")
                           @RequestParam("synopsis") String synopsis,
                           @RequestParam("pic") String pic,
                           @RequestParam("file") String file){
        logger.info("处理书籍存储:" + title + "**" + author + "**" + synopsis);
        BookModel model = new BookModel(title, new ClassifyModel(label), author, synopsis, pic, file);
        studyService.insertBook(model);
    }

    @RequestMapping("/upload/study/video")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(value={"app_study_video", "manage_study"}, allEntries = true),
            @CacheEvict(value = "search", allEntries = true)
    })
    public void uploadVideo(String part,
                            @NotBlank(message = "title不能为空")
                            @RequestParam("title") String title,
                            int label,
                            @NotBlank(message = "actor不能为空")
                            @RequestParam("actor") String actor,
                            int num,
                            @NotBlank(message = "synopsis不能为空")
                            @RequestParam("synopsis") String synopsis,
                            @RequestParam("pic") String pic,
                            @RequestParam("video") String video){
        logger.info("处理视频存储:" + part + "**" + label + "**" + title);
        List<VideoContentModel> videoList = JSONArray.parseArray(video, VideoContentModel.class);
        VideoModel model = new VideoModel(title, new ClassifyModel(label), actor, num, synopsis, pic);
        studyService.insertVideo(part, model);
        studyService.insertVideoContent(part, model.getId(), videoList);
    }

// <--------------------------------------获取试卷------------------------------------------>

    @RequestMapping(value = "/study/exam/graduate")
    @ResponseBody
//    @Cacheable(value="app_study_exam", key="'special_label_1_num_'+ #num + '_limit_' + #limit")
    public Result<List<ExamModel>> getGraduateExam(int userId, int num, int limit) {
        logger.info("app获取考研试卷 num：" + num + "**" + limit);
        List<ExamModel> list = studyService.getExamByLabel(userId, num, limit, 0);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/exam/recent")
    @ResponseBody
//    @Cacheable(value="app_study_exam", key="'recent_limit_' + #limit")
    public Result<List<ExamModel>> getRecentExam(int userId, int limit) {
        logger.info("app获取最新试卷 limit：" + limit);
        List<ExamModel> list = studyService.getExamByTime(userId, limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/exam/special")
    @ResponseBody
//    @Cacheable(value="app_study_exam", key="'special_label_' + #label + '_num_'+ #num + '_limit_' + #limit")
    public Result<List<ExamModel>> getSpecialExam(int userId, int num, int limit, int label) {
        logger.info("app获取分类试卷 label: " + label + " num：" + num + "**" + limit);
        List<ExamModel> list = studyService.getExamByLabel(userId, num, limit, label);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    /**
     * 获取一个exam的详细信息
     * @param id exam编号
     * */
    @RequestMapping(value = "/study/exam")
    @ResponseBody
//    @Cacheable(value="app_study_exam_info", key="'id_' + #id")
    public Result<ExamModel> getExam(int userId, int id) {
        logger.info("app获取试卷 id: " + id + " 的详细信息");
        ExamModel model = studyService.getExam(userId, id);
        if(model == null) return Result.error();
        historyService.insertHistory(userId, EModel.STUDY.getType(), EStudyPart.EXAM.getType(), id);
        return Result.success(model);
    }

    /**
     * 获取一个exam的全部内容 ：试题
     * @param id exam编号
     * */
    @RequestMapping(value = "/study/exam/content")
    @ResponseBody
//    @Cacheable(value="app_study_exam_content", key="'id_' + #id")
    public Result<List<QuestionModel>> getExamContent(int userId, int id) {
        logger.info("app获取试卷 id: " + id + " 的全部试卷");
        List<QuestionModel> list = studyService.getExamContent(userId, id);
        if(list == null) return Result.error();
        return Result.success(list);
    }
// <--------------------------------------获取书籍------------------------------------------>

    @RequestMapping(value = "/study/book/hot")
    @ResponseBody
//    @Cacheable(value="app_study_book", key="'hot_num_'+ #num + '_limit_' + #limit")
    public Result<List<BookModel>> getHotBook(int userId, int num, int limit) {
        logger.info("app获取最新热书籍 num：" + num + "**" + limit);
        List<BookModel> list = studyService.getBookByCount(userId, limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/book/recent")
    @ResponseBody
//    @Cacheable(value="app_study_book", key="'recent_num_'+ #num + '_limit_' + #limit")
    public Result<List<BookModel>> getRecentBook(int userId, int num, int limit) {
        logger.info("app获取最新书籍 num：" + num + "**" + limit);
        List<BookModel> list = studyService.getBookByTime(userId, limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/book/special")
    @ResponseBody
//    @Cacheable(value="app_study_book", key="'special_label_' + #label + '_num_'+ #num + '_limit_' + #limit")
    public Result<List<BookModel>> getSpecialBook(int userId, int num, int limit, int label) {
        logger.info("app获取分类书籍 label: " + label + " num：" + num + "**" + limit);
        List<BookModel> list = studyService.getBookByLabel(userId, num, limit, label);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/book")
    @ResponseBody
//    @Cacheable(value="app_study_book_info", key="'id_' + #id")
    public Result<BookModel> getBook(int userId, int id) {
        logger.info("app获取书籍 id: " + id + "的信息");
        BookModel model = studyService.getBook(userId, id);
        historyService.insertHistory(userId, EModel.STUDY.getType(), EStudyPart.BOOK.getType(), id);
        if(model == null) return Result.error();
        return Result.success(model);
    }

// <--------------------------------------获取电视------------------------------------------>

    //获取热门视频
    @RequestMapping(value = "/study/video/hot")
    @ResponseBody
//    @Cacheable(value="'app_study_video", key="'hot_' + #part + '_num_'+ #num + '_limit_' + #limit")
    public Result<List<VideoModel>> getHotVideo(int userId, String part, int num, int limit) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        logger.info("app获取" + part + "的最热视频 num：" + num + "**" + limit);
        List<VideoModel> list = studyService.getVideoByCount(userId, epart, limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    //获取最新视频
    @RequestMapping(value = "/study/video/recent")
    @ResponseBody
//    @Cacheable(value="'app_study_video", key="'recent_' + #part + '_num_'+ #num + '_limit_' + #limit")
    public Result<List<VideoModel>> getRecentVideo(int userId, String part, int num, int limit) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        logger.info("app获取" + part + "的最新视频 num：" + num + "**" + limit);
        List<VideoModel> list = studyService.getVideoByTime(userId, epart, limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    //获取分类视频
    @RequestMapping(value = "/study/video/special")
    @ResponseBody
//    @Cacheable(value="'app_study_video_special", key="'special_' + #part + '_label_' + #label + '_num_'+ #num + '_limit_' + #limit")
    public Result<List<VideoModel>> getSpecialVideo(int userId, String part, int num, int limit, int label) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        logger.info("app获取" + part + "的分类[" + label + "]视频 num：" + num + "**" + limit);
        List<VideoModel> list = studyService.getVideoByLabel(userId, epart, num, limit, label);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    //获取视频信息
    @RequestMapping(value = "/study/video")
    @ResponseBody
//    @Cacheable(value="app_study_video_info", key="#part + '_id_' + #id")
    public Result<VideoModel> getVideo(int userId, String part, int id) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        logger.info("app获取" + part + "中 id：" + id + "的信息");
        VideoModel model = studyService.getVideo(userId, epart, id);
        historyService.insertHistory(userId, EModel.STUDY.getType(), epart.getType(), id);
        if(model == null) return Result.error();
        return Result.success(model);
    }

    //获取视频全部外链
    @RequestMapping(value = "/study/video/content")
    @ResponseBody
//    @Cacheable(value="app_study_video_content", key="#part + '_id_' + #id")
    public Result<List<VideoContentModel>> getVideoContent(int userId, String part, int id) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        logger.info("app获取" + part + "中 id：" + id + "的全部外链");
        List<VideoContentModel> list = studyService.getVideoContent(epart, id);
        if(list == null) return Result.error();
        return Result.success(list);
    }
}
