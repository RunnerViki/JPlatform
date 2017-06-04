package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.utils.ConcurrentEntry;
import com.viki.crawlConfig.utils.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
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

	private Integer maxSize;

	private Connection conn;

	public UrlSniffer(String entranceUrl, Integer maxSize) {
		this.entranceUrl = entranceUrl;
		this.maxSize = maxSize;
		this.conn = ConnectionFactory.getConnection().getValue();
	}
	
	public UrlSniffer(String entranceUrl){
		this(entranceUrl,50);
	}
	
	public static void main(String[] args){
		UrlSniffer us = new UrlSniffer("http://kuaixun.stcn.com/2015/0408/12156763.shtml",200);
		us.test();
		//logger.info(us.getHostByUrl(us.entranceUrl,true));
	}
	
	private void test(){
		if(Boolean.parseBoolean("true")){
			System.setProperty("http.proxyHost", "192.168.91.11");
			System.setProperty("http.proxyPort", "80");
		}
		UrlSniffer us = new UrlSniffer("http://kuaixun.stcn.com/2015/0408/12156763.shtml",20);
		/*for(String docUrl : us.getUrls()){
			if(docUrl != null){
				logger.info(docUrl);
			}
		}*/
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
		ConcurrentEntry entry;
		while(docUrls.size() < maxSize && loopCount++ < 20){
			loopedUrls = new HashSet<String>(docUrls);
			loopedUrls.removeAll(loopedUrlsTemp);
			loopedUrlsTemp = new HashSet<String>(loopedUrls);
			for(String url : loopedUrlsTemp){
				if(url == null || url.isEmpty()){
					continue;
				}
				try {
					eles = conn.timeout(3000).url(url).get().select("a[href]");
					for(Element ele : eles){
						urlCrawled = normonizeUrl(ele.attr("href"),entranceUrl);
						if(StringUtils.isBlank(urlCrawled)){
							continue;
						}
						String regExpUrl = WebConfigSnifferUtil.getRegExpFromUrl(urlCrawled);
						if(StringUtils.isBlank(regExpUrl)){
							continue;
						}
						/*if (!WebConfigJobBalancer.allWebRegUrl.containsKey(regPattern)) {
							value = new ConcurrentHashMap<>(50);
							value.putIfAbsent(urlCrawled, null);
							entry = new ConcurrentEntry(regPattern, value);
							WebConfigJobBalancer.uncrawledUrlQueue.offer(entry);
							logger.info("新加地址正则regExpUrl:"+regPattern);
							WebConfigJobBalancer.allWebRegUrl.put(regPattern, new WeakReference<ConcurrentEntry>(entry));
						}else if(WebConfigJobBalancer.allWebRegUrl.get(regPattern) != null){
							value = WebConfigJobBalancer.allWebRegUrl.get(regPattern).get().getValue();
							value.putIfAbsent(urlCrawled, null);
							logger.info("向jobqueue中已有entry("+regPattern+")添加新地址:"+urlCrawled);
						}*/


						if (!WebConfigJobBalancer.allWebRegUrl.containsKey(regExpUrl)) {
							value = new ConcurrentHashMap<>(Constants.CRAWLED_GROUP_SIZE + 5);
							value.putIfAbsent(urlCrawled, "false");
							entry = new ConcurrentEntry(regExpUrl, value);
//							logger.info("新加地址正则regExpUrl:"+regExpUrl);
							WebConfigJobBalancer.allWebRegUrl.put(regExpUrl, entry);
						}else if((entry = WebConfigJobBalancer.allWebRegUrl.get(regExpUrl)) != null  && !entry.getIsUsed() && (value = entry.getValue()) != null && !value.containsKey(urlCrawled)){
							value.putIfAbsent(urlCrawled, "false");
//							logger.info("向jobqueue中已有entry("+regExpUrl+")添加新地址:"+urlCrawled);
							if(value.size() >= Constants.CRAWLED_GROUP_SIZE && value.size() <= Constants.CRAWLED_GROUP_SIZE + 2 && !WebConfigJobBalancer.uncrawledUrlQueue.contains(entry)){
								if(!WebConfigJobBalancer.uncrawledUrlQueue.contains(entry)){
									WebConfigJobBalancer.uncrawledUrlQueue.offer(entry);
								}
							}
						}

						/*docUrls.add(urlCrawled);
						if(!WebConfigJobBalancer.allWebUrl.containsKey(urlCrawled)){
							WebConfigJobBalancer.allWebUrl.put(urlCrawled,"");

							if (!WebConfigJobBalancer.allWebRegUrl.containsKey(regPattern)) {
								logger.info("新加地址正则regExpUrl:"+regPattern);
								WebConfigJobBalancer.allWebRegUrl.putIfAbsent(regPattern,"");
								WebConfigJobBalancer.uncrawledUrlQueue.offer(urlCrawled);
							}
						}*/
					}
				} catch (Exception e) {
					try {
						Thread.currentThread().sleep(loopCount*100);
					} catch (InterruptedException e1) {
					}
				}

			}
		}
		docUrls.remove(null);
		return docUrls;
	}
	
	private String normonizeUrl(String url,String sourceUrl){
		if(url==null || url.length() == 0){
			return null;
		}
//		if(!url.contains(WebConfigSnifferUtil.getHostByUrl(sourceUrl))){
//			return null;
//		}
		if(url.startsWith("javascript")){
			return null;
		}
		if(!url.startsWith("http")  ){
			String hostUrl = WebConfigSnifferUtil.getHostByUrl(sourceUrl);
			hostUrl = hostUrl.endsWith("/")? hostUrl.substring(0, hostUrl.length() - 1): hostUrl;
			url = url.startsWith("/") ? url : "/"+url;
			if(!url.contains(hostUrl)){
				url = hostUrl.concat(url);
			}else{
				return null;
			}
		}
		return url.split("#")[0];
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
