package com.zty.hqx;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zty.hqx.classify.EModel;
import com.zty.hqx.classify.EStudyPart;
import com.zty.hqx.controller.CountController;
import com.zty.hqx.dao.*;
import com.zty.hqx.model.*;
import com.zty.hqx.service.BaseService;
import com.zty.hqx.service.HistoryService;
import com.zty.hqx.service.StudyService;
import com.zty.hqx.util.*;
import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.PoolException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.beans.Expression;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class HqxApplicationTests {
    @Autowired
    HistoryService historyService;
    @Autowired
    SearchDao searchDao;
    @Autowired
    CollectDao collectDao;
    @Autowired
    UserDao userDao;
    @Autowired
    StudyVideoDao videoDao;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    BaseDao baseDao;
    @Autowired
    StudyExamDao examDao;
    @Autowired
    StudyBookDao bookDao;
    @Autowired
    CountDao countDao;
    @Autowired
    StudyService studyService;
    @Autowired
    BaseService baseService;

    @Autowired
    CountController countController;
    @Test
    void test(){
        countController.getCount("study", "teleplay", "year");
    }
}

