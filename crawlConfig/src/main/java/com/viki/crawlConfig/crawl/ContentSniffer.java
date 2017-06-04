package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.utils.ConnectionFactory;
import com.viki.crawlConfig.utils.MapUtil;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/*
* 正文提取
* */
public class ContentSniffer {

	static Logger logger = LoggerFactory.getLogger(ContentSniffer.class);

	private Set<Document> docs;

	private ConcurrentHashMap<String,Integer> contentXpathSet =  new ConcurrentHashMap<String,Integer>();

//	private static String contentXpath = "";

	private Connection conn = ConnectionFactory.getConnection().getValue();

	public ContentSniffer(Set<Document> docs){
		this.docs = docs;
	}
	public ContentSniffer(){}


	public String extractContentXpath(){
		List<Future> futureList = new ArrayList<>();
		for(final Document doc : docs){
			futureList.add(Constants.executorService.submit(new Runnable() {
				@Override
				public void run() {
					String contentX = "";
					contentX = extractContentXpath(doc);
					if(!contentXpathSet.containsKey(contentX)){
						contentXpathSet.put(contentX, 1);
					}else{
						contentXpathSet.put(contentX, contentXpathSet.get(contentX)+1);
					}
				}
			}));
		}
		for(Future future : futureList){
			try {
				future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		contentXpathSet.remove("");
		if(contentXpathSet.size() == 0){
			return "";
		}
		return contentXpathSet.size() > 0 ? MapUtil.sortMapByValue(contentXpathSet).entrySet().iterator().next().getKey() : "";
	}

	public String extractContentXpath(Document doc){
		String cssSelector = "";
		TextDocument doct;
		try {
//			TimeUnit.SECONDS.sleep(5);
//			HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(doc.baseUri()));
			HTMLDocument htmlDoc = new HTMLDocument(doc.html().getBytes(), doc.charset());
			doct = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
			String content = ArticleExtractor.INSTANCE.getText(doc.html());
			cssSelector = getCssSelector(doc,getLongestRow(content));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cssSelector;
	}

	/**
	 * 根据某个确定的地址，获取其最有可能的正文cssSelector
	 * @param url
	 * @return
	 */
	private String extractContentXpath(String url){
		String cssSelector = "";
		try {
			HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(url));
			TextDocument doct;
			doct = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
			String content = ArticleExtractor.INSTANCE.getText(doct);
			Document doc = conn.url(url).get();
			cssSelector = getCssSelector(doc,getLongestRow(content));
			return content;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cssSelector;
	}

	private  String getLongestRow(String content){
		String longestRow = "";
		for(String row : content.split("\\n")){
			longestRow = row.length() > longestRow.length() ? row: longestRow;
		}
		return longestRow;
	}

	public  void getRoughSelector(Element parentEle,String matchedContent, StringBuilder contentXpath2){
		String contentXpath = "";
		String xx;
		for(Element loopEle : parentEle.children()){
			if(loopEle.text().replaceAll("[\\r\\n\\t\\f\\s\\u3000]", "").contains(matchedContent.replaceAll("[\\r\\n\\t\\f\\s\\u3000]", ""))){
				contentXpath2 = StringUtils.isNotBlank(loopEle.cssSelector()) ? new StringBuilder(loopEle.cssSelector()) : contentXpath2;
				contentXpathLocal.set(contentXpath2.toString());
				getRoughSelector(loopEle,matchedContent, contentXpath2);
			}
		}
	}

	ThreadLocal<String> contentXpathLocal = new ThreadLocal<>();

	public  String getCssSelector(Element parentEle,String matchedContent){
		StringBuilder contentXpathBuilder = new StringBuilder("");
		getRoughSelector(parentEle,matchedContent, contentXpathBuilder);
		String contentXpath = contentXpathLocal.get().toString();
		if(contentXpath != null){
			while(contentXpath.contains(">")){
				if(contentXpath.split(">")[contentXpath.split(">").length-1].contains("#")){
					contentXpath = contentXpath.split(">")[contentXpath.split(">").length-1];
				}else if(contentXpath.split(">")[contentXpath.split(">").length-1].contains(".")){
					contentXpath = contentXpath.split(">")[contentXpath.split(">").length-1];
				}else{
					contentXpath = contentXpath.substring(0, contentXpath.lastIndexOf(">"));
				}
			}
			return contentXpath.trim();
		}
		return "";
	}


	public static void main(String[] args) {
		if(Boolean.parseBoolean("true")){
			System.setProperty("http.proxyHost", "192.168.91.11");
			System.setProperty("http.proxyPort", "80");
		}
		String[] urls = {"http://news.xinhuanet.com/politics/2015-04/07/c_1114893481.htm",
				"http://www.gov.cn/guowuyuan/2015-04/08/content_2843808.htm"};
		HashSet<String> urlSet = new HashSet<>();
		for(String url : urls){
			urlSet.add(url);
		}
		DocumentsGen documentsGen = new DocumentsGen(urlSet);
		Set<Document> documents = documentsGen.gen();
		ContentSniffer contentSniffer = new ContentSniffer(documents);
		contentSniffer.extractContentXpath();

	}

	private void test()throws IOException{}
}
