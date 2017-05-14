package util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class MsgUtil {

	public static InputStream sendString(String s, String charset) {
		try {
			return new ByteArrayInputStream(s.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream sendString(String s) {
		return sendString(s, "UTF-8");
	}

}
