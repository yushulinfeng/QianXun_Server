package dao;

import java.util.List;

import domain.TradeRecord;

public interface TradeRecordDAO {

	boolean save(TradeRecord tr);

	boolean delete(TradeRecord tr);

	List<TradeRecord> getByUserId(int userId);

	boolean updateBatch(List<TradeRecord> list);

	List<TradeRecord> getTheDay(String today);

}
