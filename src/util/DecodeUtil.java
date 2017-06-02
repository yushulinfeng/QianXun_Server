package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.codec.binary.Base64;

public class DecodeUtil {

	public static byte[] decodeImageString(String imageData) {
		try {
			return Base64.decodeBase64(URLDecoder.decode(imageData, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] decodeImageString(String imageData, String charset) {
		try {
			return Base64.decodeBase64(URLDecoder.decode(imageData, charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
