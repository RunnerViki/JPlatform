package com.viki;

import com.viki.crawlConfig.CrawlConfigBootstrap;
import com.viki.crawlConfig.bean.WebsiteConfig;
import com.viki.crawlConfig.crawl.*;
import com.viki.crawlConfig.mapper.WebsiteConfigMapper;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by Viki on 2017/5/31.
 * Function: TODO
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CrawlConfigBootstrap.class)
//@WebAppConfiguration

public class TitleSnifferTest {

    @Autowired
    WebsiteConfigMapper websiteConfigMapper;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void t1(){
        List<WebsiteConfig> x = websiteConfigMapper.getList(new HashMap<String, Object>());
        System.out.println(x.size());
        for(WebsiteConfig websiteConfig : x){
            try{
                if("NOTCHECK".equals(websiteConfig.getChecked_status())){
                    Set<String> subset = new HashSet<>();
                    for(String url : websiteConfig.getSampleUrl().split(",")){
                        subset.add(url);
                    }
                    DocumentsGen documentsGen = new DocumentsGen(subset);
                    Set<Document> documents = documentsGen.gen();
                    if(documents.size() == 0){
                        continue;
                    }
                    TitleSniffer titleSniffer = new TitleSniffer(documents);
                    String titleXpath = titleSniffer.extractTitleXPath();
                    logger.info("标题xpath:\t"+titleXpath);
                    for(Document document : documents){
                        System.out.println(document.location()+"\t的标题是:\t"+document.select(titleXpath));
                        System.out.println("-----");
                    }

                    ContentSniffer contentSniffer = new ContentSniffer(documents);
                    String contentXpath = contentSniffer.extractContentXpath();
                    logger.info("正文xpath:\t"+contentXpath);
                    for(Document document : documents){
                        System.out.println(document.location()+"\t的正文是:\t"+document.select(contentXpath).text());
                        System.out.println("-----");
                    }

                    PostdateSniffer postdateSniffer = new PostdateSniffer(documents, titleXpath, contentXpath);
                    String postdateXpath = postdateSniffer.extractPostDate();
                    websiteConfig.setPostdateXpath(postdateXpath);
                    websiteConfig.setPostdateFormat(postdateSniffer.getPostdateFormat());
                    logger.info("发表日期xpath:\t"+postdateXpath + "\t格式" + websiteConfig.getPostdateFormat());
                    for(Document document : documents){
                        System.out.println(document.location()+"\t的发表日期是:\t"+document.select(postdateXpath).text()+"\t格式是:"+websiteConfig.getPostdateFormat());
                        System.out.println("-----");
                    }

                    try {
                        websiteConfig.setChecked_status("RIGHT");
                        websiteConfigMapper.update(websiteConfig);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }catch (Exception e){}
        }
    }
}
