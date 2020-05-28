package com.zty.hqx.dao;

import com.zty.hqx.model.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Result;

import java.util.List;
import java.util.Map;

@Mapper
public interface CountDao {

    @Update("UPDATE ${table} SET count = 0")
    boolean updateAllCount(String table);

    @Insert("INSERT INTO count (model, part, label, id, time, count) VALUES (#{model}, #{part}, #{label}, #{id}, '${time}', #{count})")
    boolean insertCount(int model, int part, int label, int id, String time, int count);

    //指定study下某个part的一段时间内每个label的总浏览量
    @Select("SELECT label, SUM(count) FROM count WHERE model = 1 AND part = #{part} AND time BETWEEN '${time1}' AND '${time2}' GROUP BY label")
    @Results({
            @Result(property = "time", column = "label"),
            @Result(property = "count", column = "SUM(count)")
    })
    List<CountModel> getLabelContrastCount(int part, String time1, String time2);

    //获取指定时间段内 study的某个part的某个label的总浏览量
    @Select("SELECT time, SUM(count) FROM count WHERE model = #{model} AND part = #{part} AND label = #{label} AND time BETWEEN '${time1}' AND '${time2}' GROUP BY time")
    @Results({
            @Result(property = "count", column = "SUM(count)")
    })
    List<CountModel> getLabelCountByTime(int model, int part, int label, String time1, String time2);

    //获取指定时间段内 study的某个part的总浏览量
    @Select("SELECT time, SUM(count) FROM count WHERE model = #{model} AND part = #{part} AND time BETWEEN '${time1}' AND '${time2}' GROUP BY time")
    @Results({
            @Result(property = "count", column = "SUM(count)")
    })
    List<CountModel> getPartCountByTime(int model, int part, String time1, String time2);

    //获取指定时间段内 base的总浏览量
    @Select("SELECT time, SUM(count) FROM count WHERE model = 3 AND time BETWEEN '${time1}' AND '${time2}' GROUP BY time")
    @Results({
            @Result(property = "count", column = "SUM(count)")
    })
    List<CountModel> getBaseCountByTime(String time1, String time2);

    //获取study中每个part的每个label的排行榜
    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = 1 AND count.part = #{partNum} AND ${part}.label = #{label} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id GROUP BY count.id ORDER BY SUM(count.count) DESC LIMIT 10")
    List<String> getLabelTopChart(String part, int partNum, int label, String time1, String time2);

    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = 1 AND count.part = #{partNum} AND ${part}.label = #{label} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id GROUP BY count.id ORDER BY SUM(count.count) LIMIT 10")
    List<String> getLabelBottomChart(String part, int partNum, int label, String time1, String time2);

    //获取study中每个part的排行榜
    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = 1 AND count.part = #{partNum} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id GROUP BY count.id ORDER BY SUM(count.count) DESC LIMIT 10")
    List<String> getPartTopChart(String part, int partNum, String time1, String time2);

    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = 1 AND count.part = #{partNum} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id GROUP BY count.id ORDER BY SUM(count.count) LIMIT 10")
    List<String> getPartBottomChart(String part, int partNum, String time1, String time2);

    //获取基地的排行榜
    @Select("SELECT base.title FROM count, base WHERE count.model = 3 AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = base.id GROUP BY count.id ORDER BY SUM(count.count) DESC LIMIT 10")
    List<String> getBaseTopChart(String time1, String time2);

    @Select("SELECT base.title FROM count, base WHERE count.model = 3 AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = base.id GROUP BY count.id ORDER BY SUM(count.count) LIMIT 10")
    List<String> getBaseBottomChart(String time1, String time2);

    @Select("SELECT base.* FROM count, base WHERE count.model = 3 AND count.time = '${time}' AND count.id = base.id ORDER BY count.count LIMIT #{limit}")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    List<BaseModel> getBaseHot(String time, int limit);

    @Select("SELECT id FROM count WHERE model = 1 AND part = 1 AND time = '${time}' ORDER BY count LIMIT #{limit}")
    List<Integer> getBookHot(String time, int limit);

    @Select("SELECT id FROM count WHERE model = 1 AND part = #{partNum} AND time = '${time}' ORDER BY count LIMIT #{limit}")
    List<Integer> getVideoHot(String part, int partNum, String time, int limit);
}
