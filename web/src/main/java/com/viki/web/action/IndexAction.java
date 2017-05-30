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

    @RequestMapping(value = {"/","/index", "/index.html"})
    public String index(ModelMap map) {
        // 加入一个属性，用来在模板中读取
        map.addAttribute("title", "Java自学网");
        map.addAttribute("subjects", new String[]{"源码阅读","技能库", "在线工具", "登录 / 注册", "关于作者"});
        return "index";
    }
}
