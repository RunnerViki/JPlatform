package com.viki.crawlConfig.crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebConfigSnifferUtil {

	Logger logger = LoggerFactory.getLogger(WebConfigSnifferUtil.class);

	public static String getHostByUrl(String url){
		return WebConfigSnifferUtil.getHostByUrl(url,false);
	}
	
	public static String getHostByUrl(String url,boolean containProtocol){
		if(!containProtocol){
			url = url.replace("http://", "");
		}
		url = url.contains(".net")?url.split(".net")[0]+".net":url;
		url = url.contains(".com")?url.split(".com")[0]+".com":url;
		url = url.contains(".cn")?url.split(".cn")[0]+".cn":url;
		url = url.contains(".org")?url.split(".org")[0]+".org":url;
		return url;
	}
	
	/**
	 * ����������ʽ
	 * @param string
	 * @return
	 */
	public static String getRegExpFromUrl(String sourceUrl){
		String hostName = getHostByUrl(sourceUrl, true);
//		String appendix = sourceUrl.contains(".")?sourceUrl.substring(sourceUrl.lastIndexOf(".")+1):"";
		sourceUrl = sourceUrl.replace(hostName, "");//.replace(appendix, "");
		sourceUrl = sourceUrl.contains("?")?sourceUrl.substring(0,sourceUrl.indexOf("?")):sourceUrl;
		sourceUrl = SimpleRegExpGen.genReg(sourceUrl);
		return hostName+sourceUrl;
	}
	
	
	public static boolean sourceLinksFilter(String url){
		String[] fileTypeIngore = "jpg|jpeg|bmp|png|gif|mp3|rmvb|rm|wmv|avi|mp4|apk|exe".split("\\|");
		for(String type : fileTypeIngore){
			if(url.endsWith(type)){
				return false;
			}
		}
		if(url.contains("javascript")){
			return false;
		}
		if(!url.matches(".*(http|net|com|cn|org).*")){
			return false;
		}
		return true;
	}
	
//	public static void main(String[] args){
//		logger.info(sourceLinksFilter("Video"));
//	}
}
