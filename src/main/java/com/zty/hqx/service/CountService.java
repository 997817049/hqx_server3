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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class CountService {
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    CountDao countDao;
    @Autowired
    ClassifyDao classifyDao;

    public List<HashMap<String, Integer>> getContrastCount(EModel model, String partStr, String time){
        String[] nowTimeArr = getTime(time);//本次时间范围
        String[] preTimeArr = getPreTime(nowTimeArr[0], time);//上次时间范围
        HashMap<String, Integer> preMap = new HashMap<>();
        HashMap<String, Integer> nowMap = new HashMap<>();
        switch (model){
            case NEWS:break;
            case STUDY:
                EStudyPart part = EStudyPart.getEnumFromString(partStr.toUpperCase());
                List<ClassifyModel> classifyList = classifyDao.getClassify(partStr);
                for (ClassifyModel classify : classifyList) {
                    int preCount = countDao.getLabelContrastCount(model.getType(), part.getType(), classify.getNum(), preTimeArr[0], preTimeArr[1]);
                    int nowCount = countDao.getLabelContrastCount(model.getType(), part.getType(), classify.getNum(), nowTimeArr[0], nowTimeArr[1]);
                    preMap.put(classify.getMsg(), preCount);
                    nowMap.put(classify.getMsg(), nowCount);
                }
                break;
        }
        List<HashMap<String, Integer>> list = new ArrayList<>();
        list.add(preMap);
        list.add(nowMap);
        return list;
    }

    public HashMap<String, List<CountModel>> getStudyCount(String partStr, String time){
        HashMap<String, List<CountModel>> hashMap = new HashMap<>();
        String[] timeArr = getTime(time);
        timeArr = dateSplit(timeArr[0], timeArr[1]);//指定日期内的全部时间

        EStudyPart part = EStudyPart.getEnumFromString(partStr.toUpperCase());
        //获取该part的总浏览量
        List<CountModel> list1 = getPartCount(part.getType(), timeArr);
        hashMap.put("all", list1);

        List<ClassifyModel> classifyList = classifyDao.getClassify(partStr);
        for(int i = 0; i < classifyList.size(); i++) {
            List<CountModel> list = getLabelCount(part.getType(), classifyList.get(i).getNum(), timeArr);
            hashMap.put(classifyList.get(i).getEnglish(), list);
        }
        return hashMap;
    }

    //获取base的浏览量
    public HashMap<String, List<CountModel>> getBaseCount(String time){
        String[] timeArr = getTime(time);
        timeArr = dateSplit(timeArr[0], timeArr[1]);//指定日期内的全部时间
        List<CountModel> list = new ArrayList<>();
        for(String str : timeArr){
            int count = countDao.getModelCountByTime(EModel.BASE.getType(), str);
            list.add(new CountModel(str, count));
        }
        HashMap<String, List<CountModel>> hashMap = new HashMap<>();
        hashMap.put("all", list);
        return hashMap;
    }

    //获取指定时间内study中某个part每天的总浏览量
    private List<CountModel> getLabelCount(int part, int label, String[] timeArr){
        List<CountModel> list = null;
        for(String str : timeArr){
            int count = countDao.getLabelCountByTime(EModel.STUDY.getType(), part, label, str);
            list.add(new CountModel(str, count));
        }
        return list;
    }

    //获取指定时间内study中某个part每天的总浏览量
    private List<CountModel> getPartCount(int part, String[] timeArr){
        List<CountModel> list = null;
        for(String str : timeArr){
            int count = countDao.getPartCountByTime(EModel.STUDY.getType(), part, str);
            list.add(new CountModel(str, count));
        }
        return list;
    }

    public HashMap<String, List<String>> getChart(String modelStr, String partStr, String time, int type){
        String[] timeArr = getTime(time);

        EModel model = EModel.getEnumFromString(modelStr.toUpperCase());
        HashMap<String, List<String>> rs = new HashMap<>();
        switch (model){
            case BASE:
                List<String> list;
                if(type == 0) {
                    list = countDao.getModelTopChart(model.getType(), modelStr, timeArr[0], timeArr[1]);
                } else {
                    list = countDao.getModelBottomChart(model.getType(), modelStr, timeArr[0], timeArr[1]);
                }
                rs.put("all", list);
                break;
            case STUDY:
                EStudyPart part = EStudyPart.getEnumFromString(partStr.toUpperCase());
                List<ClassifyModel> classifyList = classifyDao.getClassify(part.getEnglish());
                if(type == 0) {
                    rs = getTopChart(model.getType(), part, classifyList, timeArr[0], timeArr[1]);
                } else {
                    rs = getBottomChart(model.getType(), part, classifyList, timeArr[0], timeArr[1]);
                }
                break;
        }
        return rs;
    }

    //<标签， 排行榜>
    private HashMap<String, List<String>> getTopChart(int model, EStudyPart part, List<ClassifyModel> classifyList, String time1, String time2){
        HashMap<String, List<String>> hashMap = new HashMap<>();
        //总浏览量排行榜
        List<String> list1 = countDao.getPartTopChart(model, part.getEnglish(), part.getType(), time1, time2);
        hashMap.put("all", list1);
        for(ClassifyModel classify : classifyList){
            List<String> list2 = countDao.getLabelTopChart(model, part.getEnglish(), part.getType(), classify.getNum(), time1, time2);
            hashMap.put(classify.getMsg(), list2);
        }
        return hashMap;
    }

    private HashMap<String, List<String>> getBottomChart(int model, EStudyPart part, List<ClassifyModel> classifyList, String time1, String time2){
        HashMap<String, List<String>> hashMap = new HashMap<>();
        //总浏览量排行榜
        List<String> list1 = countDao.getPartBottomChart(model, part.getEnglish(), part.getType(), time1, time2);
        hashMap.put("all", list1);
        for(ClassifyModel classify : classifyList){
            List<String> list2 = countDao.getLabelBottomChart(model, part.getEnglish(), part.getType(), classify.getNum(), time1, time2);
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

    private String[] getPreTime(String time2, String time){
        try {
            Date  date = DateFormat.getDateTimeInstance().parse(time2);
            String time1 = "";
            switch (time){
                case "week": time1 = ZtyUtil.getLastWeek(date); break;
                case "month": time1 = ZtyUtil.getLastMonth(date); break;
                case "year": time1 = ZtyUtil.getLastYear(date); break;
            }
            return new String[]{time1, time2};
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 返回指定的时间段内的有所日期
     *
     * @param startDate 开始时间,例:2016-02-25
     * @param endDate   结束时间,例:2016-03-05
     * @return
     */
    private static String[] dateSplit(String startDate, String endDate) {
        String[] ar = {};
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date newstartDate;
        try {
            newstartDate = simpleDateFormat.parse(startDate);
            Date newendDate = simpleDateFormat.parse(endDate);
            List<String> dateList = new ArrayList<String>();
            Long spi = newendDate.getTime() - newstartDate.getTime();
            Long step = spi / (24 * 60 * 60 * 1000);// 相隔天数
            List<Date> newdateList = new ArrayList<Date>();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            newdateList.add(newstartDate);
            dateList.add(startDate);
            for (int i = 1; i <= step; i++) {
                dateList.add(formatter.format(new Date(newdateList.get(i - 1).getTime() + (24 * 60 * 60 * 1000))));
                newdateList.add(new Date(newdateList.get(i - 1).getTime() + (24 * 60 * 60 * 1000)));// 比上一天+一
            }
            String[] strings = new String[dateList.size()];
            return dateList.toArray(strings);
        } catch (ParseException e) {
            e.printStackTrace();
            return ar;
        }
    }
}
