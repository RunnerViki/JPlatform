package com.viki.crawlConfig.crawl;

import com.alibaba.fastjson.JSONObject;
import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.bean.WebsiteConfig;
import com.viki.crawlConfig.mapper.WebsiteConfigMapper;
import com.viki.crawlConfig.utils.ConcurrentEntry;
import com.viki.crawlConfig.utils.ConnectionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 入口爬取线程
 *
 * @author Administrator
 *
 */
public class WebConfigJobProducer implements Runnable {

	private ArrayBlockingQueue<ConcurrentEntry> webconfigJobQueue;

	Logger logger = LoggerFactory.getLogger(WebConfigJobProducer.class);

	public WebConfigJobProducer(ArrayBlockingQueue<ConcurrentEntry> webconfigJobQueue){
		this.webconfigJobQueue = webconfigJobQueue;
		Thread.currentThread().setName("Thread"+Thread.activeCount()+":\tWebConfigJobProducer");
	}

	WebsiteConfigMapper websiteConfigMapper;

	public WebConfigJobProducer(WebsiteConfigMapper websiteConfigMapper){
		this.websiteConfigMapper = websiteConfigMapper;
		this.webconfigJobQueue = WebConfigJobBalancer.uncrawledUrlQueue;
		Thread.currentThread().setName("Thread"+Thread.activeCount()+":\tWebConfigJobProducer");
	}

	/*
	 * 从百度RSS推荐中拿到入口地址，每十分钟执行一次
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try{
			Thread.currentThread().setName("WebConfigJobProducer");
			List<WebsiteConfig> rst = websiteConfigMapper.getList(new HashMap<String, Object>());
			for(WebsiteConfig websiteConfig : rst){
				WebConfigJobBalancer.allWebRegUrl.putIfAbsent(websiteConfig.getUrlPattern(), new ConcurrentEntry(websiteConfig.getUrlPattern(), new ConcurrentHashMap<String,
						String>()).setIsUsed(true));
			}
			logger.info("初始化的正则地址:"+WebConfigJobBalancer.allWebRegUrl);
			while(true){
				try {
					baseEntrance();
					for(String regUrl : WebConfigJobBalancer.allWebRegUrl.keySet()){
						for(Map.Entry<String, String> url : WebConfigJobBalancer.allWebRegUrl.get(regUrl).getValue().entrySet()){
							if("false".equals(url.getValue())){
								try{
									new UrlSniffer(url.getKey()).getUrls();
									url.setValue("true");
								}catch (Exception e){
									logger.info(e.getMessage());
								}
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}finally {
					try {
						Thread.currentThread().sleep(3600000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}catch (Exception e){
			logger.info("error\t"+e.getMessage());
		}
	}

	String entrence = "http://www.51cto.com/";
//	entrence = ;

	private void baseEntrance(){
		try {
			Document doc = ConnectionFactory.getConnection()
					.url("http://geek.csdn.net/service/news/get_news_list?username=&from=-&size=20&type=HackCount&_="+System.currentTimeMillis())
					.ignoreContentType(true)
					.get();
			JSONObject docjson = JSONObject.parseObject(doc.text());
			doc = Jsoup.parse(docjson.getString("html"));
			Elements eles = doc.select("a[href]");
			String attr;
			ConcurrentHashMap<String,String> value;
			ConcurrentEntry entry;
			for(Element ele : eles ){
				attr = ele.attr("href");
				if(!WebConfigSnifferUtil.sourceLinksFilter(attr)){
					continue;
				}
				String regExpUrl = WebConfigSnifferUtil.getRegExpFromUrl(attr);
				if (!WebConfigJobBalancer.allWebRegUrl.containsKey(regExpUrl)) {
					value = new ConcurrentHashMap<>(Constants.CRAWLED_GROUP_SIZE + 6);
					value.putIfAbsent(attr, "false");
					entry = new ConcurrentEntry(regExpUrl, value);
//					logger.info("新加地址正则regExpUrl:"+regExpUrl);
					WebConfigJobBalancer.allWebRegUrl.put(regExpUrl, entry);
				}else if((entry = WebConfigJobBalancer.allWebRegUrl.get(regExpUrl)) != null && !entry.getIsUsed() && (value = entry.getValue()) != null && !value.containsKey(attr)){
					value.putIfAbsent(attr, "false");
//					logger.info("向jobqueue中已有entry("+regExpUrl+")添加新地址:"+attr);
					if(value.size() >= Constants.CRAWLED_GROUP_SIZE && value.size() <= Constants.CRAWLED_GROUP_SIZE + 2 && !WebConfigJobBalancer.uncrawledUrlQueue.contains(entry)){
						webconfigJobQueue.put(entry);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*	private void bingSearch(){
		Document doc;
		for(int pn = 0;pn < 20; pn++){
			try {
				doc = ConnectionFactory.getConnection().url("http://cn.bing.com/search?q=财经").get();
				Elements eles = doc.select("cite");
				for(Element ele : eles ){
					String regExpUrl = WebConfigSnifferUtil.getRegExpFromUrl(ele.text());
					if (!WebConfigJobBalancer.allWebRegUrl.containsKey(regExpUrl)) {
						WebConfigJobBalancer.allWebRegUrl.put(regExpUrl, "");
						WebConfigJobBalancer.allWebUrl.put(ele.text(), "unused");
						try {
							webconfigJobQueue.put(ele.attr("href"));
							WebConfigJobBalancer.allWebUrl.put(ele.text(), "used");
						} catch (InterruptedException e) {
							try {
								Thread.currentThread().sleep(1000);
							} catch (InterruptedException e1) {
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/

/*	private void baiduRss(){
		RSSFeedParser parser = new RSSFeedParser("http://news.baidu.com/n?cmd=1&class=stock&tn=rss");
		Feed feed = parser.readFeed();
		for (FeedMessage message : feed.getMessages()) {
			String regExpUrl = WebConfigSnifferUtil.getRegExpFromUrl(message.getLink());

			//如果是百度的链接，则跳过
			if(regExpUrl.contains("baidu.com")){
				continue;
			}

			//如果allWebUrl不含有该地址，再添加到allWebUrl中
			if (!WebConfigJobBalancer.allWebRegUrl.containsKey(regExpUrl)) {
				WebConfigJobBalancer.allWebRegUrl.put(regExpUrl, "");
				WebConfigJobBalancer.allWebUrl.put(message.getLink(), "unused");
				try {
					webconfigJobQueue.put(message.getLink());
					WebConfigJobBalancer.allWebUrl.put(message.getLink(), "used");
				} catch (InterruptedException e) {
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e1) {
					}
				}
			}
		}
	}*/
}
