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

	private static final ThreadLocal<Connection> byZHConnBean = new ThreadLocal<Connection>(){
		protected Connection initialValue() {
			Connection connection = Jsoup.connect("https://www.zhihu.com");
			connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0");
			connection.header("Accept", "application/json, text/plain, */*");
			connection.header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			connection.header("Accept-Encoding", "gzip, deflate, br");
			connection.header("Referer", "https://www.baidu.com/");
			connection.header("Origin", "https://www.zhihu.com");
			connection.header("Cookie", "q_c1=f46c20b9096341d494742e6d12bb6daa|1497596631000|1490595555000; " +
					"r_cap_id=\"N2M4ZWM5N2MzNWU3NGJkYzlkMzVhNWFjMDQ5NGQ3Mzk=|1497875188|ea234f645c0275df110cc07821b08ed6c54bd80d\"; " +
					"cap_id=\"OGIzNzExMzg5YzY1NDJmOThiOGQxODBmNGNjY2NhZGI=|1497875188|973334d07890c93e9a22505274249b43e509e9de\"; " +
					"d_c0=\"ABDCC2U7kguPTm_AW1wxk-ENtiI9y-hvBrw=|1491572025\"; _zap=9b68babc-8a97-4fd1-8e96-49a4aeb56ae0; " +
					"__utma=51854390.881962046.1497873164.1497873164.1497873178.2; __utmz=51854390.1497873178.2.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; " +
					"l_cap_id=\"NjMzM2RiZTYwN2I2NDI1Y2IxMDJjMzQ4ZDQxNTJlNjc=|1497875188|1b4067fd2255f9543919db339173380eb02f61ab\"; " +
					"capsion_ticket=\"2|1:0|10:1495338724|14:capsion_ticket|44:MzU0ZGE0NmExNTQ4NDQ5MTg1NWNkOTAyYWE1NDNlMjQ" +
					"=|d644c682b28ed0a54f3e2aac310dce32b504efbdc903fa853779addaa2252d02\"; q_c1=f46c20b9096341d494742e6d12bb6daa|1497596631000|1490595555000; " +
					"aliyungf_tc=AQAAAN/9RhZ3PwUA750R2sJ3GAAFmAiA; _xsrf=d810db8a615f34628e91005770ac9b2e; __utmc=51854390; __utmv=51854390.000--|3=entry_date=20170327=1");
			connection.header("Connection", "keep-alive");
			connection.header("Cache-Control", "max-age=0");
			connection.header("Reffer", "https://www.zhihu.com");
			connection.header("x-udid", "ABDCC2U7kguPTm_AW1wxk-ENtiI9y-hvBrw=");
			connection.header("Authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20");
			connection.ignoreContentType(true);
			connection.ignoreHttpErrors(true);
			connection.timeout(30000);
			return connection;
		}
	};


	public static Connection getZHConn(){
		return byZHConnBean.get();
	}

	public static Connection getByConn(){
		return byConnBean.get();
	}

}
