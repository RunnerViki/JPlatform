package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.utils.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
* ���ڸ�ʽ��ȡ
* */
public class DateTimeFormatGen {
	
	 
	public static final List<String> months_MMMMM = Arrays.asList(new String[]{
			"January","February","March","April","May","June"
			,"July","August","September","October","November","December"});
	public static final List<String> months_MMM = Arrays.asList(new String[]{
			"Jan","Feb","Mar","Apr","May","Jun"
			,"Jul","Aug","Sep","Oct","Nov","Dec"});
	public static final List<String> months_MMM_CHINA = Arrays.asList(new String[]{
			"һ��","����","����","����","����","����"
			,"����","����","����","ʮ��","ʮһ��","ʮ����"});

	public static final List<String> AMPM = Arrays.asList(new String[]{"am","pm"});

	public static final List<String> weekdays_EEE = Arrays.asList(new String[]{
			"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"});
	public static final List<String> weekdays_EEEEE = Arrays.asList(new String[]{
			"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"});
	public static final List<String> weekdays_EEE_CHINA = Arrays.asList(new String[]{
			"����һ","���ڶ�","������","������","������","������","������"});
	public static final List<String> timeUnit_Chinese = Arrays.asList(new String[]{"��","��","��","ʱ","��","��"});

	public static final List<String> timeUnit_Chinese_test = Arrays.asList(new String[]{"\\d+��","\\d+��","\\d+��","\\d+ʱ","\\d+��","\\d+��"});

	/*private static String postdateExtractReg = "(((\\d{2}|\\d{4})(?<=\\d+))(һ��|����|����|����|����|����|����|����|����|ʮ��|ʮһ��|ʮ����|Jan(uary)?|"
        + "Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?|"
        + "am|pm|MON(DAY)?|TUE(SDAY)?|WED(NESDAY)?|THU(RSDAY)?|FRI(DAY)?|SAT(URDAY)?|SUN(DAY)?|"
        + "����[һ������������]|��|��|��|ʱ|��|��|\\p{Punct}| )*)+";*/

	//2015-04-18:���\\p{Zs}��֧��ȫ�ǿո�
	/*private static String postdateExtractReg = "(((\\d{1}|\\d{2}|\\d{4})(?<=\\d))" +
			"(һ��|����|����|����|����|����|����|����|����|ʮ��|ʮһ��|ʮ����|" +
			"Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?|"
        + "am|pm|"
        + "MON(DAY)?|TUE(SDAY)?|WED(NESDAY)?|THU(RSDAY)?|FRI(DAY)?|SAT(URDAY)?|SUN(DAY)?|"
        + "����[һ������������]|��|��|��|ʱ|��|��|\\p{Punct}| |\\s|\\p{Zs})*)+";*/

	private static String postdateExtractReg = "(((\\d{1}|\\d{2}|\\d{4})(?<=\\d)" +
			"|һ��|����|����|����|����|����|����|����|����|ʮ��|ʮһ��|ʮ����|" +
			"Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?|"
		+ "am|pm|"
		+ "MON(DAY)?|TUE(SDAY)?|WED(NESDAY)?|THU(RSDAY)?|FRI(DAY)?|SAT(URDAY)?|SUN(DAY)?|"
		+ "����[һ������������]|��|��|��|ʱ|��|��|\\p{Punct}| |\\s|\\p{Zs})*)+";

	public static void main(String[] args){
		// 2015��04��18��?07:03??���˲ƾ�?΢�� ���л�˵ �ղر���?? ??  ���¹ؼ��ʣ� �ƾ���ý��ýͷ��ͷ�漯�� ��ӭ�������� ����:
		// http://www.sina.com.cn??2012��08��21�� 10:31??���˲ƾ�΢��  ��?�ֻ�������?�� ��?���˲ƾ���?��
		List<String> dts = postdateExtraction("2016-07-19 09:04");
		//List<String> dts = postdateExtraction(" ����ӡ�������塿2014��1��27�� �й���ҵ�о���http://www.chinairn.com �����ջ������� ������ҵ����о����� 2014-2018��������������Ŀ�������о����� 2014-2018��������Ŀ�������о����� 2014-2018���ʰ����Ұ�����Ŀ�������о����� 2014-2018���й����¼���ҵ�г����������Ͷ�ʷ��շ��� 2014-2018���ʰ�����Ŀ�������о����� 2014-2018���й��������ҵ�г����������Ͷ�ʷ��շ��� 2014-2018���й����ܼ���ҵ�г����������Ͷ�ʷ��շ� 2014-2018���й���Ƭ��ҵ�г����������Ͷ�ʷ��շ��� �鿴������ҵ>>  ��һҳ 1 2 3 ��һҳ ��ǩ��������ҵ�о����� ʯ�ͻ�����ҵ�г��о����� ������ҵ��ҵ��Ѷ ���ķ����ַ:http://www.chinairn.com/news/20140127/113208333.html ������ ������� ��������ҵȥ�꾻��ͬ������һ�� 2014/1/27 14:54:15 ��2013�������ҵҵ�������С���һ�� 2014/1/27 14:35:35 ��������ҵ����̧���ʲ������۸� 2013/12/14 8:49:16 ��������ҵ�������� 2013/12/13 14:34:56 ��������ҵ����ļͶ��Ŀ�ɵ�� 2013/12/7 9:01:51");
		for(String str : dts){
			System.out.println(str);
		}
	}

	public static String getPostdate(String source){
		String str_MMM_CHINA = Arrays.toString(months_MMM_CHINA.toArray()).replace(", ", "|");
		str_MMM_CHINA = str_MMM_CHINA.substring(1,str_MMM_CHINA.length()-1);

		String str_months_MMMMM = Arrays.toString(months_MMMMM.toArray()).replace(", ", "|");
		str_months_MMMMM = str_months_MMMMM.substring(1,str_months_MMMMM.length()-1);

		String str_months_MMM = Arrays.toString(months_MMM.toArray()).replace(", ", "|");
		str_months_MMM = str_months_MMM.substring(1,str_months_MMM.length()-1);

		String str_ampm = Arrays.toString(AMPM.toArray()).replace(", ", "|");
		str_ampm = str_ampm.substring(1,str_ampm.length()-1);

		String str_weekdays_EEE = Arrays.toString(weekdays_EEE.toArray()).replace(", ", "|");
		str_weekdays_EEE = str_weekdays_EEE.substring(1,str_weekdays_EEE.length()-1);

		String str_weekdays_EEEEE = Arrays.toString(weekdays_EEEEE.toArray()).replace(", ", "|");
		str_weekdays_EEEEE = str_weekdays_EEEEE.substring(1,str_weekdays_EEEEE.length()-1);

		String str_EEE_CHINA = Arrays.toString(weekdays_EEE_CHINA.toArray()).replace(", ", "|");
		str_EEE_CHINA = str_EEE_CHINA.substring(1,str_EEE_CHINA.length()-1);

		String str_timeUnit_Chinese = Arrays.toString(timeUnit_Chinese.toArray()).replace(", ", "|");
		str_timeUnit_Chinese = str_timeUnit_Chinese.substring(1,str_timeUnit_Chinese.length()-1);

		String str2 = ""+str_MMM_CHINA+"|"+str_months_MMMMM+"|"+str_months_MMM+"|"
				+str_ampm+"|"+str_weekdays_EEE+"|"+str_weekdays_EEEEE+"|"+str_EEE_CHINA+"|"+str_timeUnit_Chinese+"";
		String str = "((?!"+str2+")\\w)+";
		//((?![����|ƥ��])\\W)*
		Pattern pattern = Pattern.compile(str);
		Matcher m = pattern.matcher(source);
		while(m.find()){
			if(m.group().trim().isEmpty()){
				continue;
			}
			source = source.replace(m.group().trim(), " ");
			m = pattern.matcher(source);
		}
		return source;
	}



	static{

	}

	/**
	 * ��ȡһ���ı��е���������ֵ
	 * @param sourc
	 * @return
	 */
	public static List<String> postdateExtraction(String source){
		source = source.replaceAll("( )+", " ");
		List<String> postdates = new ArrayList<String>();
		Pattern p = Pattern.compile(postdateExtractReg);
        Matcher m = p.matcher(source);
        while(m.find()){
    		postdates.add(m.group().trim().replaceAll("(?<=.+)[\\p{Punct} ]+$", ""));
        }
        return postdates;
	}



	/**
	 * ʹ�ö��ָ�ʽƥ���ַ��������滻
	 * @param dateTimeStr
	 */
	public static String checkOutNoneDigitPart(String result){
		result = checkOutNoneDigitPartWithDatePattern(result,months_MMMMM,"{MMMMM}");
		result = checkOutNoneDigitPartWithDatePattern(result,months_MMM,"{MMM}");
		result = checkOutNoneDigitPartWithDatePattern(result,months_MMM_CHINA,"{MMM}");
		result = checkOutNoneDigitPartWithDatePattern(result,AMPM,"{a}");
		result = checkOutNoneDigitPartWithDatePattern(result,weekdays_EEEEE,"{EEEEE}");
		result = checkOutNoneDigitPartWithDatePattern(result,weekdays_EEE,"{EEE}");
		result = checkOutNoneDigitPartWithDatePattern(result,weekdays_EEE_CHINA,"{EEE}");
		return result;
	}

	/**
	 * ʹ��ָ���ַ�ȥƥ�������ַ���
	 * @param dateTimeStr
	 * @param pattern
	 * @param replacement
	 * @return
	 */
	private static String checkOutNoneDigitPartWithDatePattern(String dateTimeStr,List<String> pattern,String replacement){
		String result = new String(dateTimeStr);
		String monthStr = Arrays.toString(pattern.toArray()).replace(", ", "|");
		monthStr = monthStr.substring(1,monthStr.length()-1);
		Pattern monthsPattern = Pattern.compile(monthStr,Pattern.CASE_INSENSITIVE);
		Matcher monthMatcher = monthsPattern.matcher(dateTimeStr);
		while(monthMatcher.find()){
			monthMatcher.group(0);
			result = result.replace(monthMatcher.group(0), replacement);
		}
		return result;
	}


	/**
	 * ���������е������������ڵ�λ
	 * @param str
	 * @return
	 */
	public static String handleDateTimeFormat(String str){
		str = handleChineseTimeUnit(str,"\\w*\\d{1,2}��","ss��");
		str = handleChineseTimeUnit(str,"\\w*\\d{1,2}��","mm��");
		str = handleChineseTimeUnit(str,"\\w*\\d{1,2}ʱ","hhʱ");
		str = handleChineseTimeUnit(str,"\\w*\\d{1,2}��","dd��");
		str = handleChineseTimeUnit(str,"\\w*\\d{1,2}��","MM��");
		str = handleChineseTimeUnit(str,"\\w*\\d{4}��","YYYY��");
		str = handleChineseTimeUnit(str,"\\w*\\d{2}��","YY��");

		str = handleChineseTimeUnit(str,"\\d{1,2}:\\d{1,2}:\\d{1,2}","hh:mm:ss");
		str = handleChineseTimeUnit(str,"\\d{1,2}:\\d{1,2}","hh:mm");

		str = handleChineseTimeUnit(str,"\\D*?\\d{4}\\D*?","YYYY");
		return str;
	}

	private static String handleChineseTimeUnit(String result,String patternFormat,String dateFormat){
		Pattern chineseFormatSecondPattern = Pattern.compile(patternFormat);
		Matcher m = chineseFormatSecondPattern.matcher(result);
		if(m.find()){
			result = result.replace(m.group(), dateFormat);
		}
		return result;
	}

	/**
	 * ��ȡ�����ڸ�ʽ�ַ����еķָ���
	 * @param dateTimeStr
	 * @return
	 */
	private String getSpliter(String dateTimeStr){
		LinkedHashMap<Character,Integer> notDigitCharacter = new LinkedHashMap<Character,Integer>();

		//ͨ��ѭ����ȡ������
		for(Character c : dateTimeStr.toCharArray()){
			if(!Character.isDigit(c) && !Character.isLetter(c)){
				if(notDigitCharacter.containsKey(c)){
					notDigitCharacter.put(c, notDigitCharacter.get(c)+1);
				}else{
					notDigitCharacter.put(c, 1);
				}
			}
		}
		
		
		
		MapUtil.isAsc = false;
		Map<Character,Integer> rst = MapUtil.sortMapByValue(notDigitCharacter);
		
		//
		String strArray = Arrays.toString(rst.keySet().toArray());
		String[] dateEle = dateTimeStr.split(" |-|:");
		return rst.entrySet().iterator().next().getKey().toString();
	}
	
}
