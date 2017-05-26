package com.viki.crawlConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

/**
 * Created by Viki on 2017/5/26.
 */
@SpringBootApplication
@ComponentScan({"com.viki.crawlConfig"})
@EnableScheduling
public class CrawlConfigBootstrap {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(CrawlConfigBootstrap.class, args);
    }
}
