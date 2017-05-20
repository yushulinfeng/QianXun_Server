package util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import config.BasicInfoConfig;
import domain.TradeRecord;

public class AlipayTransTool {
	private static final String TEMPLATE_PATH = BasicInfoConfig.TEMPLATE_PATH;// 模板文件路径

	/**
	 * 生成付款Excel文件
	 * 
	 * @param array
	 *            提现的数组
	 * @param batchNumber
	 *            批次号，请确保唯一
	 * @param payTime
	 *            付款时间，格式：2016-04-21 15:43:31
	 * @param excel_file_path
	 *            生成的文件路径
	 */
	public static boolean transToExcel(List<TradeRecord> array,
			String batchNumber, String payTime, String excel_file_path) {
		// if (array == null)
		// return false;
		// try {
		// // 复制并打开文件
		// Workbook wb = Workbook.getWorkbook(new File(TEMPLATE_PATH));
		// WritableWorkbook book = Workbook.createWorkbook(new
		// File(excel_file_path), wb);
		// WritableSheet sheet = book.getSheet(0);
		// // 写入数据
		// sheet.addCell(new Label(0, 1, batchNumber));
		// sheet.addCell(new Label(1, 1, payTime));
		// double totle_money = 0;
		// int totle_count = array.size();
		// for (int i = 0; i < array.size(); i++) {
		// TradeRecord an_trade = array.get(i);
		// sheet.addCell(new Label(0, i + 3, an_trade.getId() + ""));
		// sheet.addCell(new Label(1, i + 3, an_trade.getAccount_email()));
		// sheet.addCell(new Label(2, i + 3, an_trade.getAccount_name()));
		// sheet.addCell(new Label(3, i + 3, an_trade.getAccount_fee()));
		// sheet.addCell(new Label(4, i + 3, an_trade.getAccount_note()));
		// try {// 暂不处理这个error，因为这里出错会在失败记录中出现
		// String money_str = an_trade.getAccount_fee();
		// double money = Double.parseDouble(money_str);
		// totle_money += money;
		// } catch (Exception e) {
		// }
		// }
		// DecimalFormat df = new DecimalFormat("0.00");
		// String totle_money_str = df.format(totle_money);
		// sheet.addCell(new Label(4, 1, totle_money_str));
		// sheet.addCell(new Label(5, 1, totle_count + ""));
		// book.write();
		// // 关闭文件
		// book.close();
		// return true;
		// } catch (Exception e) {
		// e.printStackTrace();
		// return false;
		// }
		return false;
	}

	/**
	 * 将本地指定路径的excel文件转换为数组
	 * 
	 * @param excel_file
	 *            本地文件路径
	 * @param excel_file_type
	 *            文件类型
	 * @return 结果数组
	 */
	public static ArrayList<TradeRecord> transToArray(String excel_file_path) {
		return transToArray(new File(excel_file_path));
	}

	/**
	 * 将本地excel文件转换为数组
	 * 
	 * @param excel_file
	 *            本地文件
	 * @param excel_file_type
	 *            文件类型
	 * @return 结果数组
	 */
	private static ArrayList<TradeRecord> transToArray(File excel_file) {
		ArrayList<TradeRecord> array = new ArrayList<TradeRecord>();
		// try {
		// Workbook book = Workbook.getWorkbook(excel_file);
		// Sheet sheet = book.getSheet(0);
		// // 是那个表
		// Cell cell = null;
		// cell = sheet.getCell(6, 0);
		// String result = null;
		// int end = 9;
		// try {
		// result = cell.getContents();
		// end = (result == null || result.trim().equals("")) ? 9 : 6;
		// } catch (Exception e) {
		// end = 9;
		// }
		// // 处理
		// cell = sheet.getCell(5, 1);
		// result = cell.getContents();
		// int count = 0;
		// try {
		// count = Integer.parseInt(result);
		// } catch (Exception e) {
		// }
		// for (int i = 0; i < count; i++) {
		// String[] row = new String[end];
		// for (int j = 0; j < end; j++) {
		// cell = sheet.getCell(j, i + 3);
		// result = cell.getContents();
		// row[j] = result;
		// }
		// TradeRecord an_trade = new TradeRecord();
		// try {
		// int id = Integer.parseInt(row[0]);
		// an_trade.setId(id);
		// an_trade.setAccount_email(row[1]);
		// an_trade.setAccount_name(row[2]);
		// an_trade.setAccount_fee(row[3]);
		// an_trade.setAccount_note(row[4]);
		// if (end == 9)
		// an_trade.setFinalStatus(("处理成功".equals(row[8])) ? 1 : 2);//
		// 获取批量付款结果的列表
		// else
		// an_trade.setFinalStatus(("1".equals(row[5])) ? 1 : 2);// 获取手动处理的付款结果
		// array.add(an_trade);
		// } catch (Exception e) {
		// }
		// }
		// book.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// array = null;
		// }
		return array;
	}

	public static void main(String[] args) {
		ArrayList<TradeRecord> array = transToArray("d:\\789.xls");
		System.out.println(transToExcel(array, "201604240001",
				"20160424 12:23:34", "d:\\456.xls"));
	}

}
