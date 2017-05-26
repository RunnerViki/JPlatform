package com.viki.crawlConfig.crawl;

public class SimpleRegExpGen {

	public static String genReg(String sourceUrl){
		StringBuilder groupName = new StringBuilder();
		sourceUrl = sourceUrl.contains("?")?sourceUrl.substring(0,sourceUrl.indexOf("?")-1):sourceUrl;
		String preSign = "";
		Integer preSignCount = 0;
		for(Character c : sourceUrl.toCharArray()){
			
			//
			if(Character.isDigit(c)){
				preSign = preSign.isEmpty()?"\\d":preSign;
				if(preSign.equals("\\d")){
					preSignCount = preSignCount+1;
				}else{
					groupName.append(preSign+"{"+preSignCount+"}");
					preSign = "\\d";
					preSignCount = 1;
				}
			}else if(Character.isLetter(c)){
				preSign = preSign.isEmpty()?"\\w":preSign;
				if("ƒÍ‘¬»’".contains(c.toString())){
					groupName.append(preSign+"{"+preSignCount+"}"+c);
					preSign = "";
					preSignCount = 0;
				}else if(preSign.equals("\\w")){
					preSignCount = preSignCount+1;
				}else{
					groupName.append(preSign+"{"+preSignCount+"}");
					preSign = "\\w";
					preSignCount = 1;
				}
			}else{
				if(!preSign.isEmpty()){
					groupName.append(preSign+"{"+preSignCount+"}");
				}
				preSign = "";
				preSignCount = 0;
				groupName.append(c);
			}
		}
		if(!preSign.isEmpty()){
			groupName.append(preSign+"{"+preSignCount+"}");
		}
		return groupName.toString();
	}
	
	public static String genRegByDateFormat(String dateFormat){
		dateFormat = dateFormat.replaceAll("d+", "\\\\d{1,2}");
		dateFormat = dateFormat.replaceAll("M+", "\\\\d{1,2}");
		dateFormat = dateFormat.replaceAll("y{4}", "\\\\d{4}");
		dateFormat = dateFormat.replaceAll("y{2}", "\\\\d{2}");
		dateFormat = dateFormat.replaceAll("mm", "\\\\d{1,2}");
		dateFormat = dateFormat.replaceAll("hh", "\\\\d{1,2}");
		dateFormat = dateFormat.replaceAll("ss", "\\\\d{1,2}");
		String dateFormatRegExp = new String(dateFormat);
		return dateFormatRegExp;
	}
	
}
