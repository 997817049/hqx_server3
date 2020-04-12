package com.zty.hqx.service;

import com.zty.hqx.dao.ClassifyDao;
import com.zty.hqx.model.ClassifyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassifyService {
    @Autowired
    ClassifyDao classifyDao;

    public List<ClassifyModel> getClassify(String part){
        return classifyDao.getClassify(part);
    }
}
