package com.zty.hqx.dao;

import com.zty.hqx.model.ClassifyModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClassifyDao {
    @Select("SELECT num, msg, english FROM e_${part} where num = #{num}")
    ClassifyModel getClassifyByNum(int num, String part);

    @Select("SELECT num, msg, english FROM e_${part}")
    List<ClassifyModel> getClassify(String part);

    @Insert("INSERT INTO e_${part} (msg, english) VALUES ('${model.msg}', '${model.english}')")
    @Options(useGeneratedKeys = true, keyColumn = "num", keyProperty = "model.num")
    void insertClassify(String part, ClassifyModel model);

    @Delete("delete from e_${part} where num = #{num}")
    boolean deleteClassify(String part, int num) throws Exception;
}
