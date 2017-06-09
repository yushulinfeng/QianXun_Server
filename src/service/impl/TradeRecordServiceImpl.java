package service.impl;

import java.util.List;

import service.TradeRecordService;
import dao.TradeRecordDAO;
import domain.TradeRecord;

public class TradeRecordServiceImpl implements TradeRecordService {

	private TradeRecordDAO tradeRecordDAO;

	@Override
	public boolean save(TradeRecord tr) {
		return tradeRecordDAO.save(tr);
	}

	@Override
	public boolean delete(TradeRecord tr) {
		return tradeRecordDAO.delete(tr);
	}

	@Override
	public List<TradeRecord> getTheDay(String today) {
		return tradeRecordDAO.getTheDay(today);
	}

	@Override
	public boolean updateBatch(List<TradeRecord> list) {
		return tradeRecordDAO.updateBatch(list);
	}

	@Override
	public List<TradeRecord> getByUserId(int userId) {
		return tradeRecordDAO.getByUserId(userId);
	}

	/***************************/
	public TradeRecordDAO getTradeRecordDAO() {
		return tradeRecordDAO;
	}

	public void setTradeRecordDAO(TradeRecordDAO tradeRecordDAO) {
		this.tradeRecordDAO = tradeRecordDAO;
	}
}
