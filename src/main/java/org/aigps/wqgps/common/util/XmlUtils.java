package org.aigps.wqgps.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlUtils {
	//将XML里的<XXXX>标签转成小写
	public static String convertXmlToLowerCase(String xml){
		Pattern pattern = Pattern.compile("<.+?>");
		StringBuilder sb = new StringBuilder();
		int lastIdx = 0;
		Matcher matcher = pattern.matcher(xml);
		while (matcher.find()) {
			String str = matcher.group();
			sb.append(xml.substring(lastIdx, matcher.start()));
			sb.append(str.toLowerCase());
			lastIdx = matcher.end();
		}
		return sb.append(xml.substring(lastIdx)).toString();
	}

}
