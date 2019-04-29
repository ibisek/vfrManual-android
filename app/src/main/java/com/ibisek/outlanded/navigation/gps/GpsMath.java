package com.ibisek.outlanded.navigation.gps;

import static java.lang.Math.toDegrees;

/**
 * Based on: http://www.movable-type.co.uk/scripts/latlong.html
 * http://stackoverflow.com/questions
 * /4913349/haversine-formula-in-python-bearing
 * -and-distance-between-two-gps-points
 */
public class GpsMath {

	/**
	 * Longitude and latitude in RADians!
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return distance between two points in kilometers
	 */
	public static float getDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
		int R = 6371; // km
		double dist = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

		return (float)dist;
	}

	/**
	 * Longitude and latitude in RADians!
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return distance between two points in meters
	 */
	public static float getDistanceInM(double lat1, double lon1, double lat2, double lon2) {
		return getDistanceInKm(lat1, lon1, lat2, lon2) * 1000.0f;
	}

	/**
	 * Longitude and latitude in RADians!
	 * 
	 * @param lat1 TO
	 * @param lon1 TO
	 * @param lat2 FROM
	 * @param lon2 FROM
	 * @return bearing in degrees
	 */
	public static float getBearing(double lat1, double lon1, double lat2, double lon2) {

		double dLon = lon2 - lon1;
		double y = Math.sin(dLon) * Math.cos(lat2);
		double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
		double bearing = Math.atan2(y, x);

		bearing = ((toDegrees(bearing) + 180) % 360);

		return (float) bearing;
	}

	public static void main(String[] args) {

		// VM 49.355442 16.01449
		double lat1 = Math.toRadians(49.355442);
		double lon1 = Math.toRadians(16.01449);

		// Zlin 49.232154 17.671218
		double lat2 = Math.toRadians(49.232154);
		double lon2 = Math.toRadians(17.671218);

		float dist = getDistanceInKm(lat1, lon1, lat2, lon2);
		System.out.println("dist=" + dist);

		float bearing = getBearing(lat1, lon1, lat2, lon2);
		System.out.println("bearing=" + bearing);

	}

}
