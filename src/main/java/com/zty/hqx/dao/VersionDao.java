package com.zty.hqx.dao;

import com.zty.hqx.model.Version;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VersionDao {

    @Select("SELECT * FROM version ORDER BY id DESC LIMIT 1")
    public Version getNewVersion() throws Exception;
}
