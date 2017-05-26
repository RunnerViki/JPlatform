package com.viki.crawlConfig.bean;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Constants {
	
	public static ApplicationContext ctx;
	
	private ApplicationContext ctx2;
	
	//已经爬取到的，但没有获取到内容的地址
	public static final Integer SAVED_URL_NOT_SCRAWL = 0;

	//已经爬取到的，且已经获取到内容的地址
	public static final Integer SAVED_URL_SCRAWLED = 1;

	//可用的网站配置
	public static final Integer WEB_CONFIG_VALID = 0;

	//不可用的网站配置
	public static final Integer WEB_CONFIG_INVALID = 1;

	public static final Integer GET_CONTENT_SUC = 1;

	public static final Integer GET_CONTENT_FAIL = 0;

	public static final int WORD_TYPE_NORMAL = 1;

	public static final int WORD_TYPE_UNKNOWN = 0;

	public String runningTask = "";

	public static final String CONN_NAME = "CONN";

	public static final Logger logger = Logger.getRootLogger();

//	public static GlobleConfig globleConfig;


	/*static{ TODO
		String[] paths = new String[] {"file:G:\\Workspace\\BigRazor\\WebRoot\\WEB-INF\\applicationContext-mybatis.xml",
		"file:G:\\Workspace\\BigRazor\\WebRoot\\WEB-INF\\applicationContext.xml"};
		ctx = new ClassPathXmlApplicationContext(paths);
		globleConfig = GlobleConfig.getInstance();
		System.out.println("fff");
	}*/

	public static ExecutorService executorService = new ThreadPoolExecutor(15, 15, 60L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

	public static ExecutorService executorServiceMotion = new ThreadPoolExecutor(10, 15, 60L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

	//数据库中存储的配置信息
    //public static HashMap<String,String> configItems = SystemInit.getInstance().getSystemConfigItems();
    
	/*public static void setConfigItems(HashMap<String, String> xx) {
		configItems = xx;
	}*/
	
	public Date lastScrawlTime = new Date();
	
	public Date getLastScrawlTime() {
		return lastScrawlTime;
	}

	public void setLastScrawlTime(Date xx) {
		lastScrawlTime = xx;
	}

	private static  Constants constants;
	
	private Constants(){
	}
	
	/*public static Constants getInstance(){
		if(constants==null){ TODO
			constants = new Constants();
			String[] paths = new String[] {"file:G:\\Workspace\\BigRazor\\WebRoot\\WEB-INF\\applicationContext-mybatis.xml",
			"file:G:\\Workspace\\BigRazor\\WebRoot\\WEB-INF\\applicationContext.xml"};
			constants.ctx = new ClassPathXmlApplicationContext(paths);
			constants.globleConfig = GlobleConfig.getInstance();
		}
		return constants;
	}*/
}
