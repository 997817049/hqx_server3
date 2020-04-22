package com.zty.hqx.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zty.hqx.annotation.IsStudyPart;
import com.zty.hqx.annotation.IsVideoPart;
import com.zty.hqx.classify.*;
import com.zty.hqx.model.*;
import com.zty.hqx.service.*;
import com.zty.hqx.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Controller
@Validated
@CacheConfig(cacheNames = "hqx")
public class StudyController {
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
    public Result<String> isVideoExist(@NotBlank(message = "md5不能为空")String md5){
        String path = resourceService.isExistVideo(md5);
        if(path != null) {
            return Result.success(path);
        }
        return Result.error();
    }

    //判断名字是否能用
    @RequestMapping("/test/study")
    @ResponseBody
    public boolean isTitleAvailable(@IsStudyPart String part,
                                    @NotBlank(message = "title不能为空")String title){
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        boolean rs = studyService.isTitleAvailable(epart, title);
        return rs;
    }

    //获取分类
    @RequestMapping(value = "/classify/study")
    @ResponseBody
    @Cacheable(key="'classify:study:' + #part")
    public Result<List<ClassifyModel>> getClassify(@IsStudyPart String part){
        List<ClassifyModel> list = classifyService.getClassify(part);
        return Result.success(list);
    }

    @RequestMapping(value = "/add/study/label")
    @ResponseBody
    @CacheEvict(key="'classify:study:' + #part")
    public Result<ClassifyModel> addLabel(@IsStudyPart String part, String msg, String english){
        ClassifyModel model = new ClassifyModel(msg, english);
        classifyService.insertClassify(part, model);
        return Result.success(model);
    }

    @RequestMapping(value = "/delete/study/label")
    @ResponseBody
    @CacheEvict(key="'classify:study:' + #part")
    public Result<Boolean> deleteLabel(@IsStudyPart String part, int num){
        boolean rs = classifyService.deleteClassify(part, num);
        return Result.success(rs);
    }

// <----------------------------------控制端修改资源----------------------------------------->

    @RequestMapping("/update/study/exam")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key = "'manage:study:exam'"),
            @CacheEvict(key = "'app:study:exam:recent'"),
            @CacheEvict(key = "'app:study:exam:special'"),
            @CacheEvict(key = "'app:study:exam:info:id_' + #id"),
            @CacheEvict(key = "'collect:study:exam'"),
            @CacheEvict(key = "'search:study:exam'"),
    })
    public void updateExam(int id, String title, int label, int time) {
        ExamModel model = new ExamModel(id, title, new ClassifyModel(label), time);
        studyService.updateExam(model);
    }

    @RequestMapping("/update/study/book")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key = "'manage:study:book'"),
            @CacheEvict(key = "'app:study:book:hot'"),
            @CacheEvict(key = "'app:study:book:recent'"),
            @CacheEvict(key = "'app:study:book:special'"),
            @CacheEvict(key = "'app:study:book:info:id_' + #id"),
            @CacheEvict(key = "'collect:study:book'"),
            @CacheEvict(key = "'search:study:book'"),
            @CacheEvict(key = "'history:book'")
    })
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
        BookModel model = new BookModel(id, title, new ClassifyModel(label), author, synopsis, pic, file);
        studyService.updateBook(model);
    }

    @RequestMapping("/update/study/video")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key = "'manage:study:' + #part"),
            @CacheEvict(key = "'app:study:' + #part + ':hot'"),
            @CacheEvict(key = "'app:study:' + #part + ':recent'"),
            @CacheEvict(key = "'app:study:' + #part + ':special'"),
            @CacheEvict(key = "'app:study:' + #part + ':info:id_' + #id"),
            @CacheEvict(key = "'collect:study:' + #part"),
            @CacheEvict(key = "'search:study:' + #part"),
            @CacheEvict(key = "'history:' + #part")
    })
    public void updateVideo(@IsVideoPart String part, int id,
                            @NotBlank(message = "title不能为空")
                            @RequestParam("title") String title,
                            int label,
                            @NotBlank(message = "actor不能为空")
                            @RequestParam("actor") String actor,
                            int num,
                            @NotBlank(message = "synopsis不能为空")
                            @RequestParam("synopsis") String synopsis,
                            @RequestParam("pic") String pic){
        VideoModel model = new VideoModel(id, title, new ClassifyModel(label), actor, num, synopsis, pic);
        studyService.updateVideo(part, model);
    }

    @RequestMapping("/update/study/exam/content")
    @ResponseBody
    @CacheEvict(key="'app:study:exam:content:id_' + #id")
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
        QuestionModel model = new QuestionModel(num, type, question, answer, analysis, optionA, optionB, optionC, optionD);
        studyService.updateExamContent(id, model);
    }

    @RequestMapping("/update/study/video/content")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key="'app:study:' + #part + ':content:id_' + #id"),
    })
    public void updateTvVideo(@IsVideoPart String part, int id, int oldNum, int newNum, @NotBlank String videoUrl) {
        studyService.updateVideoContentNum(part, id, oldNum, newNum, videoUrl);
    }

// <-------------------------------------控制端删除资源----------------------------------------->

    @RequestMapping("/delete/study/exam")
    @ResponseBody
    @CacheEvict(key="'app:study:exam:content:id_' + #id")
    public void deleteExamContent(int id,
                                  @NotNull @RequestParam("list") String list) {
        List<Integer> list1 = JSONArray.parseArray(list, Integer.class);
        studyService.deleteExamContent(id, list1);
    }

    @RequestMapping("/delete/study/video")
    @ResponseBody
    @CacheEvict(key="'app:study:' + #part + ':content:id_' + #id")
    public void deleteVideoContent(@IsVideoPart String part, int id,
                                   @NotNull @RequestParam("list") String listStr) {
        List<Integer> list = JSONArray.parseArray(listStr, Integer.class);
        for (int num : list){
            studyService.deleteVideoContent(part, id, num);
        }
    }

    @RequestMapping("/delete/study")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key = "'manage:study:' + #part"),
            @CacheEvict(key = "'app:study:' + #part"),
            @CacheEvict(key = "'collect:study:' + #part"),
            @CacheEvict(key = "'search:study:' + #part"),
            @CacheEvict(key = "'history'", condition = "!{#part eq 'exam'}")
    })
    public void delete(@IsStudyPart @RequestParam("part") String part,
                       @NotNull @RequestParam("list") String listStr) {
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
    @Cacheable(key="'manage:study:' + #part + ':page_'+ #page + '_limit_' + #limit + '_key_' + #key")
    public String manageGetParts(int userId, @IsStudyPart String part, int page, int limit, String key) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        int num = (page - 1) * limit;
        List list;
        switch (epart){
            case EXAM: {
                if (key == null || key.trim().isEmpty()) list = studyService.getExamByNum(num, limit);
                else list = studyService.getExamByKey(userId, key, num,limit);
                break;
            }
            case BOOK: {
                if (key == null || key.trim().isEmpty()) list = studyService.getBookByNum(num, limit);
                else list = studyService.getBookByKey(key, num,limit);
                break;
            }
            default: {
                if (key == null || key.trim().isEmpty()) list = studyService.getVideoByNum(epart, num, limit);
                else list = studyService.getVideoByKey(epart, key, num, limit);
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
            @CacheEvict(key = "'manage:study:exam'"),
            @CacheEvict(key = "'app:study:exam:recent'"),
            @CacheEvict(key = "'app:study:exam:special:' + #label"),
            @CacheEvict(key = "'search:study:exam'")
    })
    public void uploadExam(@RequestParam("title") String title,
                           @RequestParam("label") int label,
                           @RequestParam("time") int time,
                           @RequestParam("question") String question) {
        List<QuestionModel> list = JSONArray.parseArray(question, QuestionModel.class);
        ExamModel model = new ExamModel(title, new ClassifyModel(label), time);
        studyService.insertExam(model);
        studyService.insertExamContent(model.getId(), list);
    }

    @RequestMapping("/upload/study/book")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key = "'manage:study:book'"),
            @CacheEvict(key = "'app:study:book:hot'"),
            @CacheEvict(key = "'app:study:book:recent'"),
            @CacheEvict(key = "'app:study:book:special:' + #label"),
            @CacheEvict(key = "'search:study:book'")
    })
    public void uploadBook(@NotBlank(message = "title不能为空")
                           @RequestParam("title") String title,
                           int label,
                           @NotBlank(message = "author不能为空")
                           @RequestParam("author") String author,
                           @NotBlank(message = "synopsis不能为空")
                           @RequestParam("synopsis") String synopsis,
                           @RequestParam("pic") String pic,
                           @RequestParam("file") String file){
        BookModel model = new BookModel(title, new ClassifyModel(label), author, synopsis, pic, file);
        studyService.insertBook(model);
    }

    @RequestMapping("/upload/study/video")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key = "'manage:study:' + #part"),
            @CacheEvict(key = "'app:study:' + #part + ':hot'"),
            @CacheEvict(key = "'app:study:' + #part + ':special:' + #label"),
            @CacheEvict(key = "'app:study:' + #part + ':recent'"),
            @CacheEvict(key = "'search:study:' + #part")
    })
    public void uploadVideo(@IsVideoPart String part,
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
        List<VideoContentModel> videoList = JSONArray.parseArray(video, VideoContentModel.class);
        VideoModel model = new VideoModel(title, new ClassifyModel(label), actor, num, synopsis, pic);
        studyService.insertVideo(part, model);
        studyService.insertVideoContent(part, model.getId(), videoList);
    }

// <--------------------------------------获取试卷------------------------------------------>

    @RequestMapping(value = "/study/exam/graduate")
    @ResponseBody
    @Cacheable(key="'app:study:exam:special:1:num_'+ #num + '_limit_' + #limit")
    public Result<List<ExamModel>> getGraduateExam(int userId, int num, int limit) {
        List<ExamModel> list = studyService.getExamByLabel(userId, num, limit, 0);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/exam/recent")
    @ResponseBody
    @Cacheable(key="'app:study:exam:recent:limit_' + #limit")
    public Result<List<ExamModel>> getRecentExam(int userId, int limit) {
        List<ExamModel> list = studyService.getExamByTime(userId, limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/exam/special")
    @ResponseBody
    @Cacheable(key="'app:study:exam:special:' + #label + ':num_'+ #num + '_limit_' + #limit")
    public Result<List<ExamModel>> getSpecialExam(int userId, int num, int limit, int label) {
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
    @Cacheable(key="'app:study:exam:info:id_' + #id")
    public Result<ExamModel> getExam(int userId, int id) {
        ExamModel model = studyService.getExamById(userId, id);
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
    @Cacheable(key="'app:study:exam:content:id_' + #id")
    public Result<List<QuestionModel>> getExamContent(int userId, int id) {
        List<QuestionModel> list = studyService.getExamContent(id);
        if(list == null) return Result.error();
        return Result.success(list);
    }
// <--------------------------------------获取书籍------------------------------------------>

    @RequestMapping(value = "/study/book/hot")
    @ResponseBody
    @Cacheable(key="'app:study:book:hot:limit_' + #limit")
    public Result<List<BookModel>> getHotBook(int userId, int limit) {
        List<BookModel> list = studyService.getBookByCount(limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/book/recent")
    @ResponseBody
    @Cacheable(key="'app:study:book:recent:limit_' + #limit")
    public Result<List<BookModel>> getRecentBook(int userId, int limit) {
        List<BookModel> list = studyService.getBookByTime(limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/book/special")
    @ResponseBody
    @Cacheable(key="'app:study:book:special:' + #label + ':num_'+ #num + '_limit_' + #limit")
    public Result<List<BookModel>> getSpecialBook(int userId, int num, int limit, int label) {
        List<BookModel> list = studyService.getBookByLabel(num, limit, label);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    @RequestMapping(value = "/study/book")
    @ResponseBody
    @Caching(evict = {
            @CacheEvict(key="'history:book:userId_'+#userId")
    })
    @Cacheable( key="'app:study:book:info:id_' + #id")
    public Result<BookModel> getBook(int userId, int id) {
        BookModel model = studyService.getBook(userId, id);
        historyService.insertHistory(userId, EModel.STUDY.getType(), EStudyPart.BOOK.getType(), id);
        if(model == null) return Result.error();
        return Result.success(model);
    }

// <--------------------------------------获取电视------------------------------------------>

    /**
     * 个性化推荐
     * */
    @RequestMapping(value = "/study/video/personal")
    @ResponseBody
    @Cacheable(key="'app:study:' + #part +':recommend:userId_' + #userId")
    public Result<List<VideoModel>> getPersonalizedRecommendation(int userId, @IsVideoPart String part){
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoModel> list = studyService.getVideoByCount(epart, 6);
        return Result.success(list);
    }

    //获取热门视频
    @RequestMapping(value = "/study/video/hot")
    @ResponseBody
    @Cacheable(key="'app:study:' + #part +':hot:limit_' + #limit")
    public Result<List<VideoModel>> getHotVideo(int userId, @IsVideoPart String part, int limit) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoModel> list = studyService.getVideoByCount(epart, limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    //获取最新视频
    @RequestMapping(value = "/study/video/recent")
    @ResponseBody
    @Cacheable(key="'app:study:' + #part + ':recent:limit_' + #limit")
    public Result<List<VideoModel>> getRecentVideo(int userId, @IsVideoPart String part, int limit) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoModel> list = studyService.getVideoByTime(epart, limit);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    //获取分类视频
    @RequestMapping(value = "/study/video/special")
    @ResponseBody
    @Cacheable(key="'app:study:' + #part + ':special:' + #label + ':num_'+ #num + '_limit_' + #limit")
    public Result<List<VideoModel>> getSpecialVideo(int userId, @IsVideoPart String part, int num, int limit, int label) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoModel> list = studyService.getVideoByLabel(epart, num, limit, label);
        if(list == null) return Result.error();
        return Result.success(list);
    }

    //获取视频信息
    @RequestMapping(value = "/study/video")
    @ResponseBody
    @Cacheable(key="'app:study:' + #part + ':info:id_' + #id")
    public Result<VideoModel> getVideo(int userId, @IsVideoPart String part, int id) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        VideoModel model = studyService.getVideo(userId, epart, id);
        historyService.insertHistory(userId, EModel.STUDY.getType(), epart.getType(), id);
        if(model == null) return Result.error();
        return Result.success(model);
    }

    //获取视频全部外链
    @RequestMapping(value = "/study/video/content")
    @ResponseBody
    @Cacheable(key="'app:study:' + #part + ':content:id_' + #id")
    public Result<List<VideoContentModel>> getVideoContent(int userId, @IsVideoPart String part, int id) {
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoContentModel> list = studyService.getVideoContent(epart, id);
        if(list == null) return Result.error();
        return Result.success(list);
    }
}
