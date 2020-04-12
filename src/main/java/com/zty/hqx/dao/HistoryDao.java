package com.zty.hqx.dao;

import com.zty.hqx.model.CollectModel;
import org.apache.ibatis.annotations.*;

@Mapper
public interface HistoryDao {
    @Insert("INSERT INTO history(userId, modelId, partId, id, create_time, update_time) VALUES(#{userId}, #{modelId}, #{partId}, #{id}, now(), now())")
    void insertHistory(int userId, int modelId, int partId, int id);

    @Select("SELECT userId, modelId, partId, id, progress FROM history WHERE userId = #{userId} and modelId = #{modelId} and partId = #{partId} ORDER BY create_time DESC LIMIT #{limit}")
    CollectModel getRecentHistory(int userId, int modelId, int partId, int limit);
}
