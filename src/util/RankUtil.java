package util;

public class RankUtil {

	/**
	 * 将credit转换为rank
	 * 
	 * @param rank_credit
	 * @return
	 */
	public static int convertCreditToRank(int rank_credit) {
		if (0 <= rank_credit && rank_credit < 1000) {
			return 0;
		} else if (1000 <= rank_credit && rank_credit < 2000) {
			return 1;
		} else if (2000 <= rank_credit && rank_credit < 4000) {
			return 2;
		} else if (4000 <= rank_credit && rank_credit < 8000) {
			return 3;
		} else if (8000 <= rank_credit && rank_credit < 16000) {
			return 4;
		} else if (16000 <= rank_credit && rank_credit < 80000) {
			return 5;
		} else {
			return 6;
		}
	}

}
