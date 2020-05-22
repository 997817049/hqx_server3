package com.zty.hqx.service;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.dao.ClassifyDao;
import com.zty.hqx.dao.CountDao;
import com.zty.hqx.model.ClassifyModel;
import com.zty.hqx.model.CountModel;
import com.zty.hqx.util.ZtyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CountService {
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    CountDao countDao;
    @Autowired
    ClassifyDao classifyDao;

    /**
     * 获取study下的某个part的每一个label的指定时间段内time的浏览量对比图
     * eg：本周和上周 每个label的浏览量
     * @return list = {上一次的浏览量map<标签，标签浏览量>，这一次的浏览量map<标签，标签浏览量>}
     * */
    public List<HashMap<String, Integer>> getContrastCount(EStudyPart part, String time) throws ParseException {
        String[] nowTimeArr = getTime(time);//本次时间范围
        String[] preTimeArr = getPreTime(nowTimeArr[0], time);//上次时间范围
        HashMap<String, Integer> preMap = new HashMap<>();
        HashMap<String, Integer> nowMap = new HashMap<>();

        //time存储label 每个model = label + count
        List<CountModel> preCountList = countDao.getLabelContrastCount(part.getType(), preTimeArr[0], preTimeArr[1]);
        List<CountModel> nowCountList = countDao.getLabelContrastCount(part.getType(), nowTimeArr[0], nowTimeArr[1]);
        Map<String, Integer> preCountMap = preCountList.stream().collect(Collectors.toMap(CountModel::getTime, CountModel::getCount));
        Map<String, Integer> nowCountMap = nowCountList.stream().collect(Collectors.toMap(CountModel::getTime, CountModel::getCount));

        List<ClassifyModel> classifyList = classifyDao.getClassify(part.getEnglish());
        for (int i = 0; i < classifyList.size(); i++) {
            String msg = classifyList.get(i).getMsg();
            String label = classifyList.get(i).getNum() + "";
            preMap.put(msg, preCountMap.get(label));
            nowMap.put(msg, nowCountMap.get(label));
        }

        List<HashMap<String, Integer>> list = new ArrayList<>();
        list.add(preMap);
        list.add(nowMap);
        return list;
    }

    /**
     * 获取study中指定part的指定时间段time的浏览量
     * @return map<标签， list<每一天的浏览量>> 该part的分标签浏览量
     *         map<all， list<每一天的浏览量>> 该part的总浏览量
     * */
    public HashMap<String, List<CountModel>> getStudyCount(String partStr, String time){
        HashMap<String, List<CountModel>> hashMap = new HashMap<>();
        String[] timeArr = getTime(time);

        EStudyPart part = EStudyPart.getEnumFromString(partStr.toUpperCase());
        //获取指定时间内study中某个part每天的总浏览量
        List<CountModel> list = countDao.getPartCountByTime(EModel.STUDY.getType(), part.getType(), timeArr[0], timeArr[1]);
        hashMap.put("all", list);

        List<ClassifyModel> classifyList = classifyDao.getClassify(partStr);
        for (ClassifyModel classifyModel : classifyList) {
            List<CountModel> labelList = countDao.getLabelCountByTime(EModel.STUDY.getType(), part.getType(), classifyModel.getNum(), timeArr[0], timeArr[1]);
            hashMap.put(classifyModel.getEnglish(), labelList);
        }
        return hashMap;
    }

    //获取base的浏览量
    public HashMap<String, List<CountModel>> getBaseCount(String time){
        String[] timeArr = getTime(time);
        List<CountModel> list = countDao.getBaseCountByTime(timeArr[0], timeArr[1]);
        HashMap<String, List<CountModel>> hashMap = new HashMap<>();
        hashMap.put("all", list);
        return hashMap;
    }

    //获取排行榜
    public HashMap<String, List<String>> getChart(String modelStr, String partStr, String time, int type){
        String[] timeArr = getTime(time);

        EModel model = EModel.getEnumFromString(modelStr.toUpperCase());
        HashMap<String, List<String>> rs = new HashMap<>();
        switch (model){
            case BASE:
                List<String> list;
                if(type == 0) {
                    list = countDao.getBaseTopChart(timeArr[0], timeArr[1]);
                } else {
                    list = countDao.getBaseBottomChart(timeArr[0], timeArr[1]);
                }
                rs.put("all", list);
                break;
            case STUDY:
                EStudyPart part = EStudyPart.getEnumFromString(partStr.toUpperCase());
                List<ClassifyModel> classifyList = classifyDao.getClassify(part.getEnglish());
                if(type == 0) {
                    rs = getTopChart(part, classifyList, timeArr[0], timeArr[1]);
                } else {
                    rs = getBottomChart(part, classifyList, timeArr[0], timeArr[1]);
                }
                break;
        }
        return rs;
    }

    //<标签， 排行榜>
    private HashMap<String, List<String>> getTopChart(EStudyPart part, List<ClassifyModel> classifyList, String time1, String time2){
        HashMap<String, List<String>> hashMap = new HashMap<>();
        //总浏览量排行榜
        List<String> list1 = countDao.getPartTopChart(part.getEnglish(), part.getType(), time1, time2);
        hashMap.put("all", list1);
        for(ClassifyModel classify : classifyList){
            List<String> list2 = countDao.getLabelTopChart(part.getEnglish(), part.getType(), classify.getNum(), time1, time2);
            hashMap.put(classify.getMsg(), list2);
        }
        return hashMap;
    }

    private HashMap<String, List<String>> getBottomChart(EStudyPart part, List<ClassifyModel> classifyList, String time1, String time2){
        HashMap<String, List<String>> hashMap = new HashMap<>();
        //总浏览量排行榜
        List<String> list1 = countDao.getPartBottomChart(part.getEnglish(), part.getType(), time1, time2);
        hashMap.put("all", list1);
        for(ClassifyModel classify : classifyList){
            List<String> list2 = countDao.getLabelBottomChart(part.getEnglish(), part.getType(), classify.getNum(), time1, time2);
            hashMap.put(classify.getMsg(), list2);
        }
        return hashMap;
    }

    private String[] getTime(String time){
        String time2 = ZtyUtil.getNowTime();
        String time1 = "";
        switch (time){
            case "week": time1 = ZtyUtil.getLastWeek(); break;
            case "month": time1 = ZtyUtil.getLastMonth(); break;
            case "year": time1 = ZtyUtil.getLastYear(); break;
        }
        return new String[]{time1, time2};
    }

    private String[] getPreTime(String time2, String time) throws ParseException {
        String time1 = "";
        switch (time){
            case "week": time1 = ZtyUtil.getLastWeek(time2); break;
            case "month": time1 = ZtyUtil.getLastMonth(time2); break;
            case "year": time1 = ZtyUtil.getLastYear(time2); break;
        }
        return new String[]{time1, time2};
    }
}
