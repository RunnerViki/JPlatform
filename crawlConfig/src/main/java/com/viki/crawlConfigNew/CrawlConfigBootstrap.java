package com.viki.crawlConfigNew;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Viki on 2017/5/26.
 */
@SpringBootApplication
@Configuration
@ComponentScan({"com.viki.crawlConfigNew"})
@MapperScan("com.viki.crawlConfigNew.mapper")
@PropertySources({ @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)})
@EnableScheduling
public class CrawlConfigBootstrap {
    public static void main(String[] args) throws Exception {
        /*Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                System.out.println("停了。。。");
                try {
                    Files.write(new File("E:\\recordNew.txt").toPath(), JSONObject.toJSONString(ConcurrentHashMapWithSegment.segmentList).getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
        SpringApplication.run(CrawlConfigBootstrap.class, args);

    }
}
