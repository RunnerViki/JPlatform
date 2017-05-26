package com.viki.crawlConfig.utils;

import java.util.ArrayList;

public class StringUtil {
	
	public static String[] splitIgnoreEmptyString(String strObj,String splitChar){
		String[] temp = strObj.split(splitChar);
		ArrayList<String> tempArr = new ArrayList<String>();
		for(String s : temp){
			if(s.trim().length() > 0){
				tempArr.add(s);
			}
		}
		return tempArr.toArray(new String[tempArr.size()]);
	}

}
