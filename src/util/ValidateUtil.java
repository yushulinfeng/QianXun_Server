package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ValidateUtil {

	// 静态方法，便于作为工具类
	public static String MD5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString();
			// 16位的加密
			// return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void main(String[] args) {
		// 测试
		System.out.println(ValidateUtil.MD5(ValidateUtil.MD5("1462395446916" + "qianxun")));
		System.out.println("565cde174b7161c66cbd981c2afda580");
	}

}
