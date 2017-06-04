package com.viki.crawlConfigNew;

import com.alibaba.fastjson.JSONObject;
import com.viki.crawlConfig.crawl.WebConfigJobBalancer;
import com.viki.crawlConfigNew.bean.ConcurrentHashMapWithSegment;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by Viki on 2017/5/26.
 */
@SpringBootApplication
@Configuration
@ComponentScan({"com.viki.crawlConfigNew"})
@MapperScan("com.viki.crawlConfig.mapper")
@PropertySources({ @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)})
@EnableScheduling
public class CrawlConfigBootstrap {
    public static void main(String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                System.out.println("停了。。。");
                try {
                    Files.write(new File("G:\\recordNew.txt").toPath(), JSONObject.toJSONString(ConcurrentHashMapWithSegment.segmentList).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        SpringApplication.run(CrawlConfigBootstrap.class, args);

    }
}
