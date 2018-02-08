package com.viki.crawler.crawl;

import com.viki.crawler.article.ArticleDTO;
import com.viki.crawler.mapper.ArticleMapper;
import com.viki.crawler.mapper.CrawlConfigMapper;
import com.viki.crawler.utils.JsoupUtil;
import com.viki.crawler.utils.SimpleRegExpGen;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Viki on 2017/6/14.
 */
@Component
public class Worker {

    @Autowired
    CrawlConfigMapper crawlConfigMapper;

    @Autowired
    ArticleMapper articleMapper;

    int max_paged = 3000;

    HashMap<String,Document> crawledPages = new HashMap<String,Document>(1000);

    HashMap<String,Document> crawlingPages = new HashMap<String,Document>(1000);

    HashMap<String,Document> unCrawledPages = new HashMap<String,Document>(1000);

    @Scheduled(fixedDelay = 3600000)
    public void run(){
        List<HashMap<String,Object>> rst = crawlConfigMapper.getList();
        Document doc;
        Elements eles;
        for(HashMap<String, Object> map : rst){
            try {
                crawledPages.clear();
                crawlingPages.clear();
                unCrawledPages.clear();
                doc = JsoupUtil.getByConn().url(map.get("entranceUrl").toString()).get();
                crawledPages.put(map.get("entranceUrl").toString(), doc);
                eles = doc.select("a[href]");
                String urlCrawled;
                for(Element ele : eles) {
                    try{
                        urlCrawled = normonizeUrl(ele.attr("href"), map.get("entranceUrl").toString());
                        if (StringUtils.isBlank(urlCrawled)) {
                            continue;
                        }
                        if(!unCrawledPages.containsKey(urlCrawled)){
                            unCrawledPages.put(urlCrawled, null);
                        }
                    }catch (Exception e){e.printStackTrace();}
                }

                int depth = 2;
                int maxDepth = Integer.parseInt(map.get("crawlDepth").toString());
                while(depth++ <= maxDepth){
                    crawlingPages.putAll(unCrawledPages);
                    unCrawledPages.clear();
                    for(String key : crawlingPages.keySet()){
                        doc = JsoupUtil.getByConn().url(key).get();
                        crawledPages.put(doc.location(), doc);
                        eles = doc.select("a[href]");
                        for(Element ele : eles) {
                            if(unCrawledPages.size() > max_paged){
                                break;
                            }
                            try{
                                urlCrawled = normonizeUrl(ele.attr("href"), key);
                                if (StringUtils.isBlank(urlCrawled)) {
                                    continue;
                                }
                                if(!unCrawledPages.containsKey(urlCrawled)){
                                    unCrawledPages.put(urlCrawled, null);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        if(unCrawledPages.size() > max_paged){
                            break;
                        }
                    }
                    crawlingPages.clear();
                }
                unCrawledPages.clear();
                crawlingPages.clear();

                SimpleDateFormat simpleDateFormat = null;
                if(map.get("postdate_path") != null && StringUtils.isNotEmpty(map.get("postdate_path").toString())){
                    simpleDateFormat = new SimpleDateFormat(map.get("postdate_format").toString());
                }
                Pattern pattern = Pattern.compile(map.get("url_reg").toString());
                Matcher matcher;
                ArticleDTO articleDTO = new ArticleDTO();
                ArticleDTO articleDTOCp;
                for(String key : crawledPages.keySet()){
                    try{
                        Document docTmp = Jsoup.parse(new String(new String(crawledPages.get(key).html().getBytes(crawledPages.get(key).charset()), map.get("encoding").toString()).getBytes(), "UTF8"));
                        matcher = pattern.matcher(key);
                        if(matcher.find()){
                            articleDTOCp = articleDTO.clone();
                            articleDTOCp.setUrl(key);
                            articleDTOCp.setContent(docTmp.select(map.get("content_path").toString()).html());
                            try{
                                if(map.get("postdate_path") != null && StringUtils.isNotEmpty(map.get("postdate_path").toString())){
                                    articleDTOCp.setPost_date(simpleDateFormat.parse(docTmp.select(map.get("postdate_path").toString()).text()));
                                }
                            }catch (Exception e){}
                            String title = docTmp.select(map.get("title_path").toString()).text();
                            if (StringUtils.isBlank(title)){
                                title = docTmp.title();
                            }
                            articleDTOCp.setTitle(title);
                            articleMapper.insertArticle(articleDTOCp);
                        }else{
                            System.out.println(key);
                        }
                    }catch (Exception e){e.printStackTrace();}
                }

                crawlConfigMapper.update(map);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String normonizeUrl(String url,String sourceUrl){
        if(url==null || url.length() == 0){
            return null;
        }

        if(url.startsWith("javascript")){
            return null;
        }
        if(!url.startsWith("http")  ){
            String hostUrl = getHostByUrl(sourceUrl);
            hostUrl = hostUrl.endsWith("/")? hostUrl.substring(0, hostUrl.length() - 1): hostUrl;
            url = url.startsWith("/") ? url : "/"+url;
            if(!url.contains(hostUrl)){
                url = hostUrl.concat(url);
            }else{
                return null;
            }
        }
        if(!url.contains(getHostByUrl(sourceUrl))){
            return null;
        }
        return url.split("#")[0];
    }

    public String getHostByUrl(String url){
        return getHostByUrl(url,true);
    }

    public String getHostByUrl(String url,boolean containProtocol){
        String protocol = "";
        // 先补上http协议头
        if(!url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://" + url;
            protocol = "http://";
        }else if(url.startsWith("http://")){
            protocol = "http://";
        }else if(url.startsWith("https://")){
            protocol = "https://";
        }else{
            return null;
        }
        return containProtocol ? protocol + url.replace(protocol, "").split("/")[0] : url.replace(protocol, "").split("/")[0];
    }

    public String getRegExpFromUrl(String sourceUrl){
        String hostName = getHostByUrl(sourceUrl, true);
        sourceUrl = sourceUrl.replace(hostName, "");//.replace(appendix, "");
        sourceUrl = sourceUrl.contains("?")?sourceUrl.substring(0,sourceUrl.indexOf("?")):sourceUrl;
        sourceUrl = SimpleRegExpGen.genReg(sourceUrl);
        return hostName+sourceUrl;
    }
}
