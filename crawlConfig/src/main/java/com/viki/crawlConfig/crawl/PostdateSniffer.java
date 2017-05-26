package com.viki.crawlConfig.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.viki.crawlConfig.utils.MapUtil;
import com.viki.crawlConfig.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * @author vikiyang
 *
 */
public class PostdateSniffer {
	
	private String contentXpath;
	
	private String titleXpath;
	
	private HashMap<String,Integer> postdateXpathSet =  new HashMap<String,Integer>();
	
	private HashMap<String,ArrayList<String>> postdateRegSet = new HashMap<String,ArrayList<String>>();
	
	private String dataFormatSplitReplacement = "年|月|日|时|分|秒|\\p{Punct}|\\s+|\\p{Zs}| ";

	private String postdateFormat = "";

	private String cssSelector = "";

	private ArrayList<Document> docs;

	private ArrayList<Element> mainContents = new ArrayList<Element>();

	private String postdateRegExp;

	public PostdateSniffer(Set<Document> docs,String titleXpath,String contentXpath){
		this.docs = new ArrayList<Document>(docs);
		this.titleXpath = titleXpath;
		this.contentXpath = contentXpath;
	}

	/**
	 * 从docs中获取发表日期地址和发表日期选择器
	 * @return
	 */
	public String extractPostDate(){
		postdateFormat = extractPostDateFormat();
		if(postdateFormat == null){
			// TODO 如果没有拿到CSS时，需要再拿一次
		}
		//cssSelector = extractPostDateXpath();
		System.out.println(cssSelector);
		return cssSelector;
	}


	boolean gotTarget = false;
	/**
	 * 1、得到页面所在元素发表时间的字符串格式
	 * @return
	 */
	private String extractPostDateFormat(){
		Iterator<Entry<String, ArrayList<String>>> entryIterator = getPostdateRegAndValueList();
		Entry<String, ArrayList<String>> entry;
		String dateFormat = "";
		while(entryIterator.hasNext()){
			entry = entryIterator.next();
			//postdateRegExp = entry.getKey();
			/*postdateList = entry.getValue();
			dateFormat = getDateFormat(postdateList);
			dateFormat = dateFormat.replaceAll("\\p{Zs}", " ").trim();*/
			dateFormat = entry.getKey();
			if(dateFormat.isEmpty()){
				continue;
			}
			if(dateFormat.equals("yyyy年MM月dd日")){
				System.out.println("fffff");
			}
			postdateRegExp = SimpleRegExpGen.genRegByDateFormat(dateFormat);
			cssSelector = extractPostDateXpath();
			if(cssSelector == null || cssSelector.isEmpty() ){
				continue;
			}else{
				p = Pattern.compile(postdateRegExp);
				boolean isContinue = false;
				for(Document docT : this.docs){
					Elements eles  = docT.select(cssSelector);
					if(eles != null && eles.size() > 0 ){
						Element e = eles.first();
						if(!p.matcher(e.text()).find()){
							isContinue = true;
							break;
						}
					}
				}
				if(isContinue){
					continue;
				}else{
					break;
				}
			}
			/*if(dateFormat != null && dateFormat.length() > 0 ){
				if(longestDateFormat.length() == 0){
					longestDateFormat = dateFormat;
				}
				longestDateFormat = longestDateFormat.length() > dateFormat.length() ? longestDateFormat:dateFormat;
				postdateRegExp = longestDateFormat.length() > dateFormat.length() ? postdateRegExp : entry.getKey();
				//System.out.println(longestDateFormat + "\n"+postdateRegExp+"\n\n\n");
			}*/
		}
		//System.out.println(postdateRegExp+"\n"+longestDateFormat+"\n\n\n\n");
		return dateFormat;
	}

	/**
	 * 循环遍历所有的文本，找出每个文本的发表日期区域，计算区域中每个子文本的正则表达式，
	 * 找出正则表达式最多的一个entry，认定该entry与真实发表日期相关
	 * @return
	 */
	private Iterator<Entry<String, ArrayList<String>>> getPostdateRegAndValueList(){
		List<String> postdateArea;
		ArrayList<String> postDateRegExpList = new ArrayList<String>();
		String postDateFormat = "";
		//循环多个doc对象
		for(Document doc : docs){

			//提取doc对象中含有发表日期的字符串
			postdateArea = extractPostdateListInDoc(doc);

			for(String postDateUnit : postdateArea){
				//计算该日期字符串的正则表达式，加入到一个
				//TODO postDateRegExp = SimpleRegExpGen.genReg(postDateUnit);
				postDateRegExpList.clear();
				postDateRegExpList.add(postDateUnit);
				postDateFormat = getDateFormat(postDateRegExpList);
				if(postdateRegSet.containsKey(postDateFormat)){
					postdateRegSet.get(postDateFormat).add(postDateUnit);
				}else{
					ArrayList<String> postDateUnitSets = new ArrayList<String>();
					postDateUnitSets.add(postDateUnit);
					postdateRegSet.put(postDateFormat, postDateUnitSets);
				}
				/*if(postdateRegSet.containsKey(postDateRegExp)){
					postdateRegSet.get(postDateRegExp).add(postDateUnit);
				}else{
					ArrayList<String> postDateUnitSets = new ArrayList<String>();
					postDateUnitSets.add(postDateUnit);
					postdateRegSet.put(postDateRegExp, postDateUnitSets);
				}*/
			}
		}

		//返回日期正则表达式集合中的第一个元素，key为日期正则表达式，value为所有符合该正则的日期列表
		return MapUtil.sortMapByValue(postdateRegSet).entrySet().iterator();
	}

/*	private Iterator<Entry<String, ArrayList<String>>> getPostdateRegAndValueList2(){
		  HashSet<String> common = null;
		  HashSet<String> remainedMap = new HashSet<String>();
		  for(HashMap<String,String> tmp : configurations){
		   if(common == null){
		    common = new HashSet<String>(tmp.keySet());
		   }else{
		    for(String key : common){
		     if(!tmp.containsKey(key)){
		      remainedMap.add(key);
		     }
		    }
		    common.removeAll(remainedMap);
		   }
		  }
		  return common;
	} */

	/**
	 * 从一批日期字符串中提取这批字符串的日期格式字符串
	 * @param postdateList
	 * @return
	 */
	private String getDateFormat(ArrayList<String> postdateList){
		HashMap<String,Integer> replacementPostdateMap = new HashMap<String,Integer>();
		String postDate;
		if((postDate = postdateList.iterator().next())!= null){
			int length = StringUtil.splitIgnoreEmptyString(postDate, dataFormatSplitReplacement).length;
			if(length == 1 ){
				return "";
			}

			//得到一个被替换符替换掉日期单位的字符串
			String replacementPostdate = replacementTransform(postDate);
			Set<String> dateDigitUnitsInOneIdx = new HashSet<String>();
			Set<String> dateCharUnitsInOneIdx = new HashSet<String>();
			Set<String> dateUnitSet = new HashSet<String>(Arrays.asList("MM","dd"));
			String dateFormatUnit = "";
			String dateUnit = "";
			for(int idx = 0; idx<length; idx++){

				//获取到这批日期字符串中相同的日期单位，如所有的年，或者所有的月
				for(String postdate : postdateList){
					dateUnit = StringUtil.splitIgnoreEmptyString(postdate, dataFormatSplitReplacement)[idx].trim();
					if(dateUnit.length() == 0){
						continue;
					}
					try{
						//如果是一个数字，则加入到dateUnitsInOneIdx中，以便后面利用数字范围猜测
						if(dateUnit.matches("\\d+")){
							dateDigitUnitsInOneIdx.add(dateUnit);
						}else{
							dateDigitUnitsInOneIdx.clear();
							break;
						}
					}catch(NumberFormatException e){
						//如果dateUnit不是数字而导致在转换时发生异常，则可以认为它是一个字符型的日期值，则在收集所有相同日期单位后做穷举推测；
						dateCharUnitsInOneIdx.add(dateUnit);
					}
				}
				if(!dateDigitUnitsInOneIdx.isEmpty()){
					//使用相同单位的日期元素，猜测这些元素可能的单位，是年，还是月。
					dateFormatUnit = guessDigitDateUnit(dateDigitUnitsInOneIdx, dateUnitSet,replacementPostdate);
					if(dateFormatUnit.isEmpty()){
						return "";
					}
					dateDigitUnitsInOneIdx.clear();
					//把替换符再使用猜测到的日期单位替换回来，如yyyy年MM月dd日
					replacementPostdate = replacementPostdate.replace("{"+idx+"}", dateFormatUnit);
				}else if(!dateCharUnitsInOneIdx.isEmpty()){
					dateFormatUnit = guessCharDateUnit(dateCharUnitsInOneIdx,replacementPostdate);
					dateCharUnitsInOneIdx.clear();
					//把替换符再使用猜测到的日期单位替换回来，如yyyy年MM月dd日
					replacementPostdate = replacementPostdate.replace("{"+idx+"}", dateFormatUnit);
				}
			}
			replacementPostdateMap.put(replacementPostdate.trim(), replacementPostdateMap.containsKey(replacementPostdate.trim())?replacementPostdateMap.get(replacementPostdate.trim())+1:1);
		}
		if(replacementPostdateMap.size() == 0){
			return null;
		}
		return MapUtil.sortMapByValue(replacementPostdateMap).entrySet().iterator().next().getKey();
	}

	/**
	 * 利用相同单位的日期值，猜测出可能是什么日期单位
	 * @param dataUnits
	 * @param dateUnitSet
	 * @return
	 */
	private String guessDigitDateUnit(Set<String> dataUnits,Set<String> dateUnitSet,String replacementPostdate){
		String dataFormatUnit = "";
		int maxValue = 0;
		int minValue = 0;
		for(String str : dataUnits){
			if(str.length() == 4 ){
				if( !replacementPostdate.contains("yyyy")){
					dataFormatUnit = "yyyy";
					break;
				}else{
					replacementPostdate = "";
					return "";
				}
			}
			try{
			int value = Integer.parseInt(str);
			maxValue = maxValue==0?value:maxValue;
			maxValue = maxValue>value?maxValue:value;
			minValue = minValue==0?value:minValue;
			minValue = minValue<value?minValue:value;
			}catch(Exception e){
				return "";
			}
		}
		if(dataFormatUnit.isEmpty()){
			if(maxValue <=12 && minValue >=1 && !replacementPostdate.contains("MM")){
				dataFormatUnit = "MM";
			}
			else if(maxValue <=31 && minValue >=1 && !replacementPostdate.contains("dd")){
				dataFormatUnit = "dd";
			}
			else if(maxValue <=24 && minValue >=0 && !replacementPostdate.contains("hh")){
				dataFormatUnit = "hh";
			}
			else if(maxValue <=60 && minValue >=0){
				if(!replacementPostdate.contains("mm")){
					dataFormatUnit = "mm";
				}else{
					dataFormatUnit = "ss";
				}
			/*}else if(dataFormatUnit.isEmpty() && !replacementPostdate.contains("yy")){
				dataFormatUnit = "yy";*/
			}
		}
		dateUnitSet.remove(dataFormatUnit);
		return dataFormatUnit;
	}

	private String guessCharDateUnit(Set<String> dataUnits,String postDate){
		Set<String> testingResult = new HashSet<String>(dataUnits);
		testingResult.removeAll(DateTimeFormatGen.months_MMM_CHINA);
		if(testingResult.isEmpty()){
			return "MMM";
		}

		testingResult = new HashSet<String>(dataUnits);
		testingResult.removeAll(DateTimeFormatGen.months_MMM);
		if(testingResult.isEmpty()){
			return "MMM";
		}

		testingResult = new HashSet<String>(dataUnits);
		testingResult.removeAll(DateTimeFormatGen.months_MMMMM);
		if(testingResult.isEmpty()){
			return "MMMMM";
		}

		testingResult = new HashSet<String>(dataUnits);
		testingResult.removeAll(DateTimeFormatGen.AMPM);
		if(testingResult.isEmpty()){
			return "AM";
		}

		testingResult = new HashSet<String>(dataUnits);
		testingResult.removeAll(DateTimeFormatGen.weekdays_EEE);
		if(testingResult.isEmpty()){
			return "EEE";
		}
		testingResult = new HashSet<String>(dataUnits);
		testingResult.removeAll(DateTimeFormatGen.weekdays_EEEEE);
		if(testingResult.isEmpty()){
			return "EEEEE";
		}
		testingResult = new HashSet<String>(dataUnits);
		testingResult.removeAll(DateTimeFormatGen.weekdays_EEE_CHINA);
		if(testingResult.isEmpty()){
			return "EEE";
		}
		return "";
	}

	/**
	 * 使用替换符替换掉所有的日期单位
	 * @param postdate
	 * @return
	 */
	private String replacementTransform(String postdate){
		int idx = 0;
		for(String str: postdate.split(dataFormatSplitReplacement)){
			if(str != null && str.trim().length() > 0){
				postdate = postdate.replaceFirst("(?<!\\{)"+str.trim(), "{"+idx+++"}");
			}
		}
		return postdate;
	}

	/**
	 * 1、使用titleXpath和contentXpath对发表日期定位
	 * 2、得到定位区域的文本内容
	 * 3、去掉文本内容的不相关文字，得到一个更加精确的结果
	 * @param doc
	 * @return
	 */
	private List<String> extractPostdateListInDoc(Document doc){
		Element title = doc.select(titleXpath).first();
		//System.out.println(title);

		Element content = doc.select(contentXpath).first();
		if(title == null || content == null){
			return new ArrayList<String>();
		}
		Element mainContent = content;
		while(!mainContent.html().contains(title.html())){
			mainContent = mainContent.parent();
		}

		//提取正文节点与标题节点的最小父节点，并移除正文节点内容
		String postArea = mainContent.text().replace(content.text(), "").replace(title.text(), "");
		while(mainContent.parent()!= null && (postArea == null || postArea.length()==0)){
			mainContent = mainContent.parent();
			postArea = mainContent.text().replace(content.text(), "").replace(title.text(), "");
		}

		//从剩下内容中提取符合日期字符串
		List<String> postdateUnits = DateTimeFormatGen.postdateExtraction(postArea);
		while(mainContent.parent()!= null && (postdateUnits == null || postdateUnits.size() == 0)){
			mainContent = mainContent.parent();
			postArea = mainContent.text().replace(content.text(), "").replace(title.text(), "");
			postdateUnits = DateTimeFormatGen.postdateExtraction(postArea);
		}
		mainContents.add(mainContent);
		return postdateUnits;
	}




	public String extractPostDateXpath(){
		String postdateX = "";
		if(postdateRegExp == null ||postdateRegExp.length() == 0 ){
			return postdateX;
		}
		p = Pattern.compile(postdateRegExp,Pattern.LITERAL);
		//p = Pattern.compile("\\d{4}年\\d{2}月\\d{2}日");
		for(Element mainContent : mainContents){
			postdateX = extractPostDateXpath(mainContent,p);
			if(!postdateXpathSet.containsKey(postdateX)){
				postdateXpathSet.put(postdateX, 1);
			}else{
				postdateXpathSet.put(postdateX, postdateXpathSet.get(postdateX)+1);
			}
		}
		return MapUtil.sortMapByValue(postdateXpathSet).entrySet().iterator().next().getKey();
	}


	private Pattern p;
	/**
	 * 在mainContent中找到能匹配上Pattern的字符串的最小节点
	 * @param mainContent
	 * @return
	 */
	private String extractPostDateXpath(Element mainContent,Pattern pattern){
		if(mainContent.children().size() == 0){
			try{
				return mainContent.cssSelector();
			}catch(Exception e){
				return "";
			}

		}
		for(Element ele : mainContent.children()){
			if(p.matcher(ele.text()).find()){
				return extractPostDateXpath(ele,p);
			}
		}
		return mainContent.cssSelector();
	}



	public static void main(String[] args) throws IOException{
		HashSet<Document> docs = new HashSet<Document>();
		Document doc;// = Jsoup.connect("http://insurance.hexun.com/2015-04-09/174795337.html").get();
		/*docs.add(doc);
		doc = Jsoup.connect("http://insurance.hexun.com/2015-04-09/174804040.html").get();
		docs.add(doc);
		doc = Jsoup.connect("http://insurance.hexun.com/2015-04-09/174794074.html").get();
		docs.add(doc);
		doc = Jsoup.connect("http://insurance.hexun.com/2015-04-08/174770642.html").get();
		docs.add(doc);
		doc = Jsoup.connect("http://insurance.hexun.com/2015-04-08/174767269.html").get();
		docs.add(doc);
		doc = Jsoup.connect("http://insurance.hexun.com/2015-03-13/174025753.html").get();
		docs.add(doc);
		doc = Jsoup.connect("http://insurance.hexun.com/2015-03-20/174248308.html").get();
		docs.add(doc);
		doc = Jsoup.connect("http://insurance.hexun.com/2015-04-09/174813934.html").get();
		docs.add(doc);
		doc = Jsoup.connect("http://insurance.hexun.com/2015-04-09/174802927.html").get();
		docs.add(doc);*/
		doc = Jsoup.connect("http://www.chinairn.com/news/20140127/113208333.html").timeout(10000).get();
		docs.add(doc);
		PostdateSniffer postdateSniffer = new PostdateSniffer(docs,".newtittle > h1",".newscon");
		//postdateSniffer.extractPostDate();
		ArrayList<String> postDateRegExpList = new ArrayList<String>();
		postDateRegExpList.add("2014年3月1日");
		System.out.println(postdateSniffer.getDateFormat(postDateRegExpList));
		//postdateSniffer.extractPostdateListInDoc(doc);
	}

	public String getPostdateFormat() {
		return postdateFormat;
	}

	public String getPostdateRegExp() {
		return postdateRegExp;
	}

	
}
