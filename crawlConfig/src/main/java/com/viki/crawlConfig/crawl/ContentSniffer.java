package com.viki.crawlConfig.crawl;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import com.viki.crawlConfig.utils.ConnectionFactory;
import com.viki.crawlConfig.utils.MapUtil;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLFetcher;

/*
* 正文提取
* */
public class ContentSniffer {

	private Set<Document> docs;
	
	private HashMap<String,Integer> contentXpathSet =  new HashMap<String,Integer>();
	
	private static String contentXpath = "";
	
	private Connection conn = ConnectionFactory.getConnection().getValue();
	
	public ContentSniffer(Set<Document> docs){
		this.docs = docs;
	}
	public ContentSniffer(){}
	
	
	public String extractContentXpath(){
		String contentX = "";
		for(Document doc : docs){
			contentX = extractContentXpath(doc);
			if(!contentXpathSet.containsKey(contentX)){
				contentXpathSet.put(contentX, 1);
			}else{
				contentXpathSet.put(contentX, contentXpathSet.get(contentX)+1);
			}
		}
		contentXpathSet.remove("");
		if(contentXpathSet.size() == 0){
			return null;
		}
		return MapUtil.sortMapByValue(contentXpathSet).entrySet().iterator().next().getKey();
	}
	
	private String extractContentXpath(Document doc){
		String cssSelector = "";
		TextDocument doct;
		try {
			HTMLDocument htmlDoc = HTMLFetcher.fetch(new URL(doc.baseUri()));
			doct = new BoilerpipeSAXInput(htmlDoc.toInputSource()).getTextDocument();
			String content = ArticleExtractor.INSTANCE.getText(doct);
			cssSelector = getCssSelector(doc,getLongestRow(content));
		} catch (Exception e) {}
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

	public  void getRoughSelector(Element parentEle,String matchedContent){
		for(Element loopEle : parentEle.children()){
			while(loopEle.text().replaceAll("[\\r\\n\\t\\f\\s\\u3000]", "").contains(matchedContent.replaceAll("[\\r\\n\\t\\f\\s\\u3000]", ""))){
				contentXpath = loopEle.cssSelector();
				getRoughSelector(loopEle,matchedContent);
				return;
			}
		}
	}

	public  String getCssSelector(Element parentEle,String matchedContent){
		getRoughSelector(parentEle,matchedContent);
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
		ContentSniffer contentSniffer = new ContentSniffer();
		if(Boolean.parseBoolean("true")){
			System.setProperty("http.proxyHost", "192.168.91.11");
			System.setProperty("http.proxyPort", "80");
		}
		String[] urls = {"http://news.xinhuanet.com/politics/2015-04/07/c_1114893481.htm","http://www.gov.cn/guowuyuan/2015-04/08/content_2843808.htm"
				,"http://www.cankaoxiaoxi.com/roll/roll10/2015/0409/735199.shtml","http://d.youth.cn/tech_focus/201504/t20150409_6569408.html","http://wengengmiao.baijia.baidu.com/article/52893",
				"http://www.thepaper.cn/baidu.jsp?contid=1318908","http://companies.caixin.com/2015-04-08/100798381.html?utm_source=baidu&utm_medium=caixin.media.baidu.com&utm_campaign=Hezuo","http://xinwen.ynet.com/3.1/1504/09/9974540.html",
				"http://news.ifeng.com/a/20150409/43514376_0.shtml","http://www.thepaper.cn/baidu.jsp?contid=1319041","http://insurance.hexun.com/2015-04-09/174795337.html"};
		for(String url : urls){
			String content = contentSniffer.extractContentXpath(url);
			String longestRow = contentSniffer.getLongestRow(content);
			Document doc;
			try {
				doc = ConnectionFactory.getConnection().url(url).get();
				String cssSelector = contentSniffer.getCssSelector(doc,longestRow);
				System.out.println("网页："+url);
				System.out.println("元素选择器："+cssSelector);
				System.out.println("正文"+doc.select(cssSelector).text() +"\n");
			} catch (IOException e) {
				continue;
			}
		}
		
	}
	
	private void test()throws IOException{}
}
