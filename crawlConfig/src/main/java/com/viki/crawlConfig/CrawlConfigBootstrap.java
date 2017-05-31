package com.viki.crawlConfig;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Viki on 2017/5/26.
 */
@SpringBootApplication
@Configuration
//@ComponentScan({"com.viki.crawlConfig"})
@MapperScan("com.viki.crawlConfig.mapper")
@PropertySources({ @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)})
@EnableScheduling
public class CrawlConfigBootstrap {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CrawlConfigBootstrap.class, args);
    }
}
