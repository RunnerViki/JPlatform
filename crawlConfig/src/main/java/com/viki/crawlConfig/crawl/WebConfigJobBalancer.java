package com.viki.crawlConfig.crawl;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.bean.WebsiteConfig;
import com.viki.crawlConfig.mapper.WebsiteConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class WebConfigJobBalancer {


	public static final ArrayBlockingQueue<String> webconfigJobQueue = new ArrayBlockingQueue<String>(1000, false);
	
	public static final ConcurrentHashMap<String,String> allWebUrl = new ConcurrentHashMap<String,String>();
	
	public static final ConcurrentHashMap<String,String> allWebRegUrl = new ConcurrentHashMap<String,String>();
	
	public static final Integer threshold = 5; 
	
	public static ReentrantLock procucerLock = new ReentrantLock(true);
	
	public static final String balancerLock = "";

	@Autowired
	WebsiteConfigMapper websiteConfigMapper;

	@Scheduled(fixedDelay = 86400000)
	public void execute(){
		 try {
			List<WebsiteConfig> webs  = websiteConfigMapper.getList(null);
			for(WebsiteConfig wc :webs){
				allWebRegUrl.put(wc.getUrlPattern(), "");
			}
		} catch (Exception e) {
		}
		 
		Constants.executorService.submit(new WebConfigJobProducer(webconfigJobQueue));
//		Constants.executorService.submit(new WebConfigJobConsumer(webconfigJobQueue));
		Constants.executorService.submit(new WebConfigJobConsumer(webconfigJobQueue));
		Constants.executorService.submit(new ThreadMonitor());
		System.out.println("Æô¶¯¿©");
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
				System.out.println(e.attr("href"));
			}
		}
	
	}
	
	public static void main(String[] args){
		WebConfigJobBalancer webConfigJobBalancer = new WebConfigJobBalancer();
		webConfigJobBalancer.execute();
	}
*/
}
