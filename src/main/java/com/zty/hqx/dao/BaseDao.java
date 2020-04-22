package com.zty.hqx.dao;

import com.zty.hqx.model.BaseModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BaseDao {
// <---------------------------------更新基地数据------------------------------------------------>

    @Update("UPDATE base SET title = '${title}',province = '${province}', city = '${city}', pic = '${picUrl}',html = '${htmlUrl}' WHERE id = #{id}")
    void updateBase(BaseModel model);

    @Update("update base set count=count+1 where id = #{id}")
    void updateBaseCount(int id);

// <---------------------------------------增 删 基地数据------------------------------------------------>

    /**
     * Options: 插入数据时 不存在id，插入后，会将生成的id重新封装回来
     */
    @Insert("INSERT INTO base (title, pic, html, province, city, create_time) VALUES ('${title}', '${picUrl}', '${htmlUrl}', '${province}', '${city}', now())")
    void insertBase(BaseModel baseModel);

    @Delete("DELETE FROM base WHERE id = #{id}")
    void deleteBase(int id);

// <---------------------------------------根据要求获取基地信息------------------------------------------->

    @Select("SELECT id, title, pic, html, province, city, count FROM base WHERE title = '${title}'")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    BaseModel isTitleAvailable(String title);

    //通过景点名称查询景点
    @Select("SELECT * FROM base WHERE id > #{num} and title LIKE '%${key}%' \n" +
            "UNION \n" +
            "SELECT * FROM base WHERE id > #{num} and province LIKE '%${key}%'\n" +
            "UNION \n" +
            "SELECT * FROM base WHERE id > #{num} and city LIKE '%${key}%'\n" +
            "ORDER BY id LIMIT #{limit}")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    List<BaseModel> getBaseByKey(String key, int num, int limit);

    //通过景点名称查询景点
    @Select("SELECT * FROM base WHERE id > #{num} and province LIKE '%${province}%' \n" +
            "UNION \n" +
            "SELECT * FROM base WHERE id > #{num} and city LIKE '%${city}%' \n" +
            "ORDER BY id LIMIT #{limit}")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    List<BaseModel> getBaseByAddress(String province, String city, int num, int limit);

    @Select("SELECT * FROM base where id > #{num} ORDER BY count desc LIMIT #{limit};")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    List<BaseModel> getHotBase(int num, int limit);

    @Select("SELECT id, title, pic, html, province, city, count " +
            "FROM base WHERE id > #{num} ORDER BY id LIMIT #{limit};")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    List<BaseModel> getBase(int num, int limit);

    @Select("SELECT id, count FROM base")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    List<BaseModel> getAllBaseCount();

    @Select("SELECT id, title, pic, html, province, city, count FROM base WHERE id = #{id}")
    @Results({
            @Result(property = "picUrl", column = "pic"),
            @Result(property = "htmlUrl", column = "html")
    })
    BaseModel getBaseById(int id);

    @Select("select count(1) from base")
    int getBaseCount();
}
