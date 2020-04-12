package com.zty.hqx.dao;

import com.zty.hqx.model.ClassifyModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClassifyDao {
    @Select("SELECT num, msg, english FROM e_${sub} where num = #{num}")
    ClassifyModel getClassifyByNum(int num, String sub);

    @Select("SELECT num, msg, english FROM e_${part}")
    List<ClassifyModel> getClassify(String part);
}
