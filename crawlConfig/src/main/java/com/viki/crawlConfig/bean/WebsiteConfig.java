package com.viki.crawlConfig.bean;

import java.util.concurrent.locks.ReentrantLock;

public class WebsiteConfig {

	private Integer id;
	
	/**
	 * 入口链接
	 */
	private String entranceUrl;

	/**
	 * 标题在页面的xpath
	 */
	private String titleXpath;

	/**
	 * 发表日期在页面的xpath
	 */
	private String postdateXpath;

	/**
	 * 发表日期的日期格式
	 */
	private String postdateFormat;

	/**
	 * 正文内容在页面的xpath
	 */
	private String contentXpath;

	/**
	 * 该网站使用的编码
	 */
	private String encoding;

	/**
	 * 站点名称
	 */
	private String webName;

	private String groupName;

	/**
	 * 站点域
	 */
	private String domain;

	/**
	 * 每爬取一个页面之后，相隔crawling_interval秒再爬取
	 */
	private Integer crawling_interval;

	/**
	 * 如果最近stopSeconds秒内没有获取到新页面，则停止继续爬取
	 */
	private Integer stopSeconds;

	/**
	 * 只有url符合urlSourceNorm正则表达式时，才记录该url
	 */
	private String urlSourceNorm;

	/**
	 * 只有url需要符合urlNorm正则表达式时，才获取该url的内容
	 */
	private String urlPattern;

	/**
	 * 如果url中含有urlREPOrigin,则在爬取该页面前替换成urlREPReplacement
	 */
	private String urlREPOrigin,urlREPReplacement;

	/**
	 * 如果页面不含有urlPrefix,则添加
	 */
	private String urlPrefix;

	private Integer status;

	/**
	 * 发表日期是否可以从地址中获取
	 */
	private Integer postDate_is_from_url;

	private Integer crawlDepth;
	
	public ReentrantLock selfDefineLock = new ReentrantLock();
	
	public Integer getCrawlDepth() {
		return crawlDepth;
	}

	public void setCrawlDepth(Integer crawlDepth) {
		this.crawlDepth = crawlDepth;
	}

	public Integer getPostDate_is_from_url() {
		return postDate_is_from_url;
	}

	public void setPostDate_is_from_url(Integer postDateIsFromUrl) {
		postDate_is_from_url = postDateIsFromUrl;
	}

	public String getEntranceUrl() {
		return entranceUrl;
	}

	public void setEntranceUrl(String entranceUrl) {
		this.entranceUrl = entranceUrl;
	}

	public String getTitleXpath() {
		return titleXpath;
	}

	public void setTitleXpath(String titleXpath) {
		this.titleXpath = titleXpath;
	}

	public String getPostdateXpath() {
		return postdateXpath;
	}

	public void setPostdateXpath(String postdateXpath) {
		this.postdateXpath = postdateXpath;
	}

	public String getPostdateFormat() {
		return postdateFormat;
	}

	public void setPostdateFormat(String postdateFormat) {
		this.postdateFormat = postdateFormat;
	}

	public String getContentXpath() {
		return contentXpath;
	}

	public void setContentXpath(String contentXpath) {
		this.contentXpath = contentXpath;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Integer getCrawling_interval() {
		return crawling_interval;
	}

	public void setCrawling_interval(Integer crawling_interval) {
		this.crawling_interval = crawling_interval;
	}

	public Integer getStopSeconds() {
		return stopSeconds;
	}

	public void setStopSeconds(Integer stopSeconds) {
		this.stopSeconds = stopSeconds;
	}

	public String getUrlSourceNorm() {
		return urlSourceNorm;
	}

	public void setUrlSourceNorm(String urlSourceNorm) {
		this.urlSourceNorm = urlSourceNorm;
	}

	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}

	public String getUrlREPOrigin() {
		return urlREPOrigin;
	}

	public void setUrlREPOrigin(String urlREPOrigin) {
		this.urlREPOrigin = urlREPOrigin;
	}

	public String getUrlREPReplacement() {
		return urlREPReplacement;
	}

	public void setUrlREPReplacement(String urlREPReplacement) {
		this.urlREPReplacement = urlREPReplacement;
	}

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public boolean equals(WebsiteConfig websiteConfig){
		return this.domain!= null && this.entranceUrl != null && this.domain.equals(websiteConfig.getDomain()) && this.entranceUrl.equals(websiteConfig.getEntranceUrl());
	}
}
