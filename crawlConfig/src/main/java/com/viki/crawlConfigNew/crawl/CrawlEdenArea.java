package com.viki.crawlConfigNew.crawl;


import com.alibaba.fastjson.JSONObject;
import com.viki.crawlConfig.crawl.WebConfigSnifferUtil;
import com.viki.crawlConfig.utils.ConnectionFactory;
import com.viki.crawlConfigNew.bean.ConcurrentHashMapWithSegment;
import com.viki.crawlConfigNew.bean.SiteHier;
import com.viki.crawlConfigNew.utils.UrlSniffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Viki on 2017/6/4.
 */
@Component
public class CrawlEdenArea implements Runnable{


    @Override
    @Scheduled(fixedDelay = 86400000)
    public void run() {
        Thread.currentThread().setName("CrawlEdenArea");
        Document doc = null;
        try {
            doc = ConnectionFactory.getConnection()
                    .url("http://geek.csdn.net/service/news/get_news_list?username=&from=-&size=20&type=HackCount&_="+System.currentTimeMillis())
                    .ignoreContentType(true)
                    .get();
            JSONObject docjson = JSONObject.parseObject(doc.text());
            doc = Jsoup.parse(docjson.getString("html"));
            Elements eles = doc.select("a[href]");
            for(Element href : eles){
                UrlSniffer us = new UrlSniffer(href.attr("href"), 200);
                us.getUrls();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
