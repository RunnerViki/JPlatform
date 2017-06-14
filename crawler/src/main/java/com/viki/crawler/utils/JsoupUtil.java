package com.viki.crawler.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
* @ClassName: 	JsoupUtil
* @Description: Jsoup工具类
* @Author: 		YangCX
* @date:		2015年5月20日 上午11:25:05
*
*/ 
public class JsoupUtil {



	private static final ThreadLocal<Connection> byConnBean = new ThreadLocal<Connection>(){
		protected Connection initialValue() {
			Connection connection = Jsoup.connect("https://www.baidu.com/");
			connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0");
			connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.header("Accept-Language", "zh-CN,zh;q=0.8");
			connection.header("Accept-Encoding", "gzip, deflate");
			connection.header("Referer", "https://www.baidu.com/");
			connection.header("Connection", "keep-alive");
			connection.ignoreContentType(true);
			connection.ignoreHttpErrors(true);
			connection.timeout(30000);
			return connection;
		}
	};


	public static Connection getByConn(){
		return byConnBean.get();
	}

}
