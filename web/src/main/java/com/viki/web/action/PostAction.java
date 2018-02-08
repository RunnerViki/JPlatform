package com.viki.web.action;

import com.viki.web.bean.PostBean;
import com.viki.web.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Viki on 2017/5/29.
 */
@Controller
public class PostAction {

@Autowired
@Qualifier("postMapper")
    PostMapper postMapper;

    @RequestMapping(value = "/post/{post_id}.html", method = RequestMethod.GET)
    public String getPost(@PathVariable("post_id") String postId, ModelMap map){
        PostBean postBean = postMapper.select(postId);
        map.put("title", "Java自学网");
        map.put("post_title", postBean.getTitle() == null ? "______________" : postBean.getTitle());
        map.put("content", postBean.getContent());
        return "post";
    }
}
