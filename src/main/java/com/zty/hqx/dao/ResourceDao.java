package com.zty.hqx.dao;

import com.zty.hqx.model.ResourceModel;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ResourceDao {
    @Delete("DELETE FROM resource_pic WHERE url = '${pic}'")
    void deletePic(String pic) throws Exception;

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO resource_pic (md5, url, create_time) VALUES ('${md5}' , '${url}', NOW())")
    void insertPic(ResourceModel model);

    @Select("SELECT id, md5, url FROM resource_pic WHERE md5 = '${md5}'")
    ResourceModel getPic(String md5);

    @Delete("DELETE FROM resource_video WHERE url = '${url}'")
    void deleteVideo(String url) throws Exception;

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO resource_video (md5, url, create_time) VALUES ('${md5}' , '${url}', NOW())")
    void insertVideo(ResourceModel model);

    @Select("select id, md5, url from resource_video where md5='${md5}'")
    ResourceModel getVideo(String md5);

    @Delete("DELETE FROM resource_book WHERE url = '${url}'")
    void deleteBook(String url) throws Exception;

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO resource_book (md5, url, create_time) VALUES ('${md5}' , '${url}', NOW())")
    void insertBook(ResourceModel model);

    @Select("select id, md5, url from resource_book where md5='${md5}'")
    ResourceModel getBook(String md5);
}
