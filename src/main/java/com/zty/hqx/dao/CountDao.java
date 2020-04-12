package com.zty.hqx.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CountDao {

    @Update("UPDATE ${table} SET count = 0")
    boolean updateAllCount(String table);

    @Insert("INSERT INTO count (model, part, label, id, time, count) VALUES (#{model}, #{part}, #{label}, #{id}, '${time}', #{count})")
    boolean insertCount(int model, int part, int label, int id, String time, int count);

    //指定model、part和label的count
    @Select("SELECT SUM(count) FROM count WHERE model = #{model} AND part = #{part} AND label = #{label} AND time BETWEEN '${time1}' AND '${time2}'")
    int getLabelContrastCount(int model, int part, int label, String time1, String time2);

    //指定model、part和label的count
    @Select("SELECT SUM(count) FROM count WHERE model = #{model} AND part = #{part} AND label = #{label} AND time = '${time}'")
    int getLabelCountByTime(int model, int part, int label, String time);

    @Select("SELECT SUM(count) FROM count WHERE model = #{model} AND part = #{part} AND time = '${time}'")
    int getPartCountByTime(int model, int part, String time);

    //获取指定时间某个model的总浏览量
    @Select("SELECT SUM(count) FROM count WHERE model = #{model} AND time = '${time}'")
    int getModelCountByTime(int model, String time);

    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = #{model} AND count.part = #{partNum} AND count.label = #{label} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id ORDER BY count.count LIMIT 10")
    List<String> getLabelTopChart(int model, String part, int partNum, int label, String time1, String time2);

    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = #{model} AND count.part = #{partNum} AND count.label = #{label} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id ORDER BY count.count DESC LIMIT 10")
    List<String> getLabelBottomChart(int model, String part, int partNum, int label, String time1, String time2);

    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = #{model} AND count.part = #{partNum} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id ORDER BY count.count LIMIT 10")
    List<String> getPartTopChart(int model, String part, int partNum, String time1, String time2);

    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = #{model} AND count.part = #{partNum} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id ORDER BY count.count DESC LIMIT 10")
    List<String> getPartBottomChart(int model, String part, int partNum, String time1, String time2);

    @Select("SELECT ${model}.title FROM count, ${model} WHERE count.model = #{modelNum} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${model}.id ORDER BY count.count LIMIT 10")
    List<String> getModelTopChart(int modelNum, String model, String time1, String time2);

    @Select("SELECT ${part}.title FROM count, ${part} WHERE count.model = #{model} AND count.time BETWEEN '${time1}' AND '${time2}' AND count.id = ${part}.id ORDER BY count.count DESC LIMIT 10")
    List<String> getModelBottomChart(int model, String part, String time1, String time2);
}
