package com.viki.crawler;

import com.viki.crawler.utils.CustomPropertyPlaceholderConfigurer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

/**
 * Created by Viki on 2017/5/26.
 * Function: TODO
 */
@SpringBootApplication
@ComponentScan({"com.viki.crawler"})
@MapperScan("com.viki.crawler.mapper")
@EnableScheduling
public class CrawlerBootstrap {

        public static void main(String[] args) throws Exception {
//            CustomPropertyPlaceholderConfigurer.load(new FileInputStream(new File(ClassLoader.getSystemResource("application.properties").getFile())), Charset.defaultCharset());
            SpringApplication.run(CrawlerBootstrap.class, args);
        }
}
