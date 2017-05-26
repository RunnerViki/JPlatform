package com.viki.crawler;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Viki on 2017/5/26.
 * Function: TODO
 */
@RestController
@RequestMapping("/")
public class HelloController {
    @RequestMapping("/hello")
    public String index() {
        return "Hello World";
    }
}