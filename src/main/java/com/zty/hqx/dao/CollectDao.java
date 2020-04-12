package com.zty.hqx.dao;

import com.zty.hqx.model.*;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper
public interface CollectDao {

    @Select("SELECT progress FROM user_collection_#{userId} WHERE modelId = #{model} and id = #{id}")
    String isBaseCollect(CollectModel collectModel);

    @Select("SELECT progress FROM user_collection_#{userId} WHERE modelId = #{model} and partId = #{part} and id = #{id}")
    String isCollect(CollectModel collectModel);

    @Insert("INSERT INTO user_collection_#{userId}(userId, modelId, partId, id, progress, create_time, update_time) VALUES(#{userId}, #{model}, #{part}, #{id}, '${progress}', now(), now())")
    void insertCollect(CollectModel collectModel);

    @Update("update user_collection_#{userId} set progress = '${progress}', update_time = now() where modelId = #{model} and partId = #{part} and id = #{id}")
    void updateCollect(CollectModel collectModel);

    @Delete("DELETE FROM user_collection_#{userId} WHERE modelId = #{model} and id = #{id}")
    void deleteBaseCollect(CollectModel collectModel);

    @Delete("DELETE FROM user_collection_#{userId} WHERE modelId = #{model} and partId = #{part} and id = #{id}")
    void deleteCollect(CollectModel collectModel);

    @Select("SELECT exam.*, user_collection_#{userId}.progress\n" +
            "FROM user_collection_#{userId}, exam \n" +
            "WHERE user_collection_#{userId}.modelId = 1 and user_collection_#{userId}.partId = 0 and user_collection_#{userId}.id = exam.id")
    List<ExamModel> getAllExamCollect(int userId);

    @Select("SELECT book.*, user_collection_#{userId}.progress\n" +
            "FROM user_collection_#{userId}, book \n" +
            "WHERE user_collection_#{userId}.modelId = 1 and user_collection_#{userId}.partId = 1 and user_collection_#{userId}.id = book.id \n")
    List<BookModel> getAllBookCollect(int userId);

    @Select("SELECT ${part}.*, user_collection_#{userId}.progress\n" +
            "FROM user_collection_#{userId}, ${part} \n" +
            "WHERE user_collection_#{userId}.modelId = 1 and user_collection_#{userId}.partId = 2 and user_collection_#{userId}.id = ${part}.id")
    List<VideoModel> getAllVideoCollect(String part, int userId);

    @Select("SELECT base.* \n" +
            "FROM user_collection_#{userId}, base WHERE modelId = 3 AND user_collection_#{userId}.id = base.id")
    List<BaseModel> getAllBaseCollect(int userId);

    @Select("select count(*) from information_schema.TABLES t where t.TABLE_NAME ='user_collection_${userId}';")
    int tableIsExixt(int userId);

    @Update("CREATE TABLE `user_collection_${userId}`(\n" +
            "\tno INT(11) auto_increment COMMENT '编号',\n" +
            "\tmodelId INT(11) NOT NULL COMMENT '模块',\n" +
            "\tpartId INT(11) NOT NULL COMMENT '模块',\n" +
            "\tid INT(11) NOT NULL COMMENT '收藏的信息id',\n" +
            "\tprogress text DEFAULT NULL COMMENT '进度',\n" +
            "\tcreate_time datetime DEFAULT null,\n" +
            "\tupdate_time timestamp COMMENT '默认为当前时间',\n" +
            "\tPRIMARY KEY (no)\n" +
            ")ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8")
    boolean creatTable(int userId);
}
