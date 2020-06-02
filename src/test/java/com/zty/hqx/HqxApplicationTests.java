package com.zty.hqx;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.controller.CountController;
import com.zty.hqx.dao.*;
import com.zty.hqx.model.*;
import com.zty.hqx.service.BaseService;
import com.zty.hqx.service.HistoryService;
import com.zty.hqx.service.StudyService;
import com.zty.hqx.util.*;
import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.beans.Expression;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class HqxApplicationTests {
    @Autowired
    HistoryService historyService;
    @Autowired
    SearchDao searchDao;
    @Autowired
    CollectDao collectDao;
    @Autowired
    UserDao userDao;
    @Autowired
    StudyVideoDao videoDao;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    BaseDao baseDao;
    @Autowired
    StudyExamDao examDao;
    @Autowired
    StudyBookDao bookDao;
    @Autowired
    CountDao countDao;
    @Autowired
    StudyService studyService;
    @Autowired
    BaseService baseService;

    @Autowired
    CountController countController;
    @Test
    void calFavoriteWord() {
        List<Integer> userIdList = userDao.getAllUserId();
        System.out.println("用户列表：" + userIdList);
        //获取用户昨天的喜好词表<user, map<词汇，指数>>
        HashMap<Integer, HashMap<String, Double>> redisUserWordMap = null;
        try {
            redisUserWordMap = (HashMap<Integer, HashMap<String, Double>>) redisUtil.get("hqx:userWordMap");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("redis中昨天的用户喜欢表:" + redisUserWordMap);
        //今天生成的用户 - 喜好词列表 cf-idf
        HashMap<Integer, List<FavoriteWordModel>> CFIDFUserWordMap = new HashMap<>();
        //存储全部用户的喜好词表
        for(int userId : userIdList) {
            List<FavoriteWordModel> CFIDFList = getOneVideoFavoriteList(userId);
            System.out.println("用户的keyWordList " + userId + "：\n"  + CFIDFList);
            if(CFIDFList != null){
                CFIDFUserWordMap.put(userId, CFIDFList);
            }
        }

        //生成全部用户的协同过滤
        UserCFUtil userCFUtil = new UserCFUtil(CFIDFUserWordMap);
        HashMap<Integer, List<FavoriteWordModel>> similarUserWordMap = new HashMap<>();
        for(int userId : userIdList) {
            if(CFIDFUserWordMap.get(userId) == null) {
                System.out.println(userId + "用户无CFIDFUserWord -》 无recommendLevelList");
                continue;
            }
            //每一个关键词的推荐度
            List<FavoriteWordModel> recommendLevelList = userCFUtil.getRecommendLevel(userId);
            if(recommendLevelList == null || recommendLevelList.isEmpty()) {
                System.out.println(userId + "用户无recommendLevelList");
                continue;
            }
            //排序
            recommendLevelList.sort((o1, o2) -> {
                return (int) (o2.getIndex() - o1.getIndex());
            });
            System.out.println(userId + "协同推荐：" + recommendLevelList);
            similarUserWordMap.put(userId, recommendLevelList);
        }

        //时间衰减
        if (redisUserWordMap != null) {
            for (Map.Entry<Integer, HashMap<String, Double>> entry : redisUserWordMap.entrySet()){
                HashMap<String, Double> redisList  = entry.getValue();
                for (Map.Entry<String, Double> entry2 : redisList.entrySet()){
                    entry2.setValue(entry2.getValue() * 0.82);
                }
            }
        }
        System.out.println("redis*0.9:\n" + redisUserWordMap);

        HashMap<Integer, HashMap<String, Double>> newRedisUserWordMap = new HashMap<>();//今天的
        //加在一起
        for(int userId : userIdList) {
            HashMap<String, Double> redisMap = null;
            if(redisUserWordMap != null){
                redisMap = redisUserWordMap.get(userId);
            }
            List<FavoriteWordModel> favoriteList = CFIDFUserWordMap.get(userId);
            List<FavoriteWordModel> similarList = similarUserWordMap.get(userId);
            if(similarList != null){
                favoriteList.addAll(similarList);
            }
            if(redisMap == null && (favoriteList == null || favoriteList.isEmpty())){
                continue;
            } else if(favoriteList != null && !favoriteList.isEmpty()) {
                if(redisMap == null){
                    redisMap = new HashMap<>();
                }
                for(FavoriteWordModel model : favoriteList){
                    String word = model.getWord();
                    Double oldIndex = redisMap.get(word);
                    double newIndex = model.getIndex();
                    if(oldIndex != null){
                        newIndex += oldIndex;
                    }
                    redisMap.put(word, newIndex);
                }
            }
//            todo 排序redisMap

            newRedisUserWordMap.put(userId, redisMap);
            System.out.println(userId + "加在一起的:" + redisMap);
        }

        //去掉小于0.1的
        for (Iterator<Map.Entry<Integer, HashMap<String, Double>>> iterator1 = newRedisUserWordMap.entrySet().iterator(); iterator1.hasNext();){
            Map.Entry<Integer, HashMap<String, Double>> entry = iterator1.next();
            HashMap<String, Double> map = entry.getValue();
            for (Iterator<Map.Entry<String, Double>> iterator2 = map.entrySet().iterator(); iterator2.hasNext();){
                Map.Entry<String, Double> item = iterator2.next();
                Double index = item.getValue();
                if(index < 0.1){
                    iterator2.remove();
                }
            }
            if(map.isEmpty()) iterator1.remove();
        }

        // 写入redis 关键词 覆盖原来用户喜好词
        redisUtil.set("hqx:userWordMap", newRedisUserWordMap);
        System.out.println("newRedisUserWordMap:");
        System.out.println(newRedisUserWordMap);

        //删除原来的
        redisUtil.removePattern("hqx:app:study:*");
        //存入用户喜好视频列表
        for(int userId : userIdList){
            HashMap<String, Double> map = newRedisUserWordMap.get(userId);
            if(map == null || map.isEmpty()) continue;
            for (int i = 2; i < 7; i++){
                String part = EStudyPart.values()[i].getEnglish();
                List<VideoModel> videoList = getVideoList(part, userId, new ArrayList<>(map.keySet()));
                //写入新的
                redisUtil.set("hqx:app:study:" + part + ":recommend:userId_" + userId, videoList);
            }
        }
    }

    //获取用户视频列表
    private List<VideoModel> getVideoList(String part, int userId, List<String> wordList){
        List<VideoModel> list = new ArrayList<>();
        HashSet<Integer> set = new HashSet<>();
        if(wordList == null || wordList.isEmpty()){
            //存入六个hot
            list = videoDao.getVideoByCount(part, 6);
        } else {
            for(String word : wordList){
                List<VideoModel> subList = videoDao.getVideoByKey(part, word);
                for (VideoModel videoModel : subList){
                    int id = videoModel.getId();
                    if(!set.contains(id)){
                        list.add(videoModel);
                        set.add(id);
                    }
                    if(list.size() >= 6) break;
                }
                if(list.size() >= 6) break;
            }
            System.out.println("查完wordList时" + set);
        }
        if (list.size() < 6){
            List<VideoModel> subList = videoDao.getVideoByCount(part, 6);
            for (VideoModel videoModel : subList){
                int id = videoModel.getId();
                if(!set.contains(id)){
                    list.add(videoModel);
                    set.add(id);
                }
                if(list.size() >= 6) break;
            }
            System.out.println("加入hotvideo时" + set);
        }
        System.out.println(userId + "个性化推荐：\n" + list);
        return list;
    }

    //获取用户视频的关键词
    private List<FavoriteWordModel> getOneVideoFavoriteList(int userId){
        List<VideoModel> videoList = new ArrayList<>();
        String time = ZtyUtil.getYesterday();
//        String time = "2020-04-13";
        //历史记录
        for(int i = 2; i < 7; i++) {
            String part = EStudyPart.values()[i].getEnglish();
            List<VideoModel> historyList = historyService.getVideoHistoryByTime(userId, part, time);
            if(historyList != null && !historyList.isEmpty()){
                videoList.addAll(historyList);
            }

            if(userId != 1) continue;
            //收藏
            List<VideoModel> collectList = collectDao.getAllVideoCollect(part, i, userId);
            if(collectList != null && !collectList.isEmpty()){
                videoList.addAll(collectList);
            }
        }

        //查找记录
        List<String> searchList = new ArrayList<>();
        while (true) {
            List<String> list = searchDao.getUserHistoryWordByTime(userId, time);
            searchList.addAll(list);
            if(list.size() < 100) break;
        }

        //查找到的全部信息 videoList中的全部title， 以及searchList
        //一个标题认为是一个文档
        //存入docList 存储全部文档
        ArrayList<String> docList = new ArrayList<>();
        for(VideoModel model : videoList){
            docList.add(model.getTitle());
        }
        docList.addAll(searchList);
        if(docList.isEmpty()) return null;
        return TF_IDFUtil.getKeyWord(docList);
    }

    private static final Random random = new Random();
    private static final int min = 0;
    private static final int max = 1000;

    private static final Calendar cal = Calendar.getInstance();
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    //添加数据库count
    @Test
    void count(){
        System.out.println(df.format(new Date()));
        cal.setTime(new Date());
        for (int i = 0; i < 32; i++){
            cal.add(Calendar.DATE, + 1);
            Date d = cal.getTime();
            System.out.println(df.format(d));
        }
    }
    @Test
    void oneDayCount(){
        List<BaseModel> baseList = baseDao.getAllBaseCount();
        List<ExamModel> examList = examDao.getAllExamCount();
        List<BookModel> bookList = bookDao.getAllBookCount();
        List<VideoModel> tvList = videoDao.getAllVideoCount(EStudyPart.TELEPLAY.getEnglish());
        List<VideoModel> filmList = videoDao.getAllVideoCount(EStudyPart.FILM.getEnglish());
        List<VideoModel> varietyList = videoDao.getAllVideoCount(EStudyPart.VARIETY.getEnglish());
        List<VideoModel> documentaryList = videoDao.getAllVideoCount(EStudyPart.DOCUMENTARY.getEnglish());
        List<VideoModel> dramaList = videoDao.getAllVideoCount(EStudyPart.DRAMA.getEnglish());
        cal.setTime(new Date());
        for (int i = 0; i < 32; i++){
            cal.add(Calendar.DATE, + 1);
            Date d = cal.getTime();
            String time = df.format(d);
            System.out.println("插入：" + time);
            for(BaseModel model : baseList) {
                countDao.insertCount(EModel.BASE.getType(), 0, 0, model.getId(), time, getRandom());
            }
            for(ExamModel model : examList) {
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.EXAM.getType(), model.getLabel().getNum() , model.getId(), time,  getRandom());
            }
            for(BookModel model : bookList) {
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.BOOK.getType(), model.getLabel().getNum() , model.getId(), time,  getRandom());
            }
            for(VideoModel model : tvList) {
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.TELEPLAY.getType(), model.getLabel().getNum() , model.getId(), time,  getRandom());
            }
            for(VideoModel model : filmList) {
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.FILM.getType(), model.getLabel().getNum() , model.getId(), time,  getRandom());
            }
            for(VideoModel model : varietyList) {
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.VARIETY.getType(), model.getLabel().getNum() , model.getId(), time,  getRandom());
            }
            for(VideoModel model : documentaryList) {
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.DOCUMENTARY.getType(), model.getLabel().getNum() , model.getId(), time,  getRandom());
            }
            for(VideoModel model : dramaList) {
                countDao.insertCount(EModel.STUDY.getType(), EStudyPart.DRAMA.getType(), model.getLabel().getNum() , model.getId(), time,  getRandom());
            }
        }
    }

    private int getRandom(){
        return random.nextInt(max) % (max - min + 1) + min;
    }
}

