package com.viki.crawlConfigNew.bean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Viki on 2017/6/4.
 */
public     class Segment extends ConcurrentHashMap<String,SiteHier> {
    ReentrantLock reentrantLock = new ReentrantLock();

    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        return reentrantLock.tryLock(timeout, unit);
    }

    public boolean tryLock()
            throws InterruptedException {
        return reentrantLock.tryLock();
    }

    public void unlock() {
        reentrantLock.unlock();
    }
}