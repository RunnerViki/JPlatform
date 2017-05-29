package com.viki.web.action;

import org.springframework.stereotype.Controller;
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
    public String getPost(@PathVariable("post_id") String postId){

        return postId;
    }
}
