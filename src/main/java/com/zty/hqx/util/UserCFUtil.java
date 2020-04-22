package com.zty.hqx.util;

import com.zty.hqx.model.FavoriteWordModel;

import java.util.*;

/**
 * 基于用户的协同过滤推荐算法实现
 * 输入用户-->物品条目  一个用户对应多个物品 hashmap<userId, List<FavoriteWord>>
 * 用户ID	物品ID集合
 *   A		a b d
 *   B		a c
 *   C		b e
 *   D		c d e
 *
 *   先根据map生成倒排表，并计算出相似度矩阵
 */
public class UserCFUtil {
    private HashMap<Integer, List<FavoriteWordModel>> userWordMap;
    //建立 喜好词 - 用户列表 倒排表 eg: a A B
    private Map<String, Set<Integer>> wordUserMap = new HashMap<>();
    //建立用户稀疏矩阵，用于用户相似度计算【相似度矩阵】
    private int[][] sparseMatrix;
    private Set<String> items = new HashSet<>();//辅助存储物品集合
    private Map<Integer, Integer> userIndex = new HashMap<>();//辅助存储每一个用户id的在矩阵中对应的下标映射
    private Map<Integer, Integer> indexUser = new HashMap<>();//辅助存储每一个矩阵中的下标对应的用户id映射


    public UserCFUtil(HashMap<Integer, List<FavoriteWordModel>> userWordMap) {
        this.userWordMap = userWordMap;
        //输入用户总量
        int n = userWordMap.size();
        sparseMatrix = new int[n][n];
        calSimilarityMatrix();
    }

    //根据map生成倒排表，并计算出相似度矩阵
    private void calSimilarityMatrix() {
        //生成倒排表
        int index = 0;
        for(Map.Entry<Integer, List<FavoriteWordModel>> entry : userWordMap.entrySet()){
            Integer userId = entry.getKey();
            List<FavoriteWordModel> wordList = entry.getValue();
            userIndex.put(userId, index);//用户ID与稀疏矩阵建立对应关系
            indexUser.put(index, userId);
            for(FavoriteWordModel model : wordList){
                items.add(model.getWord());
            }

            index ++;
            //生成倒排表
            for(FavoriteWordModel model : wordList){
                HashSet<Integer> userSet = (HashSet<Integer>) wordUserMap.get(model.getWord());
                if(userSet == null) {
                    userSet = new HashSet<>();
                    wordUserMap.put(model.getWord(), userSet);
                }
                userSet.add(userId);
            }
        }

        //计算相似度矩阵【稀疏】
        Set<Map.Entry<String, Set<Integer>>> entrySet = wordUserMap.entrySet();
        for (Map.Entry<String, Set<Integer>> entry : entrySet) {
            Set<Integer> commonUsers = entry.getValue();
            for (Integer user_u : commonUsers) {
                for (Integer user_v : commonUsers) {
                    if (user_u.equals(user_v)) {
                        continue;
                    }
                    sparseMatrix[userIndex.get(user_u)][userIndex.get(user_v)] += 1;//计算用户u与用户v都有正反馈的物品总数
                }
            }
        }
    }

    //获取指定用户和其他用户的相似度map
    public HashMap<Integer, Double> getUserSimilarity(int recommendUser){
        HashMap<Integer, Double> map = new HashMap<>();
        //计算用户之间的相似度【余弦相似性】
        int recommendUserIndex = userIndex.get(recommendUser);
        for (int j = 0;j < sparseMatrix.length; j++) {
            if(j != recommendUserIndex){
                int jUserId = indexUser.get(j);
                double num = sparseMatrix[recommendUserIndex][j]
                        / Math.sqrt(userWordMap.get(recommendUser).size())
                        * userWordMap.get(jUserId).size();
                map.put(jUserId, num);
            }
        }
        return map;
    }

    /**
     * 获取给指定用户对推荐的物品
     * @return HashMap<String, Double>
     *     string  物品（此处指word 喜好词）
     *     double 物品的推荐程度
     *
     */
    public List<FavoriteWordModel> getRecommendLevel(int recommendUser){
        List<FavoriteWordModel> list = new ArrayList<>();
        //计算指定用户recommendUser的物品推荐度
        for(String item: items){//遍历每一件物品
            Set<Integer> users = wordUserMap.get(item);//得到购买当前物品的所有用户集合
            if(!users.contains(recommendUser)){//如果被推荐用户没有购买当前物品，则进行推荐度计算
                double itemRecommendDegree = 0.0;
                for(Integer user: users){
                    itemRecommendDegree += sparseMatrix[userIndex.get(recommendUser)][userIndex.get(user)]/Math.sqrt(userWordMap.get(recommendUser).size()*userWordMap.get(user).size());//推荐度计算
                }
                list.add(new FavoriteWordModel(item, itemRecommendDegree));
            }
        }
        return list;
    }
}
