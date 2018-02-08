package com.viki.web.action;

import com.viki.web.bean.PostBean;
import com.viki.web.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Viki on 2017/5/29.
 */
@Controller
public class IndexAction {

    @Autowired
    @Qualifier("postMapper")
    PostMapper postMapper;

    @RequestMapping(value = {"/","/index", "/index.html"})
    public String index(ModelMap map) {
        // 加入一个属性，用来在模板中读取
        map.addAttribute("title", "Java自学网");
        map.addAttribute("subjects", new String[]{"源码阅读","技能库", "在线工具", "登录 / 注册", "关于作者"});
        List<PostBean> postBeanList = postMapper.selectList();
        map.addAttribute("postList", postBeanList);
        return "index";
    }
}
