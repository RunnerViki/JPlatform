package com.viki.crawlConfigNew.crawl;

import com.viki.crawlConfig.crawl.WebConfigSnifferUtil;
import com.viki.crawlConfigNew.bean.ConcurrentHashMapWithSegment;
import com.viki.crawlConfigNew.bean.Configuration;
import com.viki.crawlConfigNew.bean.SiteHier;
import com.viki.crawlConfigNew.bean.WebsiteConfig;
import com.viki.crawlConfigNew.utils.ContentSniffer;
import com.viki.crawlConfigNew.utils.TitleSniffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Viki on 2017/6/4.
 */
@Component
public class CrawlWorker implements Runnable{

    @Override
    @Scheduled(fixedDelay = 864000)
    public void run() {
        Thread.currentThread().setName("CrawlWorker");
        boolean  isContinue = true;
        SiteHier siteHier;
        while(isContinue){
            scanQueue();
            try {
                siteHier = Configuration.siteHierBlockingQueue.poll(3, TimeUnit.SECONDS);
                if (siteHier == null) {
                    TimeUnit.SECONDS.sleep(5);
                    continue;
                }
                loopLeafHier(siteHier);
                System.out.println("-----------"); //TODO 回调生成概念归纳
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scanQueue(){
        Set<String> keySet = ConcurrentHashMapWithSegment.keySet();
        SiteHier root;
        for(String key : keySet){
            synchronized (Configuration.siteHierBlockingQueue){
                if((root = ConcurrentHashMapWithSegment.get(key)).getUrlMap().size() > Configuration.parseContentThroshold && !Configuration.siteHierBlockingQueue.contains(root)){
                    Configuration.siteHierBlockingQueue.offer(root);
                }
            }
        }
    }

    private void loopLeafHier(SiteHier siteHier){
        if(siteHier.getSubHier().size() == 0 && siteHier.getWebsiteConfig() == null){
            crawlLeafHier(siteHier);
        }
        for(SiteHier hier : siteHier.getSubHier()){
            loopLeafHier(hier);
        }
    }

    TitleSniffer titleSniffer = new TitleSniffer(null);
    ContentSniffer contentSniffer = new ContentSniffer();

    private void crawlLeafHier(SiteHier siteHier){
        try{
            Document doc = siteHier.getDocument();
            if(siteHier.getDocument() == null){
                doc = Jsoup.connect(siteHier.toString()).get();

            }
            WebsiteConfig websiteConfig = siteHier.getWebsiteConfig();
            if(websiteConfig == null){
                websiteConfig = new WebsiteConfig();
                siteHier.setWebsiteConfig(websiteConfig);
            }
            /*
            * 标题
            * */
            String webpageTitle;
            webpageTitle = doc.title() == null ? "": doc.title();
            webpageTitle = webpageTitle.replaceAll("\\s+", "").replaceAll("\\_|\\-|\\\r", "");
            String cssSelectorTemp = "";
            Double cssSelectorDegree = 0.0D;
            titleSniffer.erxtractTitleXpathBySimiliarDegree(doc.select("body").first(), webpageTitle, cssSelectorTemp, cssSelectorDegree);
            websiteConfig.setTitleXpath(cssSelectorTemp);

            /*
            * 正文
            * */
            websiteConfig.setContentXpath(contentSniffer.extractContentXpath(doc));

            websiteConfig.setUrlPattern(WebConfigSnifferUtil.getRegExpFromUrl(siteHier.toString()));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
