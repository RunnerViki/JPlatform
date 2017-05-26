package com.viki.crawlConfig.bean;

import java.util.concurrent.locks.ReentrantLock;

public class WebsiteConfig {

	private Integer id;
	
	/**
	 * �������
	 */
	private String entranceUrl;

	/**
	 * ������ҳ���xpath
	 */
	private String titleXpath;

	/**
	 * ����������ҳ���xpath
	 */
	private String postdateXpath;

	/**
	 * �������ڵ����ڸ�ʽ
	 */
	private String postdateFormat;

	/**
	 * ����������ҳ���xpath
	 */
	private String contentXpath;

	/**
	 * ����վʹ�õı���
	 */
	private String encoding;

	/**
	 * վ������
	 */
	private String webName;

	private String groupName;

	/**
	 * վ����
	 */
	private String domain;

	/**
	 * ÿ��ȡһ��ҳ��֮�����crawling_interval������ȡ
	 */
	private Integer crawling_interval;

	/**
	 * ������stopSeconds����û�л�ȡ����ҳ�棬��ֹͣ������ȡ
	 */
	private Integer stopSeconds;

	/**
	 * ֻ��url����urlSourceNorm������ʽʱ���ż�¼��url
	 */
	private String urlSourceNorm;

	/**
	 * ֻ��url��Ҫ����urlNorm������ʽʱ���Ż�ȡ��url������
	 */
	private String urlPattern;

	/**
	 * ���url�к���urlREPOrigin,������ȡ��ҳ��ǰ�滻��urlREPReplacement
	 */
	private String urlREPOrigin,urlREPReplacement;

	/**
	 * ���ҳ�治����urlPrefix,�����
	 */
	private String urlPrefix;

	private Integer status;

	/**
	 * ���������Ƿ���Դӵ�ַ�л�ȡ
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
