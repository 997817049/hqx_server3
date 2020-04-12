package com.zty.hqx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@MapperScan(value = "com.zty.hqx.dao")
@SpringBootApplication
@EnableCaching
public class HqxApplication {

    public static void main(String[] args) {
        SpringApplication.run(HqxApplication.class, args);
    }

}
