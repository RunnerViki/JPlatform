package com.viki.crawlConfigNew.crawl;

import com.alibaba.fastjson.JSONObject;
import com.viki.crawlConfig.crawl.WebConfigSnifferUtil;
import com.viki.crawlConfigNew.bean.ConcurrentHashMapWithSegment;
import com.viki.crawlConfigNew.bean.Configuration;
import com.viki.crawlConfigNew.bean.SiteHier;
import com.viki.crawlConfigNew.bean.WebsiteConfig;
import com.viki.crawlConfigNew.utils.ContentSniffer;
import com.viki.crawlConfigNew.utils.PathCountEntry;
import com.viki.crawlConfigNew.utils.PostdateSniffer;
import com.viki.crawlConfigNew.utils.TitleSniffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Viki on 2017/6/4.
 */
//@Component
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
                HashMap<SiteHier, WebsiteConfig> hierConfig = new HashMap<>();
                postDateNormalnize(siteHier, hierConfig);
                HashMap<WebsiteConfig, ArrayList<SiteHier>> configClusters = new HashMap<>();
                for(SiteHier siteHier1 : hierConfig.keySet()){
                    if(!configClusters.containsKey(siteHier1.getWebsiteConfig())){
                        ArrayList<SiteHier> siteHierArrayList = new ArrayList<>();
                        siteHierArrayList.add(siteHier1);
                        configClusters.put(siteHier1.getWebsiteConfig(), siteHierArrayList);
                    }
                    ArrayList<SiteHier> siteHierArrayList = configClusters.get(siteHier1.getWebsiteConfig());
                    Set<SiteHier> siblings = siteHier1.getParentHier().getSubHier();
                    siblings.remove(siteHier1);
                    for(SiteHier siteHier2 : siblings){
                        if(siteHier2.getWebsiteConfig() == null){
                            //  ?? TODO
                        }
                        if(siteHier1.getWebsiteConfig().equalsWithCrawlConfig(siteHier2.getWebsiteConfig())){
                            siteHierArrayList.add(siteHier2);
                        }else{
                            if(!configClusters.containsKey(siteHier2.getWebsiteConfig())){
                                ArrayList<SiteHier> siteHierArrayList2 = new ArrayList<>();
                                siteHierArrayList2.add(siteHier2);
                                configClusters.put(siteHier2.getWebsiteConfig(), siteHierArrayList2);
                            }else{
                                ArrayList<SiteHier> siteHierArrayList3 = configClusters.get(siteHier1.getWebsiteConfig());
                                siteHierArrayList3.add(siteHier2);
                            }
                        }
                    }
                }
                System.out.println("configClusters\t"+ JSONObject.toJSONString(configClusters));
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

    /*
    * 解析发表日期
    * */
    private void postDateNormalnize(SiteHier siteHier, HashMap<SiteHier,WebsiteConfig> map){
        if(siteHier.getSubHier().size() > 0 && siteHier.getDepthCurrentHier() == siteHier.getDepthInTotal() - 1){
            HashMap<String, Integer> titlePathMap = new HashMap<>();
            HashMap<String, Integer> contentPathMap = new HashMap<>();
            String titlePath;
            String contentPath;
            for(SiteHier subSiteHier1 : siteHier.getSubHier()){
                try{
                    titlePath = subSiteHier1.getWebsiteConfig().getTitleXpath();
                    if(null != titlePath){
                        if(!titlePathMap.containsKey(titlePath)){
                            titlePathMap.put(titlePath, 1);
                        }else{
                            titlePathMap.put(titlePath, titlePathMap.get(titlePath) + 1);
                        }
                    }

                    contentPath = subSiteHier1.getWebsiteConfig().getContentXpath();
                    if(null != contentPath){
                        if(!contentPathMap.containsKey(contentPath)){
                            contentPathMap.put(contentPath, 1);
                        }else{
                            contentPathMap.put(contentPath, contentPathMap.get(contentPath) + 1);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            WebsiteConfig siteHierWebsiteConfig = new WebsiteConfig();
            if(titlePathMap.size() == 1){
                siteHierWebsiteConfig.setTitleXpath(titlePathMap.keySet().iterator().next());
            }else{
                siteHierWebsiteConfig.setTitleXpath(pathBacktesting(titlePathMap, siteHier));
            }
            if(contentPathMap.size() == 1){
                siteHierWebsiteConfig.setContentXpath(contentPathMap.keySet().iterator().next());
            }else{
                siteHierWebsiteConfig.setContentXpath(pathBacktesting(contentPathMap, siteHier));
            }
            siteHier.setWebsiteConfig(siteHierWebsiteConfig);
            PostdateSniffer postdateSniffer = new PostdateSniffer(siteHier, siteHierWebsiteConfig.getTitleXpath(), siteHierWebsiteConfig.getContentXpath());
            postdateSniffer.extractPostDateXpath();
            String postdatePath = pathBacktesting(postdateSniffer.getPostdateXpathSet(), siteHier);
            siteHierWebsiteConfig.setPostdateXpath(postdatePath);
            siteHierWebsiteConfig.setPostdateFormat(postdateSniffer.getPostdateFormat());
            System.out.println("-------------第一阶段--------------" + siteHier.toString() + "\t" + siteHier.getSubHier().size());
            System.out.println(siteHierWebsiteConfig.getTitleXpath());
            System.out.println(siteHierWebsiteConfig.getContentXpath());
            System.out.println(siteHierWebsiteConfig.getPostdateXpath()+"\t" + siteHierWebsiteConfig.getPostdateFormat());
            map.put(siteHier, siteHierWebsiteConfig);
        }else if(siteHier.getSubHier() != null && siteHier.getSubHier().size() > 0){
            for(SiteHier hier : siteHier.getSubHier()){
                postDateNormalnize(hier, map);
            }
        }
    }

    /*
    * 路径回测，适用于标题及正文路径
    * */
    private String pathBacktesting(HashMap<String, Integer> pathMap, SiteHier siteHier){
        if(siteHier.getSubHier() == null || siteHier.getSubHier().size() == 0){
            return null;
        }
        if(pathMap == null || pathMap.size() == 0){
            return null;
        }
        Set<SiteHier> subSiteHier = siteHier.getSubHier();
        Document document;
        TreeSet<PathCountEntry> pathCountSet = new TreeSet<PathCountEntry>(new Comparator<PathCountEntry>() {
            @Override
            public int compare(PathCountEntry o1, PathCountEntry o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        for(String path : pathMap.keySet()){
            for(SiteHier siteHier1 : subSiteHier){
                document = siteHier1.getDocument();
                if(document == null){
                    continue;
                }
                if(document.select(path).size() > 0){

                    /*if(!pathCountSet.contains(path)){
                        pathCountSet.add(new PathCountEntry(path, 1));

                    }else{*/
                    boolean isContain = false;
                    for(PathCountEntry pathCountEntry : pathCountSet){
                        if(pathCountEntry.getKey().equals(path)){
                            pathCountEntry.setValue(pathCountEntry.getValue() + 1);
                            isContain = true;
                            break;
                        }
                    }
                    if(!isContain){
                        pathCountSet.add(new PathCountEntry(path, 1));
                    }
                    /*}*/
                }
            }
        }
        PathCountEntry pathCountEntry = pathCountSet.iterator().next();
        return pathCountEntry.getKey();
    }

    /*
    * 可用爬取配置扫描
    * */
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

    /*
    * 循环叶子节点，然后获取每个节点的爬取配置
    * */
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

    /*
    * 获取每个节点的爬取配置
    * */
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
