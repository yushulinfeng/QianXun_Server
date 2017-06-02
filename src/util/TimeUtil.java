package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	/**
	 * 返回时间戳的（长整形）形式的时间
	 * 
	 * @return
	 */
	public static String currentTime() {
		return System.currentTimeMillis() + "";
	}

	/**
	 * 返回当前的"yyyy-MM-dd"形式的时间
	 * 
	 * @return
	 */
	public static String formatCurrentTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	/**
	 * 返回format格式化的当前时间 例：format="yyyy-MM-dd hh:MM:ss"
	 * 
	 * @param format
	 * @return
	 */
	public static String format(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * convert "yyyy-MM-dd" to timestamp.
	 * 
	 * @param stime
	 * @return
	 * @throws ParseException
	 */
	public static Date convertToTimestamp(String stime) throws ParseException {
		stime = stime.trim() + " 00:00:00";
		System.out.println(stime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date result = sdf.parse(stime);

		return result;
	}

	public static String convertTo_yyyyMMdd(String timestamp) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(Long.parseLong(timestamp)));
	}

	public static void main(String[] args) throws ParseException {
		String date = convertTo_yyyyMMdd(System.currentTimeMillis() + "");
		System.out.println(date);
	}

}
