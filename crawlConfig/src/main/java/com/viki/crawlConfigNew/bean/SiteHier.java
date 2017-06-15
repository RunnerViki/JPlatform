package com.viki.crawlConfigNew.bean;

import com.viki.crawlConfigNew.mapper.SiteHierMapper;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Viki on 2017/6/4.
 */
public class SiteHier {

    private Integer id;

    private String hierName;

    private SiteHier parentHier;

    private SiteHier rootHier;

    private int depthCurrentHier = 0;

    private int depthInTotal = 0;

    private Set<SiteHier> subHier = new HashSet<>();

    private HashMap<String, SiteHier> urls;

    private static final Boolean URL_NOT_CRAWLED = false;

    private static final Boolean URL_CRAWLED = true;

    /*
    * 是根节点的时候，表示整个站点都已经爬取完成；是其他节点的时候，只表示当前地址爬取完成
    * */
    public boolean isCompletedCrawled = false;

    private WebsiteConfig websiteConfig;

    private Document document;

    private String docContent;

    private  SiteHierMapper siteHierMapper;

    public SiteHier(){}

    public SiteHier(String name, SiteHier parentHier, boolean isLeaf, SiteHierMapper siteHierMapper){
        this.hierName = name;
        this.parentHier = parentHier;
        if(parentHier != null){
            parentHier.subHier.add(this);
            this.depthCurrentHier = parentHier.depthCurrentHier + 1;
            this.rootHier = parentHier.rootHier;
            this.rootHier.depthInTotal = this.depthCurrentHier > this.rootHier.depthInTotal ? this.depthCurrentHier : this.rootHier.depthInTotal;
        }else{
            this.rootHier = this;
            this.depthInTotal = 1;
            ConcurrentHashMapWithSegment.put(this.hierName, this);
            this.depthCurrentHier = 1;
            urls = new HashMap<>();
        }
        this.rootHier.urls.put(this.toString(), this);
        this.siteHierMapper = siteHierMapper;
        try{
            siteHierMapper.insert(this);
            System.out.println("");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getDepthInTotal(){
        return this.rootHier.depthInTotal;
    }

    public int getDepthCurrentHier(){
        return this.depthCurrentHier;
    }

    public boolean destory(boolean force) throws Exception {
        if(this.rootHier.equals(this)){
            /*if(force){
                this.subHier = null;
            }*/
            Iterator<String> keyIterator = urls.keySet().iterator();
            String key;
            while(keyIterator.hasNext()){
                key = keyIterator.next();
                if(urls.get(key).isCompletedCrawled){
                    urls.remove(key);
                }
            }
            isCompletedCrawled = URL_CRAWLED;
            System.gc();
            return true;
        }else{
            throw new Exception("Permission denied: Root elements could be destoried only!");
        }
    }

    public Set<SiteHier> getSubHier(){
        return this.subHier;
    }

    public boolean isRoot(){
        return this.equals(this.rootHier);
    }

    public HashMap<String, SiteHier> getUrlMap(){
        return this.rootHier.urls;
    }

//    public boolean containsUrlInRoot(String url){
//        return this.rootHier.urls.containsKey(url);
//    }

    public boolean containSub(String name){
        if(subHier == null){
            return false;
        }
        for(SiteHier h : subHier){
            if(h.hierName.equals(name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if(parentHier != null){
            return parentHier.toString() + "/" + this.hierName;
        }else{
            return this.hierName;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SiteHier hier = (SiteHier) o;

        if (hierName != null ? !hierName.equals(hier.hierName) : hier.hierName != null) return false;
        return !(parentHier != null ? !parentHier.equals(hier.parentHier) : hier.parentHier != null);
    }

    public WebsiteConfig getWebsiteConfig() {
        return websiteConfig;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
        siteHierMapper.updateDocument(this);
        this.document = null; // TODO 后面删
    }

    public String getHierName(){return this.hierName;}

    public void setWebsiteConfig(WebsiteConfig websiteConfig) {
        this.websiteConfig = websiteConfig;
    }

    public SiteHier getParentHier(){
        return this.parentHier;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SiteHier getRootHier(){
        return this.rootHier;
    }

    public String getDocContent() {
        return this.document == null ? "" : this.document.html();
    }

    public String getFullName(){
        return this.toString();
    }

    public void setParentHier(SiteHier parentHier){
        this.parentHier = parentHier;
    }

    public void setRootHier(SiteHier rootHier){
        this.rootHier = rootHier;
    }
}