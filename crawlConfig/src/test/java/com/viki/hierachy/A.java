package com.viki.hierachy;

import com.viki.crawlConfigNew.bean.SiteHier;

import java.util.HashMap;

/**
 * Created by Viki on 2017/6/4.
 */
public class A {

    static HashMap<String, SiteHier> hierMap = new HashMap<>();

    public static void main(String[] args) {/*
        String sourceUrl = "http://www.zhangxinxu.com/wordpress/2016/05/";
        UrlSniffer us = new UrlSniffer(sourceUrl,20);
        String hostUrl;
        SiteHier root;
        SiteHier tmpParentHier;
        Set<String> urls = us.getUrls();
        for (String s : urls) {
            hostUrl = WebConfigSnifferUtil.getHostByUrl(s, true);
            if(!hierMap.containsKey(hostUrl)){
//                root = new SiteHier("ROOT:"+hostUrl, null);
//                hierMap.put(hostUrl, root);
            }else{
                root = hierMap.get(hostUrl);
            }
            tmpParentHier = root;
            String hierachys[] = s.replace(hostUrl+"/", "").split("/");
            for(String hierName : hierachys){
                if(!tmpParentHier.containSub(hierName)){
//                    SiteHier hier = new SiteHier(hierName, tmpParentHier);
//                    tmpParentHier = hier;
                }
            }
        }*/
    }

}
