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
			connection.header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Mobile Safari/537.36");
			connection.header("Accept", "application/json");
			connection.header("Accept-Language", "zh-CN,zh;q=0.8");
			connection.header("Accept-Encoding", "gzip, deflate");
			connection.header("Referer", "https://www.baidu.com/");
			connection.header("d-version", "1.0.0");
			connection.header("X-Requested-With", "XMLHttpRequest");
			connection.header("Content-Type", "application/json;charset=utf-8");
			connection.header("client-os", "web");
			connection.header("Cache-Control", "no-cache" );
			connection.header("Pragma", "no-cache" );
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
