package com.zty.hqx.service;

import com.zty.hqx.dao.ArticleDao;
import com.zty.hqx.model.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleWriteSevice {

    @Autowired
    ArticleDao articleDao;

    /**
     * 保存文章至数据库
     * @param part     模块名
     * @param article  文章实例
     * @return
     */
    public boolean saveArticle(String part, Article article) {

        boolean result = articleDao.saveArticle(part, article);

        return result;
    }
}
