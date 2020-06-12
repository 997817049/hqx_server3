package com.zty.hqx.dao;

import com.zty.hqx.classify.EModel;
import com.zty.hqx.model.BookModel;
import com.zty.hqx.model.CollectModel;
import com.zty.hqx.model.VideoModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HistoryDao {
    @Insert("INSERT INTO history(userId, modelId, partId, id, create_time, update_time) VALUES(#{userId}, #{modelId}, #{partId}, #{id}, now(), now())")
    void insertHistory(int userId, int modelId, int partId, int id);

    @Select("SELECT create_time FROM history WHERE userId = #{userId} and modelId = #{modelId} and partId = #{partId} and id = #{id}")
    String getOneHistory(int userId, int modelId, int partId, int id);

    @Select("SELECT id FROM history WHERE userId = #{userId} and modelId = #{modelId} and partId = #{partId} ORDER BY update_time DESC LIMIT #{limit} OFFSET #{num}")
    List<Integer> getHistory(int userId, int modelId, int partId, int num, int limit);

    @Select("SELECT id FROM history WHERE userId = #{userId} and modelId = #{modelId} and partId = #{partId} and update_time like '${time}%'")
    List<Integer> getHistoryByTime(int userId, int modelId, int partId, String time);

    @Update("update history set update_time = now() where userId = #{userId} and modelId = #{modelId} and partId = #{partId} and id = #{id}")
    void updateHistory(int userId, int modelId, int partId, int id);

    @Delete("DELETE FROM history WHERE modelId = #{modelId} and id = #{id}")
    void deleteAllBaseHistory(int modelId, int id);

    @Delete("DELETE FROM history WHERE modelId = #{modelId} and partId = #{partId} and id = #{id}")
    void deleteAllHistory(int modelId, int partId, int id);
}
