package com.zty.hqx.dao;

import com.zty.hqx.model.VideoContentModel;
import com.zty.hqx.model.VideoModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudyVideoDao {

    //判断名字是否可用
    @Select("SELECT * FROM ${part} WHERE title = '${title}'")
    @Results({@Result(property = "picUrl", column = "pic")})
    VideoModel isTitleAvailable(String part, String title);

//  --------------------------------------增加数据-----------------------------------------------

    @Insert("INSERT INTO ${part}_video (id, num, video, create_time) VALUES (#{id}, #{num}, '${videoUrl}', now())")
    void insertVideoContent(String part, int id, int num, String videoUrl);

    @Options(useGeneratedKeys = true, keyProperty = "model.id", keyColumn = "id")
    @Insert("INSERT INTO ${part} (title, label, num, actor, synopsis, pic, create_time) VALUES ('${model.title}', #{model.label.num}, #{model.num}, '${model.actor}', '${model.synopsis}', '${model.picUrl}', now())")
    void insertVideo(String part, VideoModel model);

//  --------------------------------------删除数据-----------------------------------------------

    @Delete("DELETE FROM ${part}_video WHERE id = #{id} and num = #{num}")
    void deleteVideoContent(String part, int id, int num);

    @Delete("DELETE FROM ${part}_video WHERE id = #{id}")
    void deleteVideoAllContent(String part, int id);

    @Delete("DELETE FROM ${part} WHERE id = #{id}")
    void deleteVideo(String part, int id);

// ------------------------------------更新数据--------------------------------------------------------

    @Update("UPDATE ${part} SET title = '${model.title}', label = '${model.label.num}',num = #{model.num},actor = '${model.actor}', synopsis = '${model.synopsis}',pic = '${model.picUrl}' WHERE id = #{model.id}")
    void updateVideo(String part, VideoModel model);

    //更新count
    @Update("update ${part} set count=count+1 where id = #{id}")
    void updateVideoCount(String part, int id);

    @Update("UPDATE ${part}_video SET num = #{newNum}, video = '${videoUrl}' WHERE id = #{id} and num = #{oldNum}")
    void updateVideoContent(String part, int id, int oldNum, int newNum, String videoUrl);

// ------------------------------------获取数据--------------------------------------------------------

    @Select("SELECT ${part}.*, e_${part}.num as num1, e_${part}.msg, e_${part}.english " +
            "FROM ${part}, e_${part} WHERE e_${part}.num = ${part}.label ORDER BY count DESC LIMIT #{limit}")
    @Results(id = "VideoMap", value = {
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "label.num", column = "num1"),
            @Result(property = "label.msg", column = "msg"),
            @Result(property = "label.english", column = "english")
    })
    List<VideoModel> getVideoByCount(String part, int limit);

    @Select("SELECT ${part}.*, e_${part}.num as num1, e_${part}.msg, e_${part}.english " +
            "FROM ${part}, e_${part} WHERE e_${part}.num = ${part}.label ORDER BY ${part}.create_time DESC LIMIT #{limit}")
    @ResultMap(value = "VideoMap")
    List<VideoModel> getVideoByTime(String part, int limit);

    @Select("SELECT ${part}.*, e_${part}.num as num1, e_${part}.msg, e_${part}.english " +
            "FROM ${part}, e_${part} WHERE label = #{label} and e_${part}.num = ${part}.label and id > #{num} ORDER BY id LIMIT #{limit}")
    @ResultMap(value = "VideoMap")
    List<VideoModel> getVideoByLabel(String part, int num, int limit, int label);

    @Select("SELECT ${part}.*, e_${part}.num as num1, e_${part}.msg, e_${part}.english " +
            "FROM ${part}, e_${part} WHERE e_${part}.num = ${part}.label and title LIKE '%${key}%' and id > #{num} ORDER BY id LIMIT #{limit}")
    @ResultMap(value = "VideoMap")
    List<VideoModel> getVideoByTitle(String part, String key, int num, int limit);

    @Select("SELECT ${part}.*, e_${part}.num as num1, e_${part}.msg, e_${part}.english  " +
            "FROM ${part}, e_${part} WHERE e_${part}.num = ${part}.label and title LIKE '%${key}%' ORDER BY id")
    @ResultMap(value = "VideoMap")
    List<VideoModel> getVideoByKey(String part, String key);

    @Select("SELECT ${part}.*, e_${part}.num as num1, e_${part}.msg, e_${part}.english " +
            "FROM ${part}, e_${part} WHERE e_${part}.num = ${part}.label and id > #{num} ORDER BY id LIMIT #{limit}")
    @ResultMap(value = "VideoMap")
    List<VideoModel> getVideoById(String part, int num, int limit);

    @Select("SELECT id, label, count, e_${part}.* FROM ${part}, e_${part} where e_${part}.num = ${part}.label")
    @ResultMap(value = "VideoMap")
    List<VideoModel> getAllVideoCount(String part);

    @Select("SELECT ${part}.*, e_${part}.num as num1, e_${part}.msg, e_${part}.english " +
            "FROM ${part}, e_${part} WHERE id = #{id} and e_${part}.num = ${part}.label")
    @ResultMap(value = "VideoMap")
    VideoModel getVideo(@Param("part") String part, @Param("id") int id);

    @Select("SELECT * FROM ${part}_video WHERE id = #{id} ORDER BY num")
    @Results({
            @Result(property = "videoUrl", column = "video")
    })
    List<VideoContentModel> getVideoContent(String part, int id);

    @Select("SELECT * FROM ${part}_video WHERE id = #{id} and num = #{num}")
    @Results({
            @Result(property = "videoUrl", column = "video")
    })
    VideoContentModel getVideoContentByNum(String part, int id, int num);

    @Select("select count(1) from ${part}")
    int getVideoCount(String part);
}
