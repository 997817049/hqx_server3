package com.zty.hqx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *扩展springmvc的功能
 */
@Configuration
public class CorsConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        super.addViewControllers(registry);
        //发送/hqx请求来的指定页面
        registry.addViewController("/hqx").setViewName("login.html");
        registry.addViewController("/base/statistics").setViewName("base/statistics_base.html");
        registry.addViewController("/base/adm").setViewName("base/adm_base.html");
        registry.addViewController("/base/add").setViewName("base/add_base.html");
//        registry.addViewController("/base/adm").setViewName("base/adm_base.html");
//        registry.addViewController("/base/add").setViewName("base/add_base.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true).maxAge(3600);
    }
}
