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
	
	private String dataFormatSplitReplacement = "��|��|��|ʱ|��|��|\\p{Punct}|\\s+|\\p{Zs}| ";

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
	 * ��docs�л�ȡ�������ڵ�ַ�ͷ�������ѡ����
	 * @return
	 */
	public String extractPostDate(){
		postdateFormat = extractPostDateFormat();
		if(postdateFormat == null){
			// TODO ���û���õ�CSSʱ����Ҫ����һ��
		}
		//cssSelector = extractPostDateXpath();
		System.out.println(cssSelector);
		return cssSelector;
	}


	boolean gotTarget = false;
	/**
	 * 1���õ�ҳ������Ԫ�ط���ʱ����ַ�����ʽ
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
			if(dateFormat.equals("yyyy��MM��dd��")){
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
	 * ѭ���������е��ı����ҳ�ÿ���ı��ķ����������򣬼���������ÿ�����ı���������ʽ��
	 * �ҳ�������ʽ����һ��entry���϶���entry����ʵ�����������
	 * @return
	 */
	private Iterator<Entry<String, ArrayList<String>>> getPostdateRegAndValueList(){
		List<String> postdateArea;
		ArrayList<String> postDateRegExpList = new ArrayList<String>();
		String postDateFormat = "";
		//ѭ�����doc����
		for(Document doc : docs){

			//��ȡdoc�����к��з������ڵ��ַ���
			postdateArea = extractPostdateListInDoc(doc);

			for(String postDateUnit : postdateArea){
				//����������ַ�����������ʽ�����뵽һ��
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

		//��������������ʽ�����еĵ�һ��Ԫ�أ�keyΪ����������ʽ��valueΪ���з��ϸ�����������б�
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
	 * ��һ�������ַ�������ȡ�����ַ��������ڸ�ʽ�ַ���
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

			//�õ�һ�����滻���滻�����ڵ�λ���ַ���
			String replacementPostdate = replacementTransform(postDate);
			Set<String> dateDigitUnitsInOneIdx = new HashSet<String>();
			Set<String> dateCharUnitsInOneIdx = new HashSet<String>();
			Set<String> dateUnitSet = new HashSet<String>(Arrays.asList("MM","dd"));
			String dateFormatUnit = "";
			String dateUnit = "";
			for(int idx = 0; idx<length; idx++){

				//��ȡ�����������ַ�������ͬ�����ڵ�λ�������е��꣬�������е���
				for(String postdate : postdateList){
					dateUnit = StringUtil.splitIgnoreEmptyString(postdate, dataFormatSplitReplacement)[idx].trim();
					if(dateUnit.length() == 0){
						continue;
					}
					try{
						//�����һ�����֣�����뵽dateUnitsInOneIdx�У��Ա�����������ַ�Χ�²�
						if(dateUnit.matches("\\d+")){
							dateDigitUnitsInOneIdx.add(dateUnit);
						}else{
							dateDigitUnitsInOneIdx.clear();
							break;
						}
					}catch(NumberFormatException e){
						//���dateUnit�������ֶ�������ת��ʱ�����쳣���������Ϊ����һ���ַ��͵�����ֵ�������ռ�������ͬ���ڵ�λ��������Ʋ⣻
						dateCharUnitsInOneIdx.add(dateUnit);
					}
				}
				if(!dateDigitUnitsInOneIdx.isEmpty()){
					//ʹ����ͬ��λ������Ԫ�أ��²���ЩԪ�ؿ��ܵĵ�λ�����꣬�����¡�
					dateFormatUnit = guessDigitDateUnit(dateDigitUnitsInOneIdx, dateUnitSet,replacementPostdate);
					if(dateFormatUnit.isEmpty()){
						return "";
					}
					dateDigitUnitsInOneIdx.clear();
					//���滻����ʹ�ò²⵽�����ڵ�λ�滻��������yyyy��MM��dd��
					replacementPostdate = replacementPostdate.replace("{"+idx+"}", dateFormatUnit);
				}else if(!dateCharUnitsInOneIdx.isEmpty()){
					dateFormatUnit = guessCharDateUnit(dateCharUnitsInOneIdx,replacementPostdate);
					dateCharUnitsInOneIdx.clear();
					//���滻����ʹ�ò²⵽�����ڵ�λ�滻��������yyyy��MM��dd��
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
	 * ������ͬ��λ������ֵ���²��������ʲô���ڵ�λ
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
	 * ʹ���滻���滻�����е����ڵ�λ
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
	 * 1��ʹ��titleXpath��contentXpath�Է������ڶ�λ
	 * 2���õ���λ������ı�����
	 * 3��ȥ���ı����ݵĲ�������֣��õ�һ�����Ӿ�ȷ�Ľ��
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

		//��ȡ���Ľڵ������ڵ����С���ڵ㣬���Ƴ����Ľڵ�����
		String postArea = mainContent.text().replace(content.text(), "").replace(title.text(), "");
		while(mainContent.parent()!= null && (postArea == null || postArea.length()==0)){
			mainContent = mainContent.parent();
			postArea = mainContent.text().replace(content.text(), "").replace(title.text(), "");
		}

		//��ʣ����������ȡ���������ַ���
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
		//p = Pattern.compile("\\d{4}��\\d{2}��\\d{2}��");
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
	 * ��mainContent���ҵ���ƥ����Pattern���ַ�������С�ڵ�
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
		postDateRegExpList.add("2014��3��1��");
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
