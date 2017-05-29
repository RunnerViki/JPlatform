package com.viki.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Created by Viki on 2017/5/29.
 */
@Controller
public class IndexAction {

//    @RequestMapping(value = "/test")
//    public  ModelAndView hello(ModelMap modelMap) {
//        ModelAndView mav = new ModelAndView("index");
//        mav.addObject("name", "test");
//        return mav;
//    }

    @RequestMapping("/test")
    public String home() {
        System.out.println("-=-----------");
//        model.put("name", "HowToDoInJava Reader !!");
        return "index";
    }
}
