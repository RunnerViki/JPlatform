package com.viki.web;

/**
 * Created by Viki on 2017/5/29.
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 主程序开始
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.viki"})
public class WebBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(WebBootstrap.class, args);
    }
}