package com.viki.web;

/**
 * Created by Viki on 2017/5/29.
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主程序开始
 */
@SpringBootApplication(scanBasePackages = {"com.viki"})
@EnableAutoConfiguration
@MapperScan("com.viki.mapper")
public class WebBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(WebBootstrap.class, args);
    }
}