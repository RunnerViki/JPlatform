package com.viki.crawlConfigNew.utils;

import com.viki.crawlConfig.crawl.WebConfigSnifferUtil;
import com.viki.crawlConfig.utils.ConcurrentEntry;
import com.viki.crawlConfig.utils.ConnectionFactory;
import com.viki.crawlConfigNew.bean.ConcurrentHashMapWithSegment;
import com.viki.crawlConfigNew.bean.SiteHier;
import com.viki.crawlConfigNew.mapper.SiteHierMapper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ���ٻ�ȡ��maxSize��url����
 * @author vikiyang
 *
 */
public class UrlSniffer {

	Logger logger = LoggerFactory.getLogger(UrlSniffer.class);

	private String entranceUrl;

	private Integer maxSize = 300;

	private Connection conn;

	public UrlSniffer(String entranceUrl, SiteHierMapper siteHierMapper) {
		this(entranceUrl, 300, siteHierMapper);
	}

	public void setEntranceUrl(String entranceUrl){
		this.entranceUrl = entranceUrl;
	}

	public UrlSniffer(String entranceUrl, Integer maxSize, SiteHierMapper siteHierMapper) {
		this.entranceUrl = entranceUrl;
		this.maxSize = maxSize;
		this.conn = ConnectionFactory.getConnection().getValue();
		this.siteHierMapper = siteHierMapper;
	}

	public static void main(String[] args){
//		UrlSniffer us = new UrlSniffer("http://kuaixun.stcn.com/2015/0408/12156763.shtml",200);
//		us.test();
		//logger.info(us.getHostByUrl(us.entranceUrl,true));
	}

	SiteHierMapper siteHierMapper;
	
	private SiteHier addSiteHier(String s){
		String hostUrl;
		SiteHier root;
		SiteHier tmpParentHier;
		hostUrl = WebConfigSnifferUtil.getHostByUrl(s, true);
		if(!ConcurrentHashMapWithSegment.containsKey(hostUrl)){
			root = new SiteHier(hostUrl, null, false, siteHierMapper);
			ConcurrentHashMapWithSegment.put(hostUrl, root);
		}else{
			root = ConcurrentHashMapWithSegment.get(hostUrl);
		}
		tmpParentHier = root;
		String hierachys[] = s.equals(hostUrl) ? new String[]{} : s.replace(hostUrl+"/", "").split("/");
		int length = hierachys.length;
		if(length == 0){
			return root;
		}
		for(int l = 0; l < length; l++){
			if(!tmpParentHier.containSub(hierachys[l])){
				SiteHier hier = new SiteHier(hierachys[l], tmpParentHier, (l == length - 1), siteHierMapper);
				tmpParentHier = hier;
			}else{
				for(SiteHier siteHier : tmpParentHier.getSubHier()){
					if(hierachys[l].equals(siteHier.getHierName())){
						tmpParentHier = siteHier;
						break;
					}
				}
			}
		}
		if(root.getUrlMap().get(s) == null){
			System.out.println("============"+s);
		}
		return root.getUrlMap().get(s);
	}


	public Set<String> getUrls() {
		String urlCrawled = "";
		Set<String> docUrls = new HashSet<String>();
		Elements eles;
		docUrls.add(entranceUrl);
		Set<String> loopedUrls;
		Set<String> loopedUrlsTemp = new HashSet<String>();
		int loopCount = 0;
		ConcurrentHashMap<String,String> value;
		String hostUrl;
		ConcurrentEntry entry;
		Document document = null;
		int errrorPages = 0;
		while(docUrls.size() < maxSize && loopCount++ < 20){
			loopedUrls = new HashSet<String>(docUrls);
			loopedUrls.removeAll(loopedUrlsTemp);
			loopedUrlsTemp = new HashSet<String>(loopedUrls);
			for(String url : loopedUrlsTemp){
				if(url == null || url.isEmpty()){
					continue;
				}
				try {
					hostUrl = WebConfigSnifferUtil.getHostByUrl(url, true);
					document = conn.timeout(3000).referrer(hostUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0").url(url).ignoreContentType(true).get();
					SiteHier siteHier = addSiteHier(url);
					if(siteHier == null){
						continue;
					}
					siteHier.setDocument(document);
					eles = document.select("a[href]");
					for(Element ele : eles){
						urlCrawled = normonizeUrl(ele.attr("href"),entranceUrl);
						if(StringUtils.isBlank(urlCrawled)){
							continue;
						}
						String regExpUrl = WebConfigSnifferUtil.getRegExpFromUrl(urlCrawled);
						if(StringUtils.isBlank(regExpUrl)){
							continue;
						}
						docUrls.add(urlCrawled);
					}
				} catch (Exception e) {
					try {
//						e.printStackTrace();
						Thread.currentThread().sleep((errrorPages++)*100);
					} catch (InterruptedException e1) {
					}
				}

			}
		}
		docUrls.remove(null);
		return docUrls;
	}
	
	private String normonizeUrl(String url,String sourceUrl){
		if(url==null || url.trim().length() <= 3){
			return null;
		}
//		if(!url.contains(WebConfigSnifferUtil.getHostByUrl(sourceUrl))){
//			return null;
//		}
		if(url.contains("mailto:")){
			return null;
		}
		if(url.startsWith("javascript")){
			return null;
		}
		if(!url.startsWith("http")  ){
			String hostUrl = WebConfigSnifferUtil.getHostByUrl(sourceUrl, true);
			hostUrl = hostUrl.endsWith("/")? hostUrl.substring(0, hostUrl.length() - 1): hostUrl;
			url = url.startsWith("/") ? url : "/"+url;
			if(!url.contains(hostUrl)){
				url = hostUrl.concat(url);
			}else{
				return null;
			}
		}
		url = url.split("#")[0];
		url = url.endsWith("/")? url.substring(0, url.length() - 1) : url;
		return url;
	}
	

	


/*	private Document addDocumentToSet(String url) {
		if(url == null){
			return null;
		}
		try {
			return conn.timeout(3000).url(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}*/
}
