package com.zty.hqx.dao;

import com.zty.hqx.model.*;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Result;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Mapper
public interface CollectDao {

    @Select("SELECT progress FROM collect WHERE userId = #{userId} and modelId = #{model} and id = #{id}")
    String isBaseCollect(CollectModel collectModel);

    @Select("SELECT progress FROM collect WHERE userId = #{userId} and modelId = #{model} and partId = #{part} and id = #{id}")
    String isCollect(CollectModel collectModel);

    @Insert("INSERT INTO collect(userId, modelId, partId, id, progress, create_time, update_time) VALUES(#{userId}, #{model}, #{part}, #{id}, '${progress}', now(), now())")
    void insertCollect(CollectModel collectModel);

    @Update("update collect set progress = '${progress}', update_time = now() where userId = #{userId} and modelId = #{model} and partId = #{part} and id = #{id}")
    void updateCollect(CollectModel collectModel);

    @Delete("DELETE FROM collect WHERE userId = #{userId} and modelId = #{model} and id = #{id}")
    void deleteBaseCollect(CollectModel collectModel);

    @Delete("DELETE FROM collect WHERE userId = #{userId} and modelId = #{model} and partId = #{part} and id = #{id}")
    void deleteCollect(CollectModel collectModel);

    @Delete("DELETE FROM collect WHERE modelId = #{model} and id = #{id}")
    void deleteAllBaseCollect(CollectModel collectModel);

    @Delete("DELETE FROM collect WHERE modelId = #{model} and partId = #{part} and id = #{id}")
    void deleteAllCollect(CollectModel collectModel);

    @Select("SELECT exam.*, e_exam.*, collect.progress\n" +
            "FROM collect, exam, e_exam \n" +
            "WHERE e_exam.num = exam.label and collect.userId = #{userId} and collect.modelId = 1 and collect.partId = 0 and collect.id = exam.id " +
            "order by collect.create_time desc")
    @Results({@Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    List<ExamModel> getAllExamCollect(int userId);

    @Select("SELECT book.*, e_book.*, collect.progress\n" +
            "FROM collect, book, e_book \n" +
            "WHERE e_book.num = book.label and collect.userId = #{userId} and collect.modelId = 1 and collect.partId = 1 and collect.id = book.id " +
            "order by collect.create_time desc")
    @Results({@Result(property = "picUrl", column = "pic"),
            @Result(property = "fileUrl", column = "file"),
            @Result(property = "label.num", column = "num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english"),})
    List<BookModel> getAllBookCollect(int userId);

    @Select("SELECT ${part}.*, e_${part}.*, collect.progress\n" +
            "FROM collect, ${part}, e_${part} \n" +
            "WHERE e_${part}.num = ${part}.label and collect.userId = #{userId} and collect.modelId = 1 and collect.partId = 2 and collect.id = ${part}.id " +
            "order by collect.create_time desc")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "label.num", column = "e_${part}.num"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english")
    })
    List<VideoModel> getAllVideoCollect(String part, int userId);

    @Select("SELECT base.* \n" +
            "FROM collect, base WHERE collect.userId = #{userId} and collect.modelId = 3 AND collect.id = base.id " +
            "order by collect.create_time desc")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    List<BaseModel> getAllBaseCollect(int userId);

//    @Select("select count(*) from information_schema.TABLES t where t.TABLE_NAME ='user_collection_${userId}';")
//    int tableIsExixt(int userId);
//
//    @Update("CREATE TABLE `user_collection_${userId}`(\n" +
//            "\tno INT(11) auto_increment COMMENT '编号',\n" +
//            "\tmodelId INT(11) NOT NULL COMMENT '模块',\n" +
//            "\tpartId INT(11) NOT NULL COMMENT '模块',\n" +
//            "\tid INT(11) NOT NULL COMMENT '收藏的信息id',\n" +
//            "\tprogress text DEFAULT NULL COMMENT '进度',\n" +
//            "\tcreate_time datetime DEFAULT null,\n" +
//            "\tupdate_time timestamp COMMENT '默认为当前时间',\n" +
//            "\tPRIMARY KEY (no)\n" +
//            ")ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=UTF8")
//    boolean creatTable(int userId);
}
