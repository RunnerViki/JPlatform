package com.viki.crawlConfig.crawl;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import com.viki.crawlConfig.utils.ConnectionFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 入口爬取线程
 *
 * @author Administrator
 *
 */
public class WebConfigJobProducer implements Runnable {

	private ArrayBlockingQueue<String> webconfigJobQueue;

	public WebConfigJobProducer(ArrayBlockingQueue<String> webconfigJobQueue){
		this.webconfigJobQueue = webconfigJobQueue;
		Thread.currentThread().setName("Thread"+Thread.activeCount()+":\tWebConfigJobProducer");
	}

	/*
	 * 从百度RSS推荐中拿到入口地址，每十分钟执行一次
	 *
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(true){
			try {
				Thread.currentThread().sleep(1000);
				baseEntrance();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void baseEntrance(){
		try {
			Document doc = ConnectionFactory.getConnection().url("http://blog.csdn.net").get();
			Elements eles = doc.select("a[href]");
			for(Element ele : eles ){
				if(!WebConfigSnifferUtil.sourceLinksFilter(ele.attr("href"))){
					continue;
				}
				String regExpUrl = WebConfigSnifferUtil.getRegExpFromUrl(ele.attr("href"));
				if (!WebConfigJobBalancer.allWebRegUrl.containsKey(regExpUrl)) {
					WebConfigJobBalancer.allWebRegUrl.put(regExpUrl, "");
					WebConfigJobBalancer.allWebUrl.put(ele.attr("href"), "unused");
					try {
						webconfigJobQueue.put(ele.attr("href"));
						WebConfigJobBalancer.allWebUrl.put(ele.attr("href"), "used");
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
