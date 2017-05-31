package com.viki.crawlConfig.crawl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.bean.WebsiteConfig;
import com.viki.crawlConfig.mapper.WebsiteConfigMapper;
import com.viki.crawlConfig.utils.ConcurrentEntry;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


public class WebConfigJobConsumer implements Runnable {

	private ConcurrentEntry entranceEntry;

	Logger logger = LoggerFactory.getLogger(WebConfigJobConsumer.class);
	
	private ArrayBlockingQueue<ConcurrentEntry> webconfigJobQueue;

	public WebConfigJobConsumer(ArrayBlockingQueue<ConcurrentEntry> webconfigJobQueue) {
			this.webconfigJobQueue = webconfigJobQueue;
	}

	@Autowired
	WebsiteConfigMapper websiteConfigMapper;

	public WebConfigJobConsumer(WebsiteConfigMapper websiteConfigMapper) {
		this.webconfigJobQueue = WebConfigJobBalancer.uncrawledUrlQueue;
		this.websiteConfigMapper = websiteConfigMapper;
	}
	
	public WebConfigJobConsumer(ConcurrentEntry entranceEntry){
		this.entranceEntry = entranceEntry;
	}

	@Override
	public void run() {
		Thread.currentThread().setName("WebConfigJobConsumer");
		boolean  isContinue = true;
		while(isContinue){
			try {
				this.entranceEntry = webconfigJobQueue.poll(3, TimeUnit.SECONDS);
				if(entranceEntry == null){
					TimeUnit.SECONDS.sleep(5);
					continue;
				}
				if(entranceEntry.getValue().size() < Constants.CRAWLED_GROUP_SIZE){
					webconfigJobQueue.offer(entranceEntry);
					for(Entry<String,String> entry : entranceEntry.getValue().entrySet()){
						if("false".equals(entry.getValue())){
							UrlSniffer urlSniffer = new UrlSniffer(entry.getKey());
							urlSniffer.getUrls();
							entry.setValue("true");
						}
					}
					continue;
				}
				WebsiteConfig websiteConfig = new WebsiteConfig();
				Set<String> subset = ImmutableSet.copyOf(Iterables.limit(entranceEntry.getValue().keySet(), Constants.CRAWLED_GROUP_SIZE));
				DocumentsGen documentsGen = new DocumentsGen(subset);
				Set<Document> documents = documentsGen.gen();

				TitleSniffer titleSniffer = new TitleSniffer(documents);
				String titleXpath = titleSniffer.extractTitleXPath();
				websiteConfig.setTitleXpath(titleXpath);

				ContentSniffer contentSniffer = new ContentSniffer(documents);
				String contentXpath = contentSniffer.extractContentXpath();
				websiteConfig.setContentXpath(contentXpath);

				if(StringUtils.isNotBlank(titleXpath) && StringUtils.isNotBlank(contentXpath)){
					PostdateSniffer postdateSniffer = new PostdateSniffer(documents, titleXpath, contentXpath);
					String postdateXpath = postdateSniffer.extractPostDate();
					websiteConfig.setPostdateXpath(postdateXpath);
					websiteConfig.setPostdateFormat(postdateSniffer.getPostdateFormat());
				}
				websiteConfig.setUrlPattern(entranceEntry.getKey());
				String entranceUrl = entranceEntry.getValue().keySet().iterator().next();
				websiteConfig.setEntranceUrl(WebConfigSnifferUtil.getHostByUrl(entranceUrl,true));
				websiteConfig.setDomain(WebConfigSnifferUtil.getHostByUrl(entranceUrl));
				websiteConfig.setEncoding(documents.iterator().next().outputSettings().charset().name());
				websiteConfig.setWebName("");
				websiteConfig.setGroupName("");
				websiteConfig.setCrawling_interval(3000);
				websiteConfig.setStopSeconds(600);
				websiteConfig.setUrlSourceNorm("");
				websiteConfig.setUrlREPOrigin("");
				websiteConfig.setUrlREPReplacement("");
				websiteConfig.setUrlPrefix("");
				websiteConfig.setStatus(0);
				websiteConfig.setSampleUrl(Arrays.toString(subset.toArray(new String[entranceEntry.getValue().values().size()])));
				try {
					websiteConfigMapper.insert(websiteConfig);
					entranceEntry.setIsUsed(true);
					entranceEntry.setValue(null);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Consumer出错了 entry.getKey():" + entranceEntry.getKey() +"\t\tentranceUrl:"+entranceUrl + "\t"+JSONObject.toJSONString(websiteConfig) + e.getMessage() + JSONObject.toJSONString(e.getStackTrace()));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.error("Consumer出错了:"+e1.getMessage()+ JSONObject.toJSONString(e1.getStackTrace()));
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;

			}
		}
		logger.info("Consumer 跑完?");
	}



}
