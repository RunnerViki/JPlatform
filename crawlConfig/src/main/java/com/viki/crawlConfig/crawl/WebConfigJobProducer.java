package com.viki.crawlConfig.crawl;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import com.viki.crawlConfig.utils.ConnectionFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * �����ȡ�߳�
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
	 * �Ӱٶ�RSS�Ƽ����õ���ڵ�ַ��ÿʮ����ִ��һ��
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
				doc = ConnectionFactory.getConnection().url("http://cn.bing.com/search?q=�ƾ�").get();
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

			//����ǰٶȵ����ӣ�������
			if(regExpUrl.contains("baidu.com")){
				continue;
			}

			//���allWebUrl�����иõ�ַ������ӵ�allWebUrl��
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
