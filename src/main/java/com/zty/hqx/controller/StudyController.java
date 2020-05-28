package com.zty.hqx.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zty.hqx.validator.IsStudyPart;
import com.zty.hqx.validator.IsVideoPart;
import com.zty.hqx.classify.*;
import com.zty.hqx.model.*;
import com.zty.hqx.service.*;
import com.zty.hqx.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@Validated
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

//=======================================页面切换===================================================

    @RequestMapping("/study/add")
    public String addExam(String part){
        if(part.equals("exam")) return "study/add_exam";
        if(part.equals("book")) return "study/add_book";
        return "study/add_video";
    }

    @RequestMapping("/study/label")
    public String addLabel(){
        return "study/add_label";
    }

    @RequestMapping("/study/exam_content/adm")
    public String admExamContent(int id, Model model){
        model.addAttribute("id", id);
        return "study/adm_exam_content.html";
    }

    @RequestMapping("/study/adm")
    public String admStudy(String part){
        return "study/adm_" + part + ".html";
    }

    @RequestMapping("/study/video_content/adm")
    public String admVideoContent(String part, int id, Model model){
        model.addAttribute("part", part);
        VideoModel video = studyService.manageGetVideo(part, id);
        model.addAttribute("model", video);
        return "study/adm_video_content";
    }

//==================================================================================================

    @RequestMapping("/study/statistics")
    public String statisticsStudy(String part, Model model){
        model.addAttribute("part", part);
        List<ClassifyModel> classifyList = classifyService.getClassify(part);
        model.addAttribute("classify", classifyList);
        return "study/statistics_study";
    }

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
    public Result<List<ClassifyModel>> getClassify(@IsStudyPart String part){
        //redis获取值
        String redisKey = "hqx:classify:study:" + part;
        Result<List<ClassifyModel>> rs = (Result<List<ClassifyModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<ClassifyModel> list = classifyService.getClassify(part);
        rs = Result.success(list);
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    @RequestMapping(value = "/add/study/label")
    @ResponseBody
    public Result<ClassifyModel> addLabel(@IsStudyPart String part, String msg, String english){
        ClassifyModel model = new ClassifyModel(msg, english);
        classifyService.insertClassify(part, model);
        redisUtil.remove("hqx:classify:study:" + part);
        return Result.success(model);
    }

    @RequestMapping(value = "/delete/study/label")
    @ResponseBody
    public Result<Boolean> deleteLabel(@IsStudyPart String part, int num){
        boolean rs = classifyService.deleteClassify(part, num);
        redisUtil.remove("hqx:classify:study:" + part);
        return Result.success(rs);
    }

// <----------------------------------控制端修改资源----------------------------------------->

    @RequestMapping("/update/study/exam")
    @ResponseBody
    public void updateExam(int id, String title, int label, int time) {
        ExamModel model = new ExamModel(id, title, new ClassifyModel(label), time);
        studyService.updateExam(model);
        redisUtil.removePattern("hqx:manage:study:exam*",
                "hqx:app:study:exam:recent*",
                "hqx:app:study:exam:special*",
                "hqx:app:study:exam:info:id_" + id + "*",
                "hqx:collect:study:exam*",
                "hqx:search:study:exam*");
    }

    @RequestMapping("/update/study/book")
    @ResponseBody
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
        redisUtil.removePattern("hqx:manage:study:book*",
                "hqx:app:study:book:hot*",
                "hqx:app:study:book:recent*",
                "hqx:app:study:book:special*",
                "hqx:app:study:book:info:id_" + id + "*",
                "hqx:collect:study:book*",
                "hqx:search:study:book*",
                "hqx:history:book*");
    }

    @RequestMapping("/update/study/video")
    @ResponseBody
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
        redisUtil.removePattern("hqx:manage:study:" + part + "*",
                "hqx:app:study:" + part + ":hot*",
                "hqx:app:study:" + part + ":recent*",
                "hqx:app:study:" + part + ":special*",
                "hqx:app:study:" + part + ":info:id_" + id + "*",
                "hqx:collect:study:" + part + "*",
                "hqx:search:study:" + part + "*",
                "hqx:history" + part + "*");
    }

    @RequestMapping("/update/study/exam/content")
    @ResponseBody
    public void updateExamContent(@DecimalMin("0") int id,
                                  @DecimalMin("0") int num,
                                  @RequestParam("type") int type,
                                  @RequestParam("question") String question,
                                  @RequestParam("answer") String answer,
                                  @RequestParam("analysis") String analysis,
                                  @RequestParam("optionA") String optionA,
                                  @RequestParam("optionB") String optionB,
                                  @RequestParam("optionC") String optionC,
                                  @RequestParam("optionD") String optionD) {
        ExamContentModel model = new ExamContentModel(num, new ClassifyModel(type), question, answer, analysis, optionA, optionB, optionC, optionD);
        studyService.updateExamContent(id, model);
        redisUtil.remove("hqx:app:study:exam:content:id_" + id);
    }

    @RequestMapping("/update/study/video/content")
    @ResponseBody
    public void updateTvVideo(@IsVideoPart String part, int id, int oldNum, int newNum, @NotBlank String videoUrl) {
        studyService.updateVideoContent(part, id, oldNum, newNum, videoUrl);
        redisUtil.remove("hqx:app:study:" + part + ":content:id_" + id);
    }

// <-------------------------------------控制端删除资源----------------------------------------->

    @RequestMapping("/delete/study/exam")
    @ResponseBody
    public Result<Boolean> deleteExamContent(int id,
                                  @NotNull @RequestParam("list") String list) {
        List<Integer> list1 = JSONArray.parseArray(list, Integer.class);
        studyService.deleteExamContent(id, list1);
        redisUtil.removePattern("hqx:app:study:exam:content:id_" + id);
        return Result.success(true);
    }

    @RequestMapping("/delete/study/video")
    @ResponseBody
    public void deleteVideoContent(@IsVideoPart String part, int id,
                                   @NotNull @RequestParam("list") String listStr) {
        List<Integer> list = JSONArray.parseArray(listStr, Integer.class);
        for (int num : list){
            studyService.deleteVideoContent(part, id, num);
        }
        redisUtil.remove("hqx:app:study:" + part + ":content:id_" + id);
    }

    @RequestMapping("/delete/study")
    @ResponseBody
    public boolean delete(@IsStudyPart @RequestParam("part") String part,
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
        redisUtil.removePattern("hqx:manage:study:" + part + "*",
                "hqx:app:study:" + part + "*",
                "hqx:collect:study:" + part + "*",
                "hqx:search:study:" + part + "*");
        //exam没有历史记录
        if(!part.equals("exam")){
            redisUtil.removePattern("hqx:history" + part + "*");
        }
        return true;
    }

    /**
     * manage获取数据
     */
    @RequestMapping(value = "/manage/study")
    @ResponseBody
    public String manageGetParts(int userId, @IsStudyPart String part, int page, int limit, String key) {
        //redis获取值
        String redisKey = "hqx:manage:study:" + part + ":page_" + page + "_limit_" + limit + "_key_" + key;
        String rs = (String) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
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
        rs = obj.toJSONString();
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

// <----------------------------------------控制端 上传资源------------------------------------------------>

    /**
     * @param title 试卷标题
     * @param label 试卷标签
     * @param time 考试时间
     * @param question 试卷内容 list-》jsonStr
     * */
    @RequestMapping("/upload/study/exam")
    @ResponseBody
    public void uploadExam(@RequestParam("title") String title,
                           @RequestParam("label") int label,
                           @RequestParam("time") int time,
                           @RequestParam("question") String question) {
        System.out.println(question);
//        List<ExamContentModel> list = (List<ExamContentModel>) JSONObject.parse(question);
        System.out.println(JSONArray.parseArray(question, ExamContentModel.class));
        List<ExamContentModel> list = JSONArray.parseArray(question, ExamContentModel.class);
        System.out.println(list);
        ExamModel model = new ExamModel(title, new ClassifyModel(label), time);
        studyService.insertExam(model);
        studyService.insertExamContent(model.getId(), list);
        redisUtil.removePattern("hqx:manage:study:exam*",
                "hqx:app:study:exam:special:" + label + "*",
                "hqx:app:study:exam:recent*",
                "hqx:search:study:exam*");
    }

    @RequestMapping("/upload/study/book")
    @ResponseBody
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
        redisUtil.removePattern("hqx:manage:study:book*",
                "hqx:app:study:book:hot*",
                "hqx:app:study:book:special:" + label + "*",
                "hqx:app:study:book:recent*",
                "hqx:search:study:book*");
    }

    @RequestMapping("/upload/study/video")
    @ResponseBody
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
        redisUtil.removePattern("hqx:manage:study:" + part + "*",
                "hqx:app:study:" + part + ":hot*",
                "hqx:app:study:" + part + ":special:" + label + "*",
                "hqx:app:study:" + part + ":recent*",
                "hqx:search:study:" + part + "*");
    }

// <--------------------------------------获取试卷------------------------------------------>

    @RequestMapping(value = "/study/exam/graduate")
    @ResponseBody
    public Result<List<ExamModel>> getGraduateExam(int userId, int num, int limit) {
        //redis获取值
        String redisKey = "hqx:app:study:exam:special:1:num_" + num + "_limit_" + limit;
        Result<List<ExamModel>> rs = (Result<List<ExamModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<ExamModel> list = studyService.getExamByLabel(userId, num, limit, 0);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    @RequestMapping(value = "/study/exam/recent")
    @ResponseBody
    public Result<List<ExamModel>> getRecentExam(int userId, int limit) {
        //redis获取值
        String redisKey = "hqx:app:study:exam:recent:limit_" + limit;
        Result<List<ExamModel>> rs = (Result<List<ExamModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<ExamModel> list = studyService.getExamByTime(userId, limit);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    @RequestMapping(value = "/study/exam/special")
    @ResponseBody
    public Result<List<ExamModel>> getSpecialExam(int userId, int num, int limit, int label) {
        //redis获取值
        String redisKey = "hqx:app:study:exam:special:" + label + ":num_" + num + "_limit_" + limit;
        Result<List<ExamModel>> rs = (Result<List<ExamModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<ExamModel> list = studyService.getExamByLabel(userId, num, limit, label);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    /**
     * 获取一个exam的详细信息
     * @param id exam编号
     * */
    @RequestMapping(value = "/study/exam")
    @ResponseBody
    public Result<ExamModel> getExam(int userId, int id) {
        //redis获取值
        String redisKey = "hqx:app:study:exam:info:id_" + id;
        Result<ExamModel> rs = (Result<ExamModel>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        ExamModel model = studyService.getExamById(userId, id);
        if(model == null) {
            rs = Result.error();
        } else {
            historyService.insertHistory(userId, EModel.STUDY.getType(), EStudyPart.EXAM.getType(), id);
            rs = Result.success(model);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    /**
     * 获取一个exam的全部内容 ：试题
     * @param id exam编号
     * */
    @RequestMapping(value = "/study/exam/content")
    @ResponseBody
    public Result<List<ExamContentModel>> getExamContent(int userId, int id) {
        //redis获取值
        String redisKey = "hqx:app:study:exam:content:id_" + id;
        Result<List<ExamContentModel>> rs = (Result<List<ExamContentModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<ExamContentModel> list = studyService.getExamContent(id);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }
// <--------------------------------------获取书籍------------------------------------------>

    @RequestMapping(value = "/study/book/hot")
    @ResponseBody
    public Result<List<BookModel>> getHotBook(int userId, int limit) {
        //redis获取值
        String redisKey = "hqx:app:study:book:hot:limit_" + limit;
        Result<List<BookModel>> rs = (Result<List<BookModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<BookModel> list = studyService.getBookByCount(userId, limit);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    @RequestMapping(value = "/study/book/recent")
    @ResponseBody
    public Result<List<BookModel>> getRecentBook(int userId, int limit) {
        //redis获取值
        String redisKey = "hqx:app:study:book:recent:limit_" + limit;
        Result<List<BookModel>> rs = (Result<List<BookModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<BookModel> list = studyService.getBookByTime(limit);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    @RequestMapping(value = "/study/book/special")
    @ResponseBody
    public Result<List<BookModel>> getSpecialBook(int userId, int num, int limit, int label) {
        //redis获取值
        String redisKey = "hqx:app:study:book:special:" + label + ":num_" + num + "_limit_" + limit;
        Result<List<BookModel>> rs = (Result<List<BookModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        List<BookModel> list = studyService.getBookByLabel(num, limit, label);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    @RequestMapping(value = "/study/book")
    @ResponseBody
    public Result<BookModel> getBook(int userId, int id) {
        String redisKey = "hqx:app:study:book:info:id_" + id;
        Result<BookModel> rs = (Result<BookModel>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        BookModel model = studyService.getBook(userId, id);
        historyService.insertHistory(userId, EModel.STUDY.getType(), EStudyPart.BOOK.getType(), id);
        redisUtil.remove("hqx:history:book:userId_" + userId);

        if(model == null) {
            rs = Result.error();
        } else {
            rs = Result.success(model);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

// <--------------------------------------获取电视------------------------------------------>

    /**
     * 个性化推荐
     * */
    @RequestMapping(value = "/study/video/recommend")
    @ResponseBody
    public Result<List<VideoModel>> getPersonalizedRecommendation(int userId, @IsVideoPart String part, int limit){
        List<VideoModel> list = null;
        try {
            list = (List<VideoModel>) redisUtil.get("hqx:app:study:" + part + ":recommend:userId_" + userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //数据库获取值
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        if(list == null || list.isEmpty()) {
            list = studyService.getVideoByCount(epart, limit);
            return Result.success(list);
        }
        if(limit == 6) {
            return Result.success(list.subList(0, 6));
        }
        return Result.success(list);
    }

    //获取热门视频
    @RequestMapping(value = "/study/video/hot")
    @ResponseBody
    public Result<List<VideoModel>> getHotVideo(int userId, @IsVideoPart String part, int limit) {
        //redis获取值
        String redisKey = "hqx:app:study:" + part + ":hot:limit_" + limit;
        Result<List<VideoModel>> rs = (Result<List<VideoModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoModel> list = studyService.getVideoByCount(epart, limit);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    //获取最新视频
    @RequestMapping(value = "/study/video/recent")
    @ResponseBody
    public Result<List<VideoModel>> getRecentVideo(int userId, @IsVideoPart String part, int limit) {
        //redis获取值
        String redisKey = "hqx:app:study:" + part + ":recent:limit_" + limit;
        Result<List<VideoModel>> rs = (Result<List<VideoModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoModel> list = studyService.getVideoByTime(epart, limit);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    //获取分类视频
    @RequestMapping(value = "/study/video/special")
    @ResponseBody
    public Result<List<VideoModel>> getSpecialVideo(int userId, @IsVideoPart String part, int num, int limit, int label) {
        //redis获取值
        String redisKey = "hqx:app:study:" + part + ":special" + label + ":num_" + num + "_limit_" + limit;
        Result<List<VideoModel>> rs = (Result<List<VideoModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoModel> list = studyService.getVideoByLabel(epart, num, limit, label);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    //获取视频信息
    @RequestMapping(value = "/study/video")
    @ResponseBody
    public Result<VideoModel> getVideo(int userId, @IsVideoPart String part, int id) {
        //redis获取值
        String redisKey = "hqx:app:study:" + part + ":info:id_" + id;
        Result<VideoModel> rs = (Result<VideoModel>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        VideoModel model = studyService.getVideo(userId, epart, id);
        //修改历史记录
        historyService.insertHistory(userId, EModel.STUDY.getType(), epart.getType(), id);
        redisUtil.remove("hqx:history:" + part + ":" + userId + "*");
        if(model == null) {
            rs = Result.error();
        } else {
            rs = Result.success(model);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }

    //获取视频全部外链
    @RequestMapping(value = "/study/video/content")
    @ResponseBody
    public Result<List<VideoContentModel>> getVideoContent(int userId, @IsVideoPart String part, int id) {
        //redis获取值
        String redisKey = "hqx:app:study:" + part + ":content:id_" + id;
        Result<List<VideoContentModel>> rs = (Result<List<VideoContentModel>>) redisUtil.get(redisKey);
        if(rs != null){
            return rs;
        }
        //数据库获取值
        EStudyPart epart = EStudyPart.getEnumFromString(part.toUpperCase());
        List<VideoContentModel> list = studyService.getVideoContent(epart, id);
        if(list == null) {
            rs = Result.error();
        } else {
            rs = Result.success(list);
        }
        redisUtil.set(redisKey, rs, 10L, TimeUnit.MINUTES);
        return rs;
    }
}
