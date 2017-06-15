package com.viki.crawlConfigNew.crawl;


import com.viki.crawlConfigNew.bean.ConcurrentHashMapWithSegment;
import com.viki.crawlConfigNew.bean.Configuration;
import com.viki.crawlConfigNew.bean.SiteHier;
import com.viki.crawlConfigNew.mapper.SiteHierMapper;
import com.viki.crawlConfigNew.utils.UrlSniffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by Viki on 2017/6/4.
 */
@Component
public class CrawlExpandArea implements Runnable{

    @Autowired
    SiteHierMapper siteHierMapper;

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
                        Set<String> keys = root.getUrlMap().keySet();
                        for(String key1 : keys){
                            try{
                                SiteHier hier = root.getUrlMap().get(key1);
                                if(hier.isCompletedCrawled){
                                    continue;
                                }
                                urlSniffer = urlSniffer == null ? new UrlSniffer(hier.toString(), siteHierMapper) : urlSniffer;
                                urlSniffer.setEntranceUrl(hier.toString());
                                urlSniffer.getUrls();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        synchronized (Configuration.siteHierBlockingQueue){
                            if((root = ConcurrentHashMapWithSegment.get(key)).getUrlMap().size() > Configuration.parseContentThroshold && !Configuration.siteHierBlockingQueue.contains(root)){
//                                Configuration.siteHierBlockingQueue.offer(root);  TODO 暂时关掉 并且回收到root，
                                System.out.println("回收前:\t"+ConcurrentHashMapWithSegment.keySet().size());
                                root = null;
                                System.gc();
                                System.out.println("回收后:\t"+ConcurrentHashMapWithSegment.keySet().size());
                                System.out.println();
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
