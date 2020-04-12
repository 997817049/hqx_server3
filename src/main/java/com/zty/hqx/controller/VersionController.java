package com.zty.hqx.controller;

import com.zty.hqx.service.VersionService;
import com.zty.hqx.classify.CodeMsg;
import com.zty.hqx.model.Result;
import com.zty.hqx.model.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 版本控制
 *
 * @author funyoo
 * @creatDate 2019/12/04 15:18
 */
@RestController
@RequestMapping("/version")
public class VersionController {

    @Autowired
    public VersionService versionService;

    /**
     * 查询最新版本
     * @return
     */
    @RequestMapping("/checkUpdate")
    public Result<Version> checkUpdate() {
        Version version = null;
        try {
            version = versionService.checkUpdate();
        } catch (Exception e) {
            version = null;
        }
        if (version == null) {
            return Result.error(CodeMsg.NO_DATA_ERROR);
        }
        return Result.success(version);
    }
}
