package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.SessionAware;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;

import config.BaseConfiger;
import config.BasicInfoConfig;
import dao.PropDAO;
import domain.PropImage;

public class PropAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3918375709223362142L;

	private Map<String, Object> session;

	private PropDAO propDAO;
	private InputStream inputStream;
	private PropImage prop;

	public String getMainPageImages() {
		String result = "failed";
		try {
			List<PropImage> list = propDAO.getLinks();
			JSONObject js = new JSONObject();
			js.put("list", list);
			result = js.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";
		}

		System.out.println("getMainPageImages-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	// //后期添加，为区分高能千寻
	public String getMainPageImages2() {
		String result = "{\"list\":[{\"context\":\"\",\"id\":1,"
				+ "\"image\":\"/qianxun/propImage/qx01.png\","
				+ "\"img\":null,\"link\":\"https://mp.weixin.qq.com/s"
				+ "?__biz=MzIzMDE5MDU1MQ==&mid=402202743&idx=1"
				+ "&sn=fccc7da1a99a828c510911318e272c5e&scene=18"
				+ "&key=710a5d99946419d90bd42ccffaa97690436f71146"
				+ "3198ca3e10c901fe55d7b18c651524aa47098015c17af7d"
				+ "4003c8b0&ascene=7&uin=MTcxMjE4NDY2MQ%3D%3D&devic\","
				+ "\"title\":\"\"},{\"context\":\"\",\"id\":2,"
				+ "\"image\":\"/qianxun/propImage/qx02.png\","
				+ "\"img\":null,\"link\":\"http://mp.weixin.qq.com/s"
				+ "?__biz=MzIzMDE5MDU1MQ==&mid=402205539&idx=1"
				+ "&sn=b046f7bf453ace90f7c366239ea8ac98&scene=18"
				+ "#wechat_redirect\",\"title\":\"\"},{\"context\":"
				+ "\"\",\"id\":3,\"image\":\"/qianxun/propImage/qx03"
				+ ".png\",\"img\":null,\"link\":\"https://mp.weixin"
				+ ".qq.com/s?__biz=MzIzMDE5MDU1MQ==&mid=402202743"
				+ "&idx=1&sn=fccc7da1a99a828c510911318e272c5e&scene"
				+ "=18&key=710a5d99946419d90bd42ccffaa97690436f71146"
				+ "3198ca3e10c901fe55d7b18c651524aa47098015c17af7d40"
				+ "03c8b0&ascene=7&uin=MTcxMjE4NDY2MQ%3D%3D&devic\","
				+ "\"title\":\"\"},{\"context\":\"高能\",\"id\":4,"
				+ "\"image\":\"/qianxun/propImage/qx04.png\",\"img\":null,"
				+ "\"link\":\"http://www.sylsylsyl.com/software"
				+ "/qianxun/BiaoQing/main.html\",\"title\":\"高能\"},"
				+ "{\"context\":\"\",\"id\":5,\"image\":\"/qianxun"
				+ "/propImage/qx05.png\",\"img\":null,\"link\":"
				+ "\"https://mp.weixin.qq.com/s?__biz=MzIzMDE5MDU1MQ="
				+ "=&mid=402202743&idx=1&sn=fccc7da1a99a828c510911318"
				+ "e272c5e&scene=18&key=710a5d99946419d90bd42ccffaa97"
				+ "690436f711463198ca3e10c901fe55d7b18c651524aa470980"
				+ "15c17af7d4003c8b0&ascene=7&uin=MTcxMjE4NDY2MQ%3D"
				+ "%3D&devic\",\"title\":\"\"}]}";
		System.out.println("getMainPageImages-result:" + result);
		inputStream = MsgUtil.sendString(result);
		return SUCCESS;
	}

	public String getSmallImages() {
		String result = "failed";
		try {
			List<PropImage> list = propDAO.getSmallLinks();
			JSONObject js = new JSONObject();
			js.put("list", list);
			result = js.toString();
		} catch (Exception e) {
			e.printStackTrace();
			result = "failed";
		}

		System.out.println("getMainPageImages-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;

	}

	public String updateLink() {
		System.out.println("updateLink-id:" + prop.getId());
		System.out.println("updateLink-link:" + prop.getLink());
		System.out.println("updateLink-imgPath:"
				+ prop.getImg().getAbsolutePath());

		String result = "-1";

		if (!"".equals(prop.getImg()) && prop.getImg() != null
				&& !"null".equals(prop.getImg())) {

			File path = new File(BaseConfiger.FileSavePath
					+ BasicInfoConfig.propImagePath);
			if (!path.exists()) {
				path.mkdirs();
			}

			String picName = prop.getId() + System.currentTimeMillis() + ".jpg";
			try {
				IOUtils.copy(new FileInputStream(prop.getImg()),
						new FileOutputStream(new File(BaseConfiger.FileSavePath
								+ BasicInfoConfig.propImagePath + picName)));
				prop.setImage("/qianxun" + BasicInfoConfig.propImagePath
						+ picName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		boolean flag = propDAO.update(prop);

		if (flag) {
			result = "1";
		}

		System.out.println("updateLink-result:" + result);
		inputStream = MsgUtil.sendString(result);

		return SUCCESS;
	}

	/****************/

	public PropDAO getPropDAO() {
		return propDAO;
	}

	public void setPropDAO(PropDAO propDAO) {
		this.propDAO = propDAO;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public PropImage getProp() {
		return prop;
	}

	public void setProp(PropImage prop) {
		this.prop = prop;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
