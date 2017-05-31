package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.utils.ConnectionFactory;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class DocumentsGen {
	
	private Set<String> targetUrls;

	private Connection conn = ConnectionFactory.getConnection().getValue();
	
	private Integer docCapacity = 50;
	
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
				targetDocs.add(conn.url(str).get());
				wrongThold = 3;
			} catch (IOException e) {
				if(wrongThold -- <= 0){
					return targetDocs;
				}
				
				continue;
			}
		}
		return targetDocs;
	}
	
}
