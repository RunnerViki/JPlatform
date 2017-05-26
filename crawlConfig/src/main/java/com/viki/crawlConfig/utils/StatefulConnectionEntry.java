package com.viki.crawlConfig.utils;

import org.jsoup.Connection;

import java.util.Map;

public class StatefulConnectionEntry implements Map.Entry<String,Connection> {

	private String status;
	
	private Connection conn;
	
    public StatefulConnectionEntry(String key, Connection value) {
        this.status = key;
        this.conn = value;
    }
    
    public void setKey(String status){
    	this.status = status;
    }
	
	@Override
	public String getKey() {
		return status;
	}

	@Override
	public Connection getValue() {
		return conn;
	}

	@Override
	public Connection setValue(Connection value) {
		this.conn = value;
		return this.conn;
	}
	
	public Connection url(String url){
		return this.conn.url(url);
	}

}
