package util;

import java.net.URLEncoder;

public class ManagerUtil {
	private static String server_path = "http://localhost:8080/qianxun/";

	public static String showDialog(String text) {
		return "<script> alert('" + text + "'); </script>";
	}

	public static String dealCashExcelPath(String path) {
		return "<script> window.location='" + server_path + path + "' </script>";
	}

	public String getCashExcelName(String downtime) {
		String filename = "getcash_";
		try {
			filename += downtime + ".xls";
			filename = URLEncoder.encode(filename, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filename;
	}

}
