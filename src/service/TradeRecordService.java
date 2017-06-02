package service;

import java.util.List;

import domain.TradeRecord;

public interface TradeRecordService {

	boolean save(TradeRecord tr);

	boolean delete(TradeRecord tr);

	List<TradeRecord> getByUserId(int userId);

	boolean updateBatch(List<TradeRecord> list);

	List<TradeRecord> getTheDay(String today);

}
