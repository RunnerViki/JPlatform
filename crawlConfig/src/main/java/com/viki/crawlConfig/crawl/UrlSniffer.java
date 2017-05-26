package com.viki.crawlConfig.crawl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.viki.crawlConfig.utils.ConnectionFactory;
import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 最少获取到maxSize的url个数
 * @author vikiyang
 *
 */
public class UrlSniffer {

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
		//System.out.println(us.getHostByUrl(us.entranceUrl,true));
	}
	
	private void test(){
		if(Boolean.parseBoolean("true")){
			System.setProperty("http.proxyHost", "192.168.91.11");
			System.setProperty("http.proxyPort", "80");
		}
		UrlSniffer us = new UrlSniffer("http://kuaixun.stcn.com/2015/0408/12156763.shtml",20);
		/*for(String docUrl : us.getUrls()){
			if(docUrl != null){
				System.out.println(docUrl);
			}
		}*/
	}

	public Set<String> getUrls() {
		
		Set<String> docUrls = new HashSet<String>();
		Elements eles;
		docUrls.add(entranceUrl);
		Set<String> loopedUrls;
		Set<String> loopedUrlsTemp = new HashSet<String>();
		int loopCount = 0;
		while(docUrls.size() < maxSize && loopCount++ < 20){
			loopedUrls = new HashSet<String>(docUrls);
			loopedUrls.removeAll(loopedUrlsTemp);
			loopedUrlsTemp = new HashSet<String>(loopedUrls);
			for(String url : loopedUrlsTemp){
				if(url == null || url.isEmpty()){
					continue;
				}
				//System.out.println(url);
				try {
					eles = conn.timeout(3000).url(url).get().select("a[href]");
					for(Element ele : eles){
						docUrls.add(normonizeUrl(ele.attr("href"),entranceUrl));
					}
				} catch (IOException e) {
					try {
						Thread.currentThread().sleep(loopCount*100);
					} catch (InterruptedException e1) {
					}
				}

			}
		}
		docUrls.remove(null);
		
		/*try {
			Document doc = conn.timeout(3000).url(entranceUrl).get();
			
			Elements eles = doc.select("a[href]");
			loopedUrls = new HashSet<String>(docUrls);
			Iterator<String> urlIter = loopedUrls.iterator();
			while(docUrls.size() < maxSize){
				String url ="";
				if(urlIter.hasNext()){
					url = urlIter.next();
					if(url == null || url.isEmpty()){
						continue;
					}
				}else{
					loopedUrlsTemp = new HashSet<String>(loopedUrls);
					loopedUrls = new HashSet<String>(docUrls);
					loopedUrls.removeAll(loopedUrlsTemp);
				}
				if(url == null || url.isEmpty()){
					continue;
				}
				System.out.println(url);
				eles = conn.timeout(3000).url(url).get().select("a[href]");
				for(Element ele : eles){
					docUrls.add(normonizeUrl(ele.attr("href"),entranceUrl));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return docUrls;
	}
	
	private String normonizeUrl(String url,String sourceUrl){
		if(url==null || url.length() == 0){
			return null;
		}
		if(!url.contains(WebConfigSnifferUtil.getHostByUrl(sourceUrl))){
			return null;
		}
		if(url.startsWith("javascript")){
			return null;
		}
		if(!url.startsWith("http")  ){
			if(!url.contains(WebConfigSnifferUtil.getHostByUrl(sourceUrl))){
				url = sourceUrl.concat(url);
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
