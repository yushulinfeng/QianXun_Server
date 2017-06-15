package util;

public class DistanceUtil {

	// ACOS(sin(PI()/180*starty)*sin(PI()/180*x) +
	// cos(PI()/180*starty)*cos(PI()/180*y)
	// *cos( PI()/180*x - PI()/180*startx ))*6371

	private static double EARTH_RADIUS = 6378.137;// 地球半径

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	public static double cal(double lat1, double lng1, double lat2, double lng2) {
		double s = Math.round((2 * Math.asin(Math.sqrt(Math.pow(
				Math.sin((lat1 * Math.PI / 180.0 - lat2 * Math.PI / 180.0) / 2), 2)
				+ Math.cos(lat1 * Math.PI / 180.0)
				* Math.cos(lat2 * Math.PI / 180.0 * Math.PI / 180.0)
				* Math.pow(Math.sin((lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0) / 2), 2))))
				* EARTH_RADIUS * 10000) / 10000;
		return s;
	}

	public static void main(String[] args) {
		System.out.println(GetDistance(36, 117, 36, 117));
		System.out.println(cal(36, 117, 36, 117));
		System.out.println(Math.sqrt(117 * 117 + 36 * 36));
	}

}
