package com.viki.crawlConfig.crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ThreadMonitor implements Runnable{

	Logger logger = LoggerFactory.getLogger(ThreadMonitor.class);

	@Override
	public void run() {
		while(true){
			logger.info("WebConfigJobBalancer.allWebRegUrl:"+new Date()+"\t"+WebConfigJobBalancer.allWebRegUrl);
			logger.info("WebConfigJobBalancer.webconfigJobQueue.size():"+new Date()+"\t"+WebConfigJobBalancer.uncrawledUrlQueue.size());
			/*logger.info(JSON.toJSONString(Thread.getAllStackTraces()));*/
			try {
				Thread.currentThread().sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
