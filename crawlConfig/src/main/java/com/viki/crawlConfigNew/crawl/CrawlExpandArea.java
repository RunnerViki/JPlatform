package com.viki.crawlConfigNew.crawl;


import com.viki.crawlConfig.crawl.WebConfigSnifferUtil;
import com.viki.crawlConfigNew.bean.ConcurrentHashMapWithSegment;
import com.viki.crawlConfigNew.bean.Configuration;
import com.viki.crawlConfigNew.bean.SiteHier;
import com.viki.crawlConfigNew.utils.UrlSniffer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Viki on 2017/6/4.
 */
@Component
public class CrawlExpandArea implements Runnable{


    @Override
    @Scheduled(fixedDelay = 86400)
    public void run() {
        Thread.currentThread().setName("CrawlExpandArea");
        Set<String> keySet;
        while(true){
            try{
                keySet = ConcurrentHashMapWithSegment.keySet();
                UrlSniffer urlSniffer = null;
                String hostUrl;
                SiteHier root;
                SiteHier tmpParentHier;
                for(String key : keySet){
                    root = ConcurrentHashMapWithSegment.get(key);
                    try{
                        if(!root.isRoot()){
                            continue;
                        }
                        HashMap<String, SiteHier> urlsMap = root.getUrlMap();
                        for(SiteHier hier : urlsMap.values()){
                            try{
                                if(hier.isCompletedCrawled){
                                    continue;
                                }
                                urlSniffer = urlSniffer == null ? new UrlSniffer(hier.toString()) : urlSniffer;
                                urlSniffer.setEntranceUrl(hier.toString());
                                Set<String> urls = urlSniffer.getUrls();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        synchronized (Configuration.siteHierBlockingQueue){
                            if((root = ConcurrentHashMapWithSegment.get(key)).getUrlMap().size() > Configuration.parseContentThroshold && !Configuration.siteHierBlockingQueue.contains(root)){
                                Configuration.siteHierBlockingQueue.offer(root);
                            }
                        }
                        root.destory(true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void loopLeafHier(SiteHier siteHier){
        if(siteHier.getSubHier().size() == 0 && siteHier.getWebsiteConfig() == null){

        }
        for(SiteHier hier : siteHier.getSubHier()){
            loopLeafHier(hier);
        }
    }
}
