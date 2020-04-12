package com.zty.hqx.dao;

import com.zty.hqx.model.EmailModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface EmailDao {

    @Select("select * from email")
    List<EmailModel> getRecivers();
}
