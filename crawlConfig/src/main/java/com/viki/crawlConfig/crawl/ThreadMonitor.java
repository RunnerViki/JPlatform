package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.bean.WebsiteConfig;
import com.viki.crawlConfig.mapper.WebsiteConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class ThreadMonitor implements Runnable{

	Logger logger = LoggerFactory.getLogger(ThreadMonitor.class);

	@Autowired
	WebsiteConfigMapper websiteConfigMapper;

	public ThreadMonitor(WebsiteConfigMapper websiteConfigMapper){
		this.websiteConfigMapper = websiteConfigMapper;
	}

	@Override
	public void run() {
		while(true){
			try {
				logger.info("WebConfigJobBalancer.allWebRegUrl:"+new Date()+"\t"+WebConfigJobBalancer.allWebRegUrl.size());
				logger.info("WebConfigJobBalancer.webconfigJobQueue.size():"+new Date()+"\t"+WebConfigJobBalancer.uncrawledUrlQueue.size());
				if(WebConfigJobBalancer.uncrawledUrlQueue.size() == 0){

					List<WebsiteConfig> rst = websiteConfigMapper.getList(new HashMap<String, Object>());
					HashMap<String, Object> domains = new HashMap<>();
					for(WebsiteConfig websiteConfig : rst){
						domains.put(websiteConfig.getDomain(), "");
					}
					int maxsize = 0;
					for(String itemKey : WebConfigJobBalancer.allWebRegUrl.keySet()){
						try{
							if(WebConfigJobBalancer.allWebRegUrl.get(itemKey).getIsUsed() || WebConfigJobBalancer.allWebRegUrl.get(itemKey).getValue().keySet().size() == 0){
								continue;
							}
							String domainCrawledd= WebConfigSnifferUtil.getHostByUrl(WebConfigJobBalancer.allWebRegUrl.get(itemKey).getValue().keySet().toArray()[0].toString());
							if(domains.containsKey(domainCrawledd)){
								continue;
							}
							maxsize = maxsize > WebConfigJobBalancer.allWebRegUrl.get(itemKey).getValue().size() ? maxsize : WebConfigJobBalancer.allWebRegUrl.get(itemKey).getValue().size();
						}catch (Exception e){
							e.printStackTrace();
						}
					}

					if(maxsize <= 10){
						continue;
					}

					for(String itemKey : WebConfigJobBalancer.allWebRegUrl.keySet()){
						if(WebConfigJobBalancer.allWebRegUrl.get(itemKey).getIsUsed()){
							continue;
						}
						if(WebConfigJobBalancer.allWebRegUrl.get(itemKey).getValue().size() >= maxsize){
							String domainCrawledd= WebConfigSnifferUtil.getHostByUrl(WebConfigJobBalancer.allWebRegUrl.get(itemKey).getValue().keySet().toArray()[0].toString());
							if(domains.containsKey(domainCrawledd)){
								continue;
							}
							if(!WebConfigJobBalancer.uncrawledUrlQueue.contains(WebConfigJobBalancer.allWebRegUrl.get(itemKey))){
								WebConfigJobBalancer.uncrawledUrlQueue.offer(WebConfigJobBalancer.allWebRegUrl.get(itemKey));
							}
						}
					}
				}
				/*logger.info(JSON.toJSONString(Thread.getAllStackTraces()));*/


			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				try {
					Thread.currentThread().sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
