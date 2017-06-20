package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.struts2.interceptor.SessionAware;

import service.TradeRecordService;
import util.AlipayTransTool;
import util.ManagerUtil;
import util.MsgUtil;

import com.opensymphony.xwork2.ActionSupport;

import config.BasicInfoConfig;
import domain.TradeRecord;

public class ManagerAction extends ActionSupport implements SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = 809831071725060690L;
	private InputStream inputStream;
	private Map<String, Object> session;

	private TradeRecordService tradeRecordService;

	private String downtime;
	private File upfile;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public TradeRecordService getTradeRecordService() {
		return tradeRecordService;
	}

	public void setTradeRecordService(TradeRecordService tradeRecordService) {
		this.tradeRecordService = tradeRecordService;
	}

	public String getDowntime() {
		return downtime;
	}

	public void setDowntime(String downtime) {
		this.downtime = downtime;
	}

	public File getUpfile() {
		return upfile;
	}

	public void setUpfile(File upfile) {
		this.upfile = upfile;
	}

	// //////////////test///////////////////
	public String test() {
		inputStream = MsgUtil.sendString(session.get("session") + "TEST");
		return SUCCESS;
	}

	// /////////////////////////////////////////////////

	public String getTheDayCashExcel() {
		String root = BasicInfoConfig.root;
		String tradeDir = BasicInfoConfig.TradeRecordDir;
		if (!new File(root + tradeDir).exists()) {
			new File(root + tradeDir).mkdirs();
		}
		// ///////////////////////这句sql不会写了
		List<TradeRecord> array = tradeRecordService.getByUserId(Integer.parseInt(downtime));
		if (array == null || array.size() == 0) {
			String show = ManagerUtil.showDialog("Today No Data!");
			inputStream = MsgUtil.sendString(show);
			return SUCCESS;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String batchNumber = format.format(new Date());
		AlipayTransTool.transToExcel(array, batchNumber + 0001, batchNumber, root + tradeDir + batchNumber + ".xls");
		inputStream = MsgUtil.sendString(ManagerUtil.dealCashExcelPath(tradeDir + batchNumber + ".xls"));
		return SUCCESS;
	}

	public String updateTheDayCashExcel() {
		if (upfile != null) {// ///////////////////////文本框传输问题
			System.out.println("upfile:" + upfile);
			String root = BasicInfoConfig.root;
			String tradeDir = BasicInfoConfig.TradeRecordDir;
			String name = "123.xls";
			try {
				IOUtils.copy(new FileInputStream(upfile), new FileOutputStream(new File(root + tradeDir + name)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			String show = ManagerUtil.showDialog("Upload Failed!");
			inputStream = MsgUtil.sendString(show);
			return SUCCESS;
		}
		System.out.println("upfile:" + upfile);
		inputStream = MsgUtil.sendString("file:" + upfile);
		return SUCCESS;
	}
}
