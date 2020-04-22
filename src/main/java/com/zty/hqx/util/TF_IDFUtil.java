package com.zty.hqx.util;

import com.zty.hqx.model.FavoriteWordModel;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;

import java.util.*;

public class TF_IDFUtil {

    public static List<FavoriteWordModel> getKeyWord(List<String> docList){
        List<String> allWordList = spiltAllDoc(docList);
        HashSet<String> allWordSet = new HashSet<>(allWordList);//存入全部词汇

        HashMap<String, Double> TFMap = calTF(allWordList);
        //计算idf
        HashMap<String, Double> IDFMap = calIDF(allWordSet, docList);
        return calTF_IDF(allWordSet, TFMap, IDFMap);
    }

    private static List<String> spiltAllDoc(List<String> docList){
        //遍历每一个文档，先分词
        StringBuilder stringBuilder = new StringBuilder();
        for(String doc : docList){
            stringBuilder.append(spiltDoc(doc));//分词结果
        }
        return Arrays.asList(stringBuilder.toString().split(" "));
    }

    //分词
    private static StringBuilder spiltDoc(String str){
        StringBuilder result = new StringBuilder();
        //保留停用词
        for(Word word : WordSegmenter.segWithStopWords(str, SegmentationAlgorithm.MaxNgramScore)){
            String txt = word.getText();//只记录大于1的词
            if(txt.length() > 1) {
                result.append(txt).append(" ");
            }
        }
        return result;
    }

    //统计每个词汇出现次数 计算词频
    private static HashMap<String, Double> calTF(List<String> allWordList){
        //double wordCount = allWordList.size();//词汇总数
        HashMap<String, Double> TFMap = new HashMap<>();
        for(String word : allWordList) {
            TFMap.merge(word, 1.0, (a, b) -> a + b);
        }

        //计算词频tf  将次数变为词频 次数/总数
//        for(Map.Entry<String, Double> entry : TFMap.entrySet()) {
//            Double count = entry.getValue();
//            entry.setValue(count / wordCount);
//        }
        return TFMap;
    }

    //计算idf
    private static HashMap<String, Double> calIDF(Set<String> allWordSet, List<String> docList){
        double docCount = docList.size();
        HashMap<String, Double> IDFMap = new HashMap<>();
        for(String word : allWordSet) { //遍历每一个词
            int count = 0;
            for(String doc : docList){
                if(doc.contains(word)) {
                    count ++;
                }
            }
            IDFMap.put(word, Math.log( docCount / (count+1) ));
        }
        return IDFMap;
    }

    //计算tf-idf并排序
    private static List<FavoriteWordModel> calTF_IDF(Set<String> allWordSet, HashMap<String, Double> TFMap, HashMap<String, Double> IDFMap){
        List<FavoriteWordModel> TF_IDFList = new ArrayList<>();
        for(String word : allWordSet) {
            TF_IDFList.add(new FavoriteWordModel(word, TFMap.get(word) * IDFMap.get(word)));
        }

        TF_IDFList.sort(new Comparator<FavoriteWordModel>() {
            //升序排序
            public int compare(FavoriteWordModel o1, FavoriteWordModel o2) {
                if(o2.getIndex() >= o1.getIndex()){
                    return 1;
                }
                return -1;
            }
        });

        return TF_IDFList;
    }

}
