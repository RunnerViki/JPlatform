package com.viki.crawlConfig.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Viki on 2017/5/28.
 * Function: TODO
 */
public class ConcurrentEntry implements Map.Entry<String,ConcurrentHashMap<String,String>>{

    private String key;

    public transient ConcurrentHashMap<String, String> member;

    private boolean isUsed = false;

    public ConcurrentEntry(){}

    public ConcurrentEntry(String key, ConcurrentHashMap<String,String> value){
        this.key = key;
        this.member = value;
    }


    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ConcurrentHashMap<String, String> getValue() {
        return member;
    }

    @Override
    public ConcurrentHashMap<String, String> setValue(ConcurrentHashMap<String, String> value) {
        this.member = value;
        return member;
    }

    public ConcurrentEntry setIsUsed(boolean isUsed){
        this.isUsed = isUsed;
        return this;
    }

    public boolean getIsUsed(){
        return isUsed;
    }
}
