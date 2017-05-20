package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

public class BaseConfiger {

	/**
	 * 项目部署后所在路径
	 */
	public static String ROOT = null;

	/**
	 * 项目所在的host路径，用以拼装返回的文件路径
	 */
	public static String HOST = "http://120.27.25.61:80/";
	/**
	 * 项目的文件所存放目录
	 */
	public static String FileSavePath = null;

	static {
		try {
			Properties props = new Properties();
			String srcPath = BaseConfiger.class.getResource("/").getPath();
			try {
				srcPath = URLDecoder.decode(srcPath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				srcPath = srcPath.replace("%20", " ");
				e.printStackTrace();
			}
			ROOT = srcPath.substring(0, srcPath.length() - 17);
			props.load(new FileInputStream(new File(srcPath + File.separator + "qianxun.properties")));

			FileSavePath = props.getProperty("FileSavePath", ROOT + "/qianxun");
			HOST = props.getProperty("HOST", HOST);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
