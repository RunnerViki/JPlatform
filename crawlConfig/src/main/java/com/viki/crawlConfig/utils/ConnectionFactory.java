package com.viki.crawlConfig.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionFactory {

	public static Integer poolSize = 5;
	
	public static ArrayList<StatefulConnectionEntry> connections = new ArrayList<StatefulConnectionEntry>(poolSize);
	
	public static final String STATUS_IDLE = "idle";
	
	public static final String STATUS_BUSY = "busy";
	
	static{
		for(int n = 0; n < poolSize; n++){
			Connection conn = Jsoup.connect("http:\\www.baidu.com");
			conn.request().header("Connection", "Keep-Alive");
			conn.request().header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:34.0) Gecko/20100101 Firefox/34.0");
			conn.request().header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			conn.request().header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			conn.request().header("Accept-Encoding", "gzip, deflate");
			conn.request().header("http.protocol.cookie-policy", "compatibility");
			conn.request().header("Cache-Control", "max-age=0");
			conn.timeout(30000);
			//conn.request().header("Host","money.finance.sina.com.cn");
			connections.add(new StatefulConnectionEntry("idle",conn));
		}
	}
	
	public static synchronized StatefulConnectionEntry getConnection(){
		for(StatefulConnectionEntry connectionEntry : connections){
			if(connectionEntry.getKey().equals(STATUS_IDLE)){
				connectionEntry.setKey(STATUS_BUSY);
				return connectionEntry;
			}
		}
		Connection conn = Jsoup.connect("http:\\www.baidu.com");
		conn.request().header("Connection", "Keep-Alive");
		conn.request().header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:34.0) Gecko/20100101 Firefox/34.0");
		conn.request().header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.request().header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		conn.request().header("Accept-Encoding", "gzip, deflate");
		conn.request().header("http.protocol.cookie-policy", "compatibility");
		conn.request().header("Cache-Control", "max-age=0");
		//conn.request().header("Host","");
		conn.timeout(30000);
		StatefulConnectionEntry newlyConn = new StatefulConnectionEntry(STATUS_BUSY,conn);
		connections.add(newlyConn);
		return newlyConn;
	}
	
	public static void releaseConnection(StatefulConnectionEntry statefulConnectionEntry){
		statefulConnectionEntry.getValue().cookies(new HashMap<String,String>());
		statefulConnectionEntry.setKey(STATUS_IDLE);
	}
}
