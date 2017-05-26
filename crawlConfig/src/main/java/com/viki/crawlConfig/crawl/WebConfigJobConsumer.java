package com.viki.crawlConfig.crawl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;

import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.bean.WebsiteConfig;
import com.viki.crawlConfig.mapper.WebsiteConfigMapper;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class WebConfigJobConsumer implements Runnable {

	private String entranceUrl;
	
	private ArrayBlockingQueue<String> webconfigJobQueue;

	public WebConfigJobConsumer(ArrayBlockingQueue<String> webconfigJobQueue) {
			this.webconfigJobQueue = webconfigJobQueue;
	}
	
	public WebConfigJobConsumer(String entranceUrl){
		this.entranceUrl = entranceUrl;
	}

	@Override
	public void run() {
		boolean  isContinue = true;
		while(isContinue){
			try {
				this.entranceUrl = webconfigJobQueue.take();
				if(!WebConfigSnifferUtil.sourceLinksFilter(entranceUrl)){
					System.out.println(entranceUrl);
					continue;
				}
				WebsiteConfig websiteConfig = new WebsiteConfig();
				UrlSniffer urlSniffer = new UrlSniffer(entranceUrl);
				UrlGroupSpliter urlGroupSpliter = new UrlGroupSpliter(urlSniffer.getUrls());
				Iterator<Entry<String, HashSet<String>>> entrys = urlGroupSpliter.seperateAndGetFirst();
				Entry<String, HashSet<String>> entry;
				System.out.println(entrys.hasNext());
				int count = 0;
				while((entry = entrys.next())!=null  && count++ < 4){
					/*if(!entry.getKey().replaceAll("\\s+", "").contains(WebConfigSnifferUtil.getRegExpFromUrl(entranceUrl))){
						continue;
					}*/
					System.out.println(entry.getKey() + "\t---------------\t"+WebConfigSnifferUtil.getRegExpFromUrl(entranceUrl));
					/*if(WebConfigJobBalancer.allWebRegUrl.contains(entry.getKey())){
						System.out.println(entry.getKey());
						continue;
					}*/
					websiteConfig.setUrlPattern(entry.getKey());
					System.out.println("1111111111");
					
					
					DocumentsGen documentsGen = new DocumentsGen(entry.getValue());
					Set<Document> documents = documentsGen.gen();
					System.out.println("22222222222");
					TitleSniffer titleSniffer = new TitleSniffer(documents);
					String titleXpath = titleSniffer.extractTitleXPath();
					System.out.println("titleXpath------------"+titleXpath);
					websiteConfig.setTitleXpath(titleXpath);
					if(titleXpath==null || titleXpath.length() ==0){
						new ErrorNote(entry,entry.getKey(),"titleXpath为空，传入地址为"+entranceUrl).write();
						continue;
					}

					ContentSniffer contentSniffer = new ContentSniffer(documents);
					String contentXpath = contentSniffer.extractContentXpath();
					System.out.println("contentXpath---------------------"+contentXpath);
					websiteConfig.setContentXpath(contentXpath);
					if(contentXpath==null ||contentXpath.length() == 0){
						new ErrorNote(entry,entry.getKey(),"contentXpath为空，传入地址为"+entranceUrl).write();
						continue;
					}


					PostdateSniffer postdateSniffer = new PostdateSniffer(documents, titleXpath, contentXpath);

					String postdateXpath = postdateSniffer.extractPostDate();
					System.out.println("postdateXpath------------------------"+postdateXpath);

					if(postdateXpath == null || postdateXpath.length() == 0 ){
						postdateXpath = postdateSniffer.extractPostDate();
						new ErrorNote(entry,entry.getKey(),"postdateXpath为空，传入地址为"+entranceUrl).write();
						continue;
					}
					websiteConfig.setPostdateXpath(postdateXpath);
					websiteConfig.setPostdateFormat(postdateSniffer.getPostdateFormat());
					System.out.println("PostdateFormat--------------------"+postdateSniffer.getPostdateFormat());
					if(postdateSniffer.getPostdateFormat() == null || postdateSniffer.getPostdateFormat().length() == 0 ){
						new ErrorNote(entry,entry.getKey(),"postdateSniffer.getPostdateFormat为空，传入地址为"+entranceUrl).write();
						continue;
					}

					websiteConfig.setEntranceUrl(WebConfigSnifferUtil.getHostByUrl(entranceUrl,true));
					websiteConfig.setDomain(WebConfigSnifferUtil.getHostByUrl(entranceUrl));
					websiteConfig.setEncoding(documents.iterator().next().outputSettings().charset().name());
					websiteConfig.setWebName("");
					websiteConfig.setGroupName("");
					websiteConfig.setCrawling_interval(3000);
					websiteConfig.setStopSeconds(600);
					websiteConfig.setUrlSourceNorm("");
					websiteConfig.setUrlREPOrigin("");
					websiteConfig.setUrlREPReplacement("");
					websiteConfig.setUrlPrefix("");
					websiteConfig.setStatus(0);

					 String[] paths = new String[] {"file:G:\\Workspace\\BigRazor\\WebRoot\\WEB-INF\\applicationContext-mybatis.xml",
						"file:G:\\Workspace\\BigRazor\\WebRoot\\WEB-INF\\applicationContext.xml"};
					 ApplicationContext ctx = new ClassPathXmlApplicationContext(paths);
					 Constants.ctx = ctx;
					 try {
						websiteConfigMapper.insert(websiteConfig);
						System.out.println("插入一个爬取配置到数据库");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			} catch (Exception e1) {
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
		}
		//this.run();
	}

	@Autowired
	WebsiteConfigMapper websiteConfigMapper;

}
