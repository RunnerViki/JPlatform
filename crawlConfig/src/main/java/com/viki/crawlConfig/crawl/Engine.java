package com.viki.crawlConfig.crawl;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.viki.crawlConfig.bean.WebsiteConfig;
import org.jsoup.nodes.Document;


public class Engine {

	public static void main(String[] args){
		WebsiteConfig websiteConfig = new WebsiteConfig();
		UrlSniffer urlSniffer = new UrlSniffer("http://news.hexun.com/2015-04-08/174780595.html");
		UrlGroupSpliter urlGroupSpliter = new UrlGroupSpliter(urlSniffer.getUrls());
		Entry<String, HashSet<String>> entry = urlGroupSpliter.seperateAndGetFirst().next();
		websiteConfig.setUrlPattern(entry.getKey());
		
		DocumentsGen documentsGen = new DocumentsGen(entry.getValue());
		Set<Document> documents = documentsGen.gen();
		
		TitleSniffer titleSniffer = new TitleSniffer(documents);
		String titleXpath = titleSniffer.extractTitleXPath();
		websiteConfig.setTitleXpath(titleXpath);
		//logger.info("titleXpath"+titleXpath);
		
		ContentSniffer contentSniffer = new ContentSniffer(documents);
		String contentXpath = contentSniffer.extractContentXpath();
		websiteConfig.setContentXpath(contentXpath);
		//logger.info("contentXpath"+contentXpath);
		
		PostdateSniffer postdateSniffer = new PostdateSniffer(documents,titleXpath,contentXpath);
		String postdateXpath = postdateSniffer.extractPostDate();
		websiteConfig.setPostdateXpath(postdateXpath);
		websiteConfig.setPostdateFormat(postdateSniffer.getPostdateFormat());
	}
	

}
