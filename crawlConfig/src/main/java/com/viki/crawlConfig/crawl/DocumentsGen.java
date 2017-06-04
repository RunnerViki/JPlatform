package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.utils.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class DocumentsGen {
	
	private Set<String> targetUrls;

	private Connection conn = ConnectionFactory.getConnection().getValue();
	
	private Integer docCapacity = Constants.CRAWLED_GROUP_SIZE;
	
	public DocumentsGen(Set<String> targetUrls){
		this.targetUrls = targetUrls;
	}
	
	public HashSet<Document> gen(){
		HashSet<Document> targetDocs = new HashSet<Document>();
		int wrongThold = 3;
		for(String str : targetUrls){
			try {
				if(targetDocs.size() >= docCapacity){
					return targetDocs;
				}
				if(StringUtils.isEmpty(str)){
					continue;
				}
				targetDocs.add(conn.url(str).get());
				wrongThold = 3;
			} catch (Exception e) {
				if(wrongThold -- <= 0){
					return targetDocs;
				}
				
				continue;
			}finally {
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return targetDocs;
	}
	
}
