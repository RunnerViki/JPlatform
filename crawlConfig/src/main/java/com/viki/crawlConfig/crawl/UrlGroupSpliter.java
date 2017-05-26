package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.utils.MapUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


/**
 * ��һ��URL���ҳ�����ͬһ��������ʽ�����Ҹ������ķ���
 * @author vikiyang
 *
 */
public class UrlGroupSpliter {

	private Set<String> docs;

	private HashMap<String,HashSet<String>> groups = new HashMap<String,HashSet<String>>();

	public UrlGroupSpliter(Set<String> docs){
		this.docs = docs;
	}

	public UrlGroupSpliter(){}

	/**
	 * ����������ʽ�����URL���ڵķ���
	 * @return
	 */
	public Iterator<Entry<String, HashSet<String>>> seperateAndGetFirst(){
		for(String str : docs){
			String groupName = WebConfigSnifferUtil.getRegExpFromUrl(str);
			if(!groups.containsKey(groupName)){
				groups.put(groupName, new HashSet<String>());
			}
			groups.get(groupName).add(str);
		}
		//MapUtil.isAsc = false;
		groups.remove("");
		groups.remove(null);
		return MapUtil.sortMapByValue(groups).entrySet().iterator();
	}
	
	public static void main(String[] args){
		UrlGroupSpliter urlGroupSpliter = new UrlGroupSpliter();
		System.out.println(WebConfigSnifferUtil.getRegExpFromUrl("http://kuaixun.stcn.com/2015/0401/12145952.shtml"));
		//urlGroupSpliter.test();
	}
	
	public void test(){
		System.out.println("http://kuaixun.stcn.com/2015/0401/12145952.shtml".matches("http://kuaixun.stcn.com/\\d{4}/\\d{4}/\\d{8}.\\w{5}"));
	}
	
}

