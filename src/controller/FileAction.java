package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import service.TradeRecordService;
import util.AlipayTransTool;
import util.MsgUtil;
import util.TimeUtil;

import com.opensymphony.xwork2.ActionSupport;

import config.BaseConfiger;
import config.BasicInfoConfig;
import domain.TradeRecord;

public class FileAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4495016920453329938L;

	private Map<String, Object> session;
	private InputStream inputStream;

	private TradeRecordService tradeRecordService;

	private File alipayResult;
	private String today;

	public String alipayUpload() {
		System.out.println("alipayUpload-alipayResult:" + alipayResult.getAbsolutePath());

		boolean isSaved = false;

		File dir = new File(BaseConfiger.FileSavePath + BasicInfoConfig.AlipayTradeDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String filePath = BaseConfiger.FileSavePath + BasicInfoConfig.AlipayTradeDir + System.currentTimeMillis()
				/ 1000 + ".xls";

		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}

		try {

			file.createNewFile();

			InputStream in = new FileInputStream(alipayResult);

			OutputStream out = new FileOutputStream(file);

			byte[] b = new byte[40];
			while (in.read(b) != -1) {
				out.write(b);
				out.flush();
			}
			in.close();
			out.close();
			isSaved = true;
		} catch (FileNotFoundException e) {
			isSaved = false;
			e.printStackTrace();
		} catch (IOException e) {
			isSaved = false;
			e.printStackTrace();
		}

		// 进行执行excel的操作
		if (isSaved) {
			List<TradeRecord> list = AlipayTransTool.transToArray(filePath);
			if (list == null) {
				isSaved = false;
			} else {
				isSaved = tradeRecordService.updateBatch(list);
			}
		}

		System.out.println("alipayUpload-isSaved:" + isSaved);
		inputStream = MsgUtil.sendString((isSaved ? 1 : -1) + "");
		return SUCCESS;
	}

	public String tradeDownload() {
		System.out.println("tradeDownload-timestamp:" + today);

		boolean isSucceed = false;
		File dir = new File(BaseConfiger.FileSavePath + BasicInfoConfig.TradeRecordDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String filePath = BaseConfiger.FileSavePath + BasicInfoConfig.TradeRecordDir + System.currentTimeMillis()
				+ ".xls";

		try {
			List<TradeRecord> list = tradeRecordService.getTheDay(today);

			if (list == null) {
				isSucceed = false;
			} else {
				boolean flag = AlipayTransTool.transToExcel((ArrayList<TradeRecord>) list, System.currentTimeMillis()
						+ "", TimeUtil.format("yyyy-MM-dd hh:MM:ss"), filePath);
				if (flag) {
					isSucceed = true;
				} else {
					isSucceed = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isSucceed = false;
		}

		System.out.println("alipayUpload-isSucceed:" + isSucceed);
		System.out.println("alipayUpload-filePath：" + filePath);
		inputStream = MsgUtil.sendString((isSucceed ? filePath : -1) + "");
		return SUCCESS;
	}

	/***************************/
	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public File getAlipayResult() {
		return alipayResult;
	}

	public void setAlipayResult(File alipayResult) {
		this.alipayResult = alipayResult;
	}

	public TradeRecordService getTradeRecordService() {
		return tradeRecordService;
	}

	public void setTradeRecordService(TradeRecordService tradeRecordService) {
		this.tradeRecordService = tradeRecordService;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getToday() {
		return today;
	}

	public void setToday(String today) {
		this.today = today;
	}

}
