package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.bean.Constants;
import com.viki.crawlConfig.utils.MapUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;



public class TitleSniffer {

	private Set<Document> docs;

	private ConcurrentHashMap<String,Integer> titleXpath =  new ConcurrentHashMap<String,Integer>();

	static  Logger logger = LoggerFactory.getLogger(TitleSniffer.class);


	public TitleSniffer(Set<Document> docs){
		this.docs = docs;
	}

	public String extractTitleXPath(){
		List<Future> futureList = new ArrayList<>();
		for(final Document doc : docs){
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					String cssSelectorTemp = "";
					Double cssSelectorDegree = 0.0D;
					String title = "";
					String webpageTitle;
					webpageTitle = doc.title() == null ? "": doc.title();
					webpageTitle = webpageTitle.replaceAll("\\s+", "").replaceAll("\\_|\\-|\\\r", "");
					title = extractTitleXpathByTag(doc,webpageTitle);
					if(title == null){
						erxtractTitleXpathBySimiliarDegree(doc.select("body").first(),webpageTitle, cssSelectorTemp, cssSelectorDegree);
						title = cssSelectorTemp;
					}
					if(!titleXpath.containsKey(title)){
						titleXpath.put(title, 1);
					}else{
						titleXpath.put(title, titleXpath.get(title)+1);
					}
				}
			};
			futureList.add(Constants.executorService.submit(runnable));
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
		if(titleXpath.size() == 0){
			return "";
		}
		if(titleXpath.size() == 0){
			return "";
		}
		return titleXpath.size() > 0 ? MapUtil.sortMapByValue(titleXpath).entrySet().iterator().next().getKey() : "";
	}

	public static void main(String[] args) throws IOException{
		Set<Document> docs = new HashSet<Document>();
		Document doc = Jsoup.connect("http://www.leiphone.com/news/201504/mdlLDik9GNCaRyb8.html").get();
		docs.add(doc);
		TitleSniffer ts = new TitleSniffer(docs);
		logger.info(ts.extractTitleXPath());
		logger.info(doc.select(ts.extractTitleXPath()).text());
		System.out.println("-------");
	}


	public void erxtractTitleXpathBySimiliarDegree(Element ele, String webPageTitle, String cssSelectorTemp, Double cssSelectorDegree){
		Double degree = getSimliarDegree(ele.text(),webPageTitle);
		if(degree >= cssSelectorDegree){
			try{
				cssSelectorTemp = ele.cssSelector();
				cssSelectorDegree = degree;
			}catch(Exception e){
			}
		}
		for(Element e : ele.children()){
			if(e.text().isEmpty()){
				continue;
			}
			erxtractTitleXpathBySimiliarDegree(e,webPageTitle, cssSelectorTemp, cssSelectorDegree);
		}
	}

	private String extractTitleXpathByTag(Element doc, String webPageTitle){
		Elements eles;

		//先判断是否有h1-h6系列标签
		for(String t : new String[]{"h1","h2","h3","h4","h5","h6"}){
			eles = doc.select(t);
			for(Element el : eles){

				//如果元素没有内容，则直接处理下一个元素
				if(el.text() != null && el.text().trim().length() > 0){

					//如果该元素内容与网页标题不存在单向包容，则排除
					if(!el.text().replaceAll("\\s+", "").replaceAll("\\_|\\-|\\\r", "").contains(webPageTitle) &&
							!webPageTitle.contains(el.text().replaceAll("\\s+", "").replaceAll("\\_|\\-|\\\r", ""))){
						continue;
					}
					try{
						return el.cssSelector();
					}catch(Exception e){
						continue;
					}
				}
			}
		}
		return null;
	}

	private Double getSimliarDegree(String sourceA,String sourceB){
		String sa = new String(sourceA);
		String sb = new String(sourceB);
		ArrayList<Character> setA = new ArrayList<Character>();
		for(Character c : sourceA.toCharArray()){
			setA.add(c);
		}
		for(Character c : sourceB.toCharArray()){
			setA.add(c);
		}
		int duplicatedCharCount = 0;
		for(Character c : setA){
			if(sourceA.indexOf(c) >= 0 && sourceB.indexOf(c) >=0){
				sourceA = Pattern.compile(c.toString(),Pattern.LITERAL).matcher(sourceA).replaceFirst("");
				sourceB = Pattern.compile(c.toString(),Pattern.LITERAL).matcher(sourceB).replaceFirst("");
				duplicatedCharCount++;
			}
		}
		return new Double(duplicatedCharCount*2) / (sa.length() + sb.length());

		//return new Double(2*(sourceA.length() + sourceB.length() - setC.size() - (sourceA.length() - setA.size()) - (sourceB.length() - setB.size()))) / (sourceA.length() + sourceB.length());
	}


	private static String enshortCssSelector(Document dodd){
		return null;
	}

	@SuppressWarnings("unused")
	private String normonizeUrl(String targetUrl,String sourceUrl){
		if(targetUrl==null || targetUrl.length() == 0){
			return null;
		}
		if(targetUrl.startsWith("javascript")){
			return null;
		}
		if(!targetUrl.startsWith("http")  ){
			if(!targetUrl.contains(sourceUrl.split("(^/)/(^/)")[0])){
				targetUrl = sourceUrl.split("(^/)/(^/)")[0].concat(targetUrl);
			}else{
				return null;
			}
		}
		return targetUrl.split("#")[0];
	}
}
