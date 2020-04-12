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
    void updateVideoContentNum(String part, int id, int oldNum, int newNum, String videoUrl);

// ------------------------------------获取数据--------------------------------------------------------

    @Select("SELECT * FROM ${part} ORDER BY count DESC LIMIT #{limit}")
    @Results({
            @Result(property = "picUrl", column = "pic")
    })
    List<VideoModel> getVideoByCount(String part, int limit);

    @Select("SELECT * FROM ${part} ORDER BY create_time DESC LIMIT #{limit}")
    @Results({
            @Result(property = "picUrl", column = "pic")
    })
    List<VideoModel> getVideoByTime(String part, int limit);

    @Select("SELECT * FROM ${part} WHERE label = #{label} and id > #{num} ORDER BY id LIMIT #{limit}")
    @Results({
            @Result(property = "picUrl", column = "pic")
    })
    List<VideoModel> getVideoByLabel(String part, int num, int limit, int label);

    @Select("SELECT * FROM ${part} WHERE title LIKE '%${key}%' and id > #{num} ORDER BY id LIMIT #{limit}")
    @Results({
            @Result(property = "picUrl", column = "pic")
    })
    List<VideoModel> getVideoByTitle(String part, String key, int num, int limit);

    @Select("SELECT * FROM ${part} WHERE id > #{num} ORDER BY id LIMIT #{limit}")
    @Results({
            @Result(property = "picUrl", column = "pic")
    })
    List<VideoModel> getVideoById(String part, int num, int limit);

    @Select("SELECT id, label, count FROM ${part}")
    @Results({
            @Result(property = "picUrl", column = "pic")
    })
    List<VideoModel> getAllVideoCount(String part);

    @Select("SELECT * FROM ${part} WHERE id = #{id}")
    @Results({
            @Result(property = "picUrl", column = "pic")
    })
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
    VideoContentModel getVideoContent2(String part, int id, int num);

    @Select("select count(1) from ${part}")
    int getVideoCount(String part);
}
