package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.mapper.WebsiteConfigMapper;
import com.viki.crawlConfig.utils.ConcurrentEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Component
public class WebConfigJobBalancer {


	Logger logger = LoggerFactory.getLogger(WebConfigJobBalancer.class);

//	public static final ArrayBlockingQueue<String> webconfigJobQueue = new ArrayBlockingQueue<String>(100, false);

	public static final ArrayBlockingQueue<ConcurrentEntry> uncrawledUrlQueue = new ArrayBlockingQueue<ConcurrentEntry>(100);

	/*
	* 所有获取到的地址
	* */
	public static final ConcurrentHashMap<String,String> allWebUrl = new ConcurrentHashMap<String,String>();

	/*
	* 所有没有爬取过页面的地址
	* */
//	public static final ConcurrentHashMap<String,String> allWebUrlNotCrawled = new ConcurrentHashMap<String,String>();

	/*
	* 所有爬取过的地址的简化正则, key是正则，value是符合该正则的地址列表
	* */
	public static final ConcurrentHashMap<String,ConcurrentEntry>allWebRegUrl = new ConcurrentHashMap<String,ConcurrentEntry>();

	public static final Integer threshold = 5;

	public static ReentrantLock procucerLock = new ReentrantLock(true);

	public static final String balancerLock = "";

	@Autowired
	WebsiteConfigMapper websiteConfigMapper;

	@Scheduled(fixedDelay = 86400000)
	public void execute(){
		Constants.executorService.submit(new WebConfigJobProducer(websiteConfigMapper));
//		Constants.executorService.submit(new WebConfigJobConsumer(websiteConfigMapper));
		Constants.executorService.submit(new WebConfigJobConsumer(websiteConfigMapper));
		Constants.executorService.submit(new ThreadMonitor(websiteConfigMapper));
		logger.info("启动咯");
	}
	/*
	public static Connection conn;
	public static java.sql.Connection connDB;
	private void getFrom() throws Exception{
		conn = ConnectionFactory.getConnection().getValue();
		connDB = DataBaseConnFactory.getConn();
		ResultSet rs =connDB.prepareStatement("select stock_name from stock").executeQuery();
		List<Stock> stockList = new ArrayList<Stock>();
		while(rs.next()){
			Stock s = new Stock();
			s.setStockName(rs.getString("stock_name"));
			stockList.add(s);
		}
		Document doc;
		HashSet<WebsiteConfig> wcs = new HashSet<WebsiteConfig>();
		for(Stock st : stockList){
			doc = conn.url("http://www.iwencai.com/stockpick/search?typed=1&preParams=&ts=1&f=1&qs=1&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w="+st.getStockName()).get();
			Element ele = doc.select("#jhszgdyb_yqfmxw").first();
			Elements eles = ele.select("a[href]");
			for(Element e : eles){
				WebsiteConfig wc = new WebsiteConfig();
				wc.setEntranceUrl(e.attr("href"));
				logger.info(e.attr("href"));
			}
		}
	
	}
	
	public static void main(String[] args){
		WebConfigJobBalancer webConfigJobBalancer = new WebConfigJobBalancer();
		webConfigJobBalancer.execute();
	}
*/
}
