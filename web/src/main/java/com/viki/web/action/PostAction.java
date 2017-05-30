package com.viki.web.action;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.websocket.server.PathParam;

/**
 * Created by Viki on 2017/5/29.
 */
@Controller
public class PostAction {

    @RequestMapping(value = "/post/{post_id}.html", method = RequestMethod.GET)
    public String getPost(@PathVariable("post_id") String postId, ModelMap map){
        System.out.println(postId);
        map.put("title", "Java自学网");
        map.put("post_title", "html 控件/元素 透明与背景透明");
        return "post";
    }
}
