package com.viki.crawlConfig.crawl;

import java.util.Date;

public class ThreadMonitor implements Runnable{

	@Override
	public void run() {
		while(true){
			System.out.println("WebConfigJobBalancer.allWebRegUrl.size():"+new Date()+"\t"+WebConfigJobBalancer.allWebRegUrl.size());
			System.out.println("WebConfigJobBalancer.webconfigJobQueue.size():"+new Date()+"\t"+WebConfigJobBalancer.webconfigJobQueue.size());
			try {
				Thread.currentThread().sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
