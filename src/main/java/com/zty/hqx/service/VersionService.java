package com.zty.hqx.service;

import com.zty.hqx.dao.VersionDao;
import com.zty.hqx.model.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 版本服务
 *
 * @author funyoo
 * @creatDate 2019/12/04 15:18
 */
@Service
public class VersionService {

    @Autowired
    public VersionDao versionDao;

    /**
     * 查询最新版本
     * @return
     * @throws Exception 数据库异常
     */
    public Version checkUpdate() throws Exception {
        Version version = versionDao.getNewVersion();
        return version;
    }
}
