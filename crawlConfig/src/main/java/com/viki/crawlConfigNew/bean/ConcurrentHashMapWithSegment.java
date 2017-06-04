package com.viki.crawlConfigNew.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Viki on 2017/6/4.
 */
public class ConcurrentHashMapWithSegment {

    public static int segments = 16;

    public static List<Segment> segmentList = new ArrayList<>(segments);

    static {
        for(int i = 0; i < segments; i++){
            segmentList.add(new Segment());
        }
    }


    public static SiteHier put(String key, SiteHier value){
        int segmentNo = (key.hashCode() % segments + segments ) % segments;
        Segment segment = segmentList.get(segmentNo);
        try {
            if(segment.tryLock(3, TimeUnit.SECONDS)){
                segment.put(key, value);
                return value;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            segment.unlock();
        }
        return null;
    }

    public static Set<String> keySet(){
        Set<String> keySet = new HashSet<>();
        for(int i = 0; i < segments; i++){
            keySet.addAll(segmentList.get(i).keySet());
        }
        return keySet;
    }

    public static SiteHier putIfAbsent(String key, SiteHier value){
        int segmentNo = (key.hashCode() % segments + segments ) % segments;
        Segment segment = segmentList.get(segmentNo);
        try {
            if(segment.tryLock(3, TimeUnit.SECONDS)){
                segment.putIfAbsent(key, value);
                return value;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            segment.unlock();
        }
        return null;
    }

    public static SiteHier get(String key){
        int segmentNo = (key.hashCode() % segments + segments ) % segments ;
        Segment segment = segmentList.get(segmentNo);
        return segment.get(key);
    }

    public static boolean containsKey(String key){
        int segmentNo = (key.hashCode() % segments + segments ) % segments;
        Segment segment = segmentList.get(segmentNo);
        return segment.containsKey(key);
    }


}
