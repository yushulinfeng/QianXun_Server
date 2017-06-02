package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

public class LogUtil {

	public LogUtil() {
		String root = this.getClass().getResource("/").getPath();
		root = root.substring(0, root.length() - 17);

		File f = new File(root + new Date() + ".txt");
		try {
			f.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(f);
			PrintStream printStream = new PrintStream(fileOutputStream);
			System.setOut(printStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
