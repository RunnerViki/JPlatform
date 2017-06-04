package com.viki.crawlConfig.crawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebConfigSnifferUtil {

	static Logger logger = LoggerFactory.getLogger(WebConfigSnifferUtil.class);

	public static String getHostByUrl(String url){
		return WebConfigSnifferUtil.getHostByUrl(url,false);
	}
	
	public static String getHostByUrl(String url,boolean containProtocol){
		String protocol = "";
		// 先补上http协议头
		if(!url.startsWith("http://") && !url.startsWith("https://")){
			url = "http://" + url;
			protocol = "http://";
		}else if(url.startsWith("http://")){
			protocol = "http://";
		}else if(url.startsWith("https://")){
			protocol = "https://";
		}else{
			logger.error("未知协议");
			return null;
		}
		return containProtocol ? protocol + url.replace(protocol, "").split("/")[0] : url.replace(protocol, "").split("/")[0];
	}
	
	/**
	 * ����������ʽ
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
