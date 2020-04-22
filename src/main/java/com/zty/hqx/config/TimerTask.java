package com.zty.hqx.config;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.dao.*;
import com.zty.hqx.model.*;
import com.zty.hqx.service.HistoryService;
import com.zty.hqx.util.RedisUtil;
import com.zty.hqx.util.TF_IDFUtil;
import com.zty.hqx.util.UserCFUtil;
import com.zty.hqx.util.ZtyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    UserDao userDao;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    HistoryService historyService;
    @Autowired
    SearchDao searchDao;
    @Autowired
    CollectDao collectDao;

    //昨天时间
    private String yesterdayTime = ZtyUtil.getYesterday();

    /**
     * 获取每一项当天的浏览量 存入count 表中
     * 清空每一项的浏览量
     * 每天1点执行
     * */
    @Scheduled(cron = "0 0 1 * * ? *")
    private void statisticalCount() {
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

    /**
     * 根据用户前一天的历史记录，收藏记录，查找记录计算出用户的喜好词列表
     * 并将列表存入redis
     * 取出redis中 用户前一天的喜好词列表，* 0.82 时间衰减 大约可保持四天左右
     * 用衰减后的前一天的喜好词列表 + 新计算的当天的喜好词列表 = 新一天的用户喜好词列表
     * 列表存入redis 覆盖前一天的缓存
     * 根据每个用户的喜好词列表  查找出用户对应的个性化推荐视频列表（只有视频有个性化推荐）
     * 视频列表只要六个 如果不够 hot补
     * 将redis中前一天的个性化推荐视频列表 删除 存入新的
     * （一天中用户 有增减， 覆盖可能导致一部分删除的用户信息永远存在)
     * 每天1点执行
     * */
    @Scheduled(cron = "0 0 1 * * ? *")
    private void calFavoriteWord() {
        List<User> userList = userDao.getAllUser();
        System.out.println("用户列表：" + userList);
        Set<Integer> userSet = userList.stream().map(User::getId).collect(Collectors.toSet());
        HashMap<Integer, List<FavoriteWordModel>> redisUserWordMap = (HashMap<Integer, List<FavoriteWordModel>>) redisUtil.get("userWordMap");
        System.out.println("redis中昨天的用户喜欢表:" + redisUserWordMap);
        HashMap<Integer, List<FavoriteWordModel>> newRedisUserWordMap = new HashMap<>();//今天的
        //今天生成的用户 - 喜好词列表 cf-idf
        HashMap<Integer, List<FavoriteWordModel>> CFIDFUserWordMap = new HashMap<>();
        //存储全部用户的喜好词表
        for(int userId : userSet) {
            List<FavoriteWordModel> CFIDFList = getOneVideoFavoriteList(userId);
            System.out.println("用户的keyWordList " + userId + "：\n"  + CFIDFList);
            if(CFIDFList != null){
                CFIDFUserWordMap.put(userId, CFIDFList);
            }
        }

        //生成全部用户的协同过滤
        UserCFUtil userCFUtil = new UserCFUtil(CFIDFUserWordMap);
        HashMap<Integer, List<FavoriteWordModel>> similarUserWordMap = new HashMap<>();
        for(int userId : userSet) {
            if(CFIDFUserWordMap.get(userId) == null) {
                System.out.println(userId + "该用户无CFIDFUserWord -》 无recommendLevelList");
                continue;
            }
            //每一个关键词的推荐度
            List<FavoriteWordModel> recommendLevelList = userCFUtil.getRecommendLevel(userId);
            if(recommendLevelList == null || recommendLevelList.isEmpty()) {
                System.out.println(userId + "该用户无recommendLevelList");
                continue;
            }
            //排序
            recommendLevelList.sort((o1, o2) -> {
                return (int) (o2.getIndex() - o1.getIndex());
            });
            //截取大于0的
            int index = recommendLevelList.size() - 1;
            for(; index >= 0; index++) {
                double num = recommendLevelList.get(index).getIndex();
                if(num >= 1) break;
            }
            System.out.println("协同推荐：" + recommendLevelList);
            //截取前三个
            index = index > 3 ? 3 : index;
            recommendLevelList = recommendLevelList.subList(0, index);
            System.out.println("协同推荐 截取：" + recommendLevelList);
            similarUserWordMap.put(userId, recommendLevelList);
        }

        for(int userId : userSet) {
            List<FavoriteWordModel> redisList;
            HashMap<String, Double> redisMap = new HashMap<>();
            if(redisUserWordMap != null && (redisList = redisUserWordMap.get(userId)) != null){
                //redis * 0.9
                for(FavoriteWordModel model : redisList){
                    redisMap.put(model.getWord(), model.getIndex() * 0.82);
                }
            }
            System.out.println("redis * 0.9:" + redisMap);

            //加在一起
            List<FavoriteWordModel> favoriteList = CFIDFUserWordMap.get(userId);
            if(favoriteList != null){
                List<FavoriteWordModel> similarList = similarUserWordMap.get(userId);
                if(similarList != null){
                    favoriteList.addAll(similarList);
                }
                for(FavoriteWordModel model : favoriteList){
                    String word = model.getWord();
                    double newIndex = model.getIndex();
                    Double index = redisMap.get(word);
                    if(index != null){
                        index += newIndex;
                    } else if(newIndex > 1){
                        index = newIndex;
                    }
                    redisMap.put(word, index);
                }
            }
            System.out.println("加在一起的:" + redisMap);

            if(redisMap.size() > 0){
                //生成新的redisList
                redisList = new ArrayList<>();
                // 去除小于1的
                for(Map.Entry<String, Double> entry : redisMap.entrySet()){
                    double index = entry.getValue();
                    if(index > 1) {
                        redisList.add(new FavoriteWordModel(entry.getKey(), index));
                    }
                }
            } else {
                redisList = null;
            }

            System.out.println("新生成的：" + redisList);
            newRedisUserWordMap.put(userId, redisList);
        }
        //写入redis 关键词 覆盖原来的
        redisUtil.set("userWordMap", newRedisUserWordMap);

        //存入用户喜好视频列表
        for (Map.Entry<Integer, List<FavoriteWordModel>> entry : newRedisUserWordMap.entrySet()){
            int userId = entry.getKey();
            List<FavoriteWordModel> wordList = entry.getValue();
            for (int i = 2; i < 7; i++){
                String part = EStudyPart.values()[i].getEnglish();
                List<VideoModel> list = getVideoList(part, userId, wordList);
                //删除原来的写入新的
                redisUtil.remove("userVideoMap:" + part);
                redisUtil.set("app:study:" + part + ":recommend:userId_" + userId, list);
            }
        }
    }

    //获取用户视频列表
    private List<VideoModel> getVideoList(String part, int userId, List<FavoriteWordModel> wordList){
        List<VideoModel> list = new ArrayList<>();
        HashSet<Integer> set = new HashSet<>();
        if(wordList == null || wordList.isEmpty()){
            //存入六个hot
            list = videoDao.getVideoByCount(part, 6);
        } else {
            for(FavoriteWordModel model : wordList){
                String word = model.getWord();
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
                System.out.println("查完wordList时" + set);
            }
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
            List<VideoModel> collectList = collectDao.getAllVideoCollect(part, userId);
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
