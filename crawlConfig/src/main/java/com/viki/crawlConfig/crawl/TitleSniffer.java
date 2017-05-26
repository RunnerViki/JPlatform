package com.viki.crawlConfig.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.viki.crawlConfig.utils.MapUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class TitleSniffer {

	private Set<Document> docs;
	
	private HashMap<String,Integer> titleXpath =  new HashMap<String,Integer>();
	
	public TitleSniffer(Set<Document> docs){
		this.docs = docs;
	}
	
	public String extractTitleXPath(){
		String title = "";
		String webpageTitle;
		for(Document doc : docs){
			cssSelectorDegree = 0.00;
			cssSelectorTemp = "";
			webpageTitle = doc.title() == null ? "": doc.title();
			webpageTitle = webpageTitle.replaceAll("\\s+", "").replaceAll("\\_|\\-|\\\r", "");
			title = extractTitleXpathByTag(doc,webpageTitle);
			if(title == null){
				erxtractTitleXpathBySimiliarDegree(doc.select("body").first(),webpageTitle);
				title = cssSelectorTemp;
			}
			if(!titleXpath.containsKey(title)){
				titleXpath.put(title, 1);
			}else{
				titleXpath.put(title, titleXpath.get(title)+1);
			}
		}
		if(titleXpath.size() == 0){
			return null;
		}
		titleXpath.remove(null);
		if(titleXpath.size() == 0){
			return null;
		}
		return MapUtil.sortMapByValue(titleXpath).entrySet().iterator().next().getKey();
	}
	
	public static void main(String[] args) throws IOException{
		Set<Document> docs = new HashSet<Document>();
		Document doc = Jsoup.connect("http://www.leiphone.com/news/201504/mdlLDik9GNCaRyb8.html").get();
		docs.add(doc);
		TitleSniffer ts = new TitleSniffer(docs);
		System.out.println(ts.extractTitleXPath());
		System.out.println(doc.select(ts.extractTitleXPath()).text());
		
	}
	
	private String cssSelectorTemp = "";
	private Double cssSelectorDegree = 0.00;
	private void erxtractTitleXpathBySimiliarDegree(Element ele, String webPageTitle){
		double degree = getSimliarDegree(ele.text(),webPageTitle);
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
			erxtractTitleXpathBySimiliarDegree(e,webPageTitle);
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
