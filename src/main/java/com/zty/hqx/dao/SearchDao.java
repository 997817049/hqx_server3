package com.zty.hqx.dao;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SearchDao {
    @Select("SELECT word FROM search ORDER BY COUNT DESC LIMIT 5")
    List<String> getHotWord();

    @Update("insert into search set word='${word}', create_time = NOW() on duplicate key update count=count+1;")
    void updateWordCount(String word);

    @Update("insert into search_history set userId=#{userId}, word = '${word}', create_time = NOW() on duplicate key update count=count+1;")
    void insertUserWord(int userId, String word);

    @Select("SELECT word FROM search_history ORDER BY update_time DESC LIMIT 5")
    List<String> getUserHistoryWord(int userId);

    @Delete("DELETE FROM search_history WHERE id = #{userId}")
    boolean deleteHistoryWord (int userId);
}
