package com.viki.crawlConfig.crawl;

import com.viki.crawlConfig.utils.MapUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
* 日期格式提取
* */
public class DateTimeFormatGen {

    Logger logger = LoggerFactory.getLogger(DateTimeFormatGen.class);
    public static final List<String> months_MMMMM = Arrays.asList(new String[]{
            "January","February","March","April","May","June"
            ,"July","August","September","October","November","December"});
    public static final List<String> months_MMM = Arrays.asList(new String[]{
            "Jan","Feb","Mar","Apr","May","Jun"
            ,"Jul","Aug","Sep","Oct","Nov","Dec"});
    public static final List<String> months_MMM_CHINA = Arrays.asList(new String[]{
            "一月","二月","三月","四月","五月","六月"
            ,"七月","八月","九月","十月","十一月","十二月"});

    public static final List<String> AMPM = Arrays.asList(new String[]{"am","pm"});

    public static final List<String> weekdays_EEE = Arrays.asList(new String[]{
            "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"});
    public static final List<String> weekdays_EEEEE = Arrays.asList(new String[]{
            "MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"});
    public static final List<String> weekdays_EEE_CHINA = Arrays.asList(new String[]{"星期一","星期二","星期三","星期四","星期五","星期六","星期日"});
    public static final List<String> timeUnit_Chinese = Arrays.asList(new String[]{"年","月","日","时","分","秒"});

    public static final List<String> timeUnit_Chinese_test = Arrays.asList(new String[]{"\\d+年","\\d+月","\\d+日","\\d+时","\\d+分","\\d+秒"});

	/*private static String postdateExtractReg = "(((\\d{2}|\\d{4})(?<=\\d+))(一月|二月|三月|四月|五月|六月|七月|八月|九月|十月|十一月|十二月|Jan(uary)?|"
        + "Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?|"
        + "am|pm|MON(DAY)?|TUE(SDAY)?|WED(NESDAY)?|THU(RSDAY)?|FRI(DAY)?|SAT(URDAY)?|SUN(DAY)?|"
        + "星期[一二三四五六日]|年|月|日|时|分|秒|\\p{Punct}| )*)+";*/

    //2015-04-18:添加\\p{Zs}，支持全角空格
	/*private static String postdateExtractReg = "(((\\d{1}|\\d{2}|\\d{4})(?<=\\d))" +
			"(一月|二月|三月|四月|五月|六月|七月|八月|九月|十月|十一月|十二月|" +
			"Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?|"
        + "am|pm|"
        + "MON(DAY)?|TUE(SDAY)?|WED(NESDAY)?|THU(RSDAY)?|FRI(DAY)?|SAT(URDAY)?|SUN(DAY)?|"
        + "星期[一二三四五六日]|年|月|日|时|分|秒|\\p{Punct}| |\\s|\\p{Zs})*)+";*/

    private static String postdateExtractReg = "(((\\d{1}|\\d{2}|\\d{4})(?<=\\d)" +
            "|一月|二月|三月|四月|五月|六月|七月|八月|九月|十月|十一月|十二月|" +
            "Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?|"
            + "am|pm|"
            + "MON(DAY)?|TUE(SDAY)?|WED(NESDAY)?|THU(RSDAY)?|FRI(DAY)?|SAT(URDAY)?|SUN(DAY)?|"
            + "星期[一二三四五六日]|年|月|日|时|分|秒|\\p{Punct}| |\\s|\\p{Zs})*)+";

    public static void main(String[] args){
        // 2015年04月18日?07:03??新浪财经?微博 我有话说 收藏本文?? ??  文章关键词： 财经外媒外媒头版头版集萃 欢迎发表评论 分享到:
        // http://www.sina.com.cn??2012年08月21日 10:31??新浪财经微博  【?手机看新闻?】 【?新浪财经吧?】
        List<String> dts = postdateExtraction("2016-07-19 09:04");
        //List<String> dts = postdateExtraction(" 【打印】【繁体】2014年1月27日 中国行业研究网http://www.chinairn.com 中研普华报道： 金浦钛业相关研究报告 2014-2018年版甘油磷酸钠项目可行性研究报告 2014-2018年版甘油项目可行性研究报告 2014-2018年版甘氨酰酪氨酸项目可行性研究报告 2014-2018年中国种衣剂行业市场竞争格局与投资风险分析 2014-2018年版甘氨酸项目可行性研究报告 2014-2018年中国制冷剂行业市场竞争格局与投资风险分析 2014-2018年中国橡胶溶剂行业市场竞争格局与投资风险分 2014-2018年中国橡胶片行业市场竞争格局与投资风险分析 查看更多行业>>  上一页 1 2 3 下一页 标签：金浦钛业研究报告 石油化工行业市场研究报告 金浦钛业行业资讯 本文分享地址:http://www.chinairn.com/news/20140127/113208333.html 分享到： 相关新闻 ·金浦钛业去年净利同比增近一成 2014/1/27 14:54:15 ·2013年金浦钛业业绩“逆市”增一成 2014/1/27 14:35:35 ·金浦钛业涉嫌抬高资产评估价格 2013/12/14 8:49:16 ·金浦钛业定增疑云 2013/12/13 14:34:56 ·金浦钛业定增募投项目疑点多 2013/12/7 9:01:51");
        for(String str : dts){
//			logger.info(str);
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
        //((?![正则|匹配])\\W)*
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
     * 提取一段文本中的所有日期值
     * @param sourc
     * @return
     */
    public static List<String> postdateExtraction(String source){
        source = source.replaceAll("( )+", " ");
        List<String> postdates = new ArrayList<String>();
        Pattern p = Pattern.compile(postdateExtractReg);
        Matcher m = p.matcher(source);
        String postDateTmp;
        while(m.find()){
            postDateTmp = m.group().trim().replaceAll("(?<=.+)[\\p{Punct} ]+$", "");
            if(!StringUtils.isBlank(postDateTmp)){
                postdates.add(postDateTmp);
            }
        }
        return postdates;
    }

    public static void postdateExtraction(List<String> tmp, Element source, int depth){
        if(depth > 50){
            return;
        }
        for(Element ele : source.getAllElements()){
            if(ele == source){
                continue;
            }
            if(ele.getAllElements().size() > 1){
                String txt = ele.text().replaceAll("( )+", " ");
                Pattern p = Pattern.compile(postdateExtractReg);
                Matcher m = p.matcher(txt);
                if(m.find() && StringUtils.isNotBlank(m.group())){
                    postdateExtraction(tmp, ele, depth+1);
                }
            }else{
                String txt = ele.text().replaceAll("( )+", " ");
                Pattern p = Pattern.compile(postdateExtractReg);
                Matcher m = p.matcher(txt);
                String postDateTmp;
                while(m.find()){
                    postDateTmp = m.group().trim().replaceAll("(?<=.+)[\\p{Punct} ]+$", "");
                    if(!StringUtils.isBlank(postDateTmp.trim()) && postDateTmp.length() > 4 && !NumberUtils.isDigits(postDateTmp)){
                        tmp.add(postDateTmp);
                    }
                }
            }
        }
    }



    /**
     * 使用多种格式匹配字符串，并替换
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
     * 使用指定字符去匹配日期字符串
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
     * 处理日期中的所有中文日期单位
     * @param str
     * @return
     */
    public static String handleDateTimeFormat(String str){
        str = handleChineseTimeUnit(str,"\\w*\\d{1,2}秒","ss秒");
        str = handleChineseTimeUnit(str,"\\w*\\d{1,2}分","mm分");
        str = handleChineseTimeUnit(str,"\\w*\\d{1,2}时","hh时");
        str = handleChineseTimeUnit(str,"\\w*\\d{1,2}日","dd日");
        str = handleChineseTimeUnit(str,"\\w*\\d{1,2}月","MM月");
        str = handleChineseTimeUnit(str,"\\w*\\d{4}年","YYYY年");
        str = handleChineseTimeUnit(str,"\\w*\\d{2}年","YY年");

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
     * 获取到日期格式字符串中的分隔符
     * @param dateTimeStr
     * @return
     */
    private String getSpliter(String dateTimeStr){
        LinkedHashMap<Character,Integer> notDigitCharacter = new LinkedHashMap<Character,Integer>();

        //通过循环获取到所有
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
