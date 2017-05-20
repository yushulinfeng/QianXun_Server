package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class BasicInfoConfig {

	/**
	 * 密钥
	 */
	public static final String secretKey = "qianxun";

	/**
	 * 用BIG文件夹存储原图，用SMALL存储缩略图，然而数据库链接只存原图链接，在使用小图链接的时候会手动替换BIG为SMALL.
	 */
	public static String root = BaseConfiger.ROOT;
	public static String[] illegalWords;

	// static {
	// root = BasicInfoConfig.class.getResource("/").getPath();
	// System.out.println("init root :" + root);
	// root = root.substring(0, root.length() - 17);
	// // root = root.replace("%20", " ");
	// try {
	// root = URLDecoder.decode(root, "UTF-8");
	// } catch (UnsupportedEncodingException e) {
	// root = root.replace("%20", " ");
	// e.printStackTrace();
	// }
	// // root = "E://qianxun//";
	// System.out.println("config root :" + root);
	// }

	// 主机地址 /http://120.27.25.61/
	public static final String HOST = BaseConfiger.HOST;
	// public static final String HOST = "http://192.168.1.102:8080";

	// 交易记录日志保存地址
	public static final String TradeRecordDir = "/Files/TradeRecord/";

	// 支付宝处理完之后返回的文件
	public static final String AlipayTradeDir = "/Files/alipay/";

	// 用户头像保存地址
	public static final String headIconDir = "/Image/headIcon/big/";
	public static final String SmallheadIconDir = "/Image/headIcon/small/";

	// 默认头像保存地址
	public static final String default_headIcon = "/Image/qianxun/default_headIcon.png";

	// 千寻主页图片保存文件夹
	public static final String propImagePath = "/Image/propImage/";

	// 用户主页背景图片保存地址
	public static final String homePageImageDir = "/Image/backgroundImage/big/";
	public static final String SmallhomePageImageDir = "/Image/backgroundImage/small/";

	// 用户需求类图片保存地址
	public static final String UserRequestImageDir = "/Image/userRequest/big/";
	public static final String SmallUserRequestImageDir = "/Image/userRequest/small/";

	// 用户认证类图片保存地址
	public static final String IDImageDir = "/Image/UserVerify/IDImage/";
	public static final String stuIDImageDir = "/Image/UserVerify/stuIDImage/";

	// 用户评论类图片保存地址
	public static final String CommentImageDir = "/Image/userComment/big/";
	public static final String SmallCommentImageDir = "/Image/userComment/small/";

	// 用户动态类图片保存地址
	public static final String DynamicImageDir = "/Image/dynamic/big/";
	public static final String SmallDynamicImageDir = "/Image/dynamic/small/";

	// 用户服务类图片保存地址
	public static final String BusinessServiceImageDir = "/Image/businessService/big/";
	public static final String SmallBusinessServiceImageDir = "/Image/businessService/small/";

	// 最大举报数
	public static final int MaxReportNumber = 3;

	// excel默认存放路径
	public static final String TEMPLATE_PATH = BasicInfoConfig.root + "/WEB-INF/classes/alipay_template.xls";// 模板文件路径

	// 预设值的提现周期,1天
	public static final long CASH_PERIOD = 1000 * 60 * 60 * 24;

	static {
		File words = new File(BasicInfoConfig.root + "/words.txt");
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			String line;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(words), "UTF-8"));
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			String s = sb.toString();
			illegalWords = s.split(",");
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			illegalWords = null;
		}
	}

	public static void main(String[] args) {

	}

}

// 默认在用户登录的时候检查一遍数据库中的需求是否已经过期
