package util;

import config.BasicInfoConfig;

public class CheckUtil {
	public static String sendCheckCode(long phone) {
		return "123456";
	}

	/**
	 * 检测字符串中是否有敏感词汇，有返回true，否则返回false
	 * 
	 * @param match
	 * @return
	 */
	public static boolean hasIllegalWords(String match) {
		// 检测敏感词汇
		try {
			String[] illegalWords = BasicInfoConfig.illegalWords;
			for (String s : illegalWords) {
				if (match.contains(s)) {
					return true;
				}
			}
		} catch (Exception e) {
			System.out.println("illegal words load failed..");
			e.printStackTrace();
			System.out.println("illegal words load failed..");
		}
		return false;
	}
}
