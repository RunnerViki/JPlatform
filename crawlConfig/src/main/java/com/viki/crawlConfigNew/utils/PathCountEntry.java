package com.viki.crawlConfigNew.utils;

import java.util.Map;

public class PathCountEntry implements Map.Entry<String,Integer> {

	private String path;

	private Integer count;

    public PathCountEntry(String key, Integer value) {
        this.path = key;
        this.count = value;
    }

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	public void setKey(String path){
    	this.path = path;
    }
	
	@Override
	public String getKey() {
		return path;
	}

	@Override
	public Integer getValue() {
		return count;
	}

	@Override
	public Integer setValue(Integer value) {
		this.count = value;
		return this.count;
	}
}
