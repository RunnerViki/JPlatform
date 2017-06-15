package com.viki.crawlConfigNew.bean;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Viki on 2017/6/4.
 */
public class Configuration {

    public static final int parseContentThroshold = 1000;

    public static final ArrayBlockingQueue<SiteHier> siteHierBlockingQueue = new ArrayBlockingQueue<SiteHier>(100);
}
