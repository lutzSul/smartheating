package de.lutz.smartheating.uponor;

public class UponorKnxHelper {
	public static final String ADDR_TEMPERATURE = "1";
	public static final String ADDR_SETPOINT = "2";
	public static final String ADDR_HAVAC_MODE = "3";
	public static final String ADDR_BATTERY_STATUS = "4";
	public static final String ADDR_MIN_SETPOINT = "5";
	public static final String ADDR_MAX_SETPOINT = "6";
	public static final String ADDR_COMFORT_SETTING = "7";
	public static final String ADDR_ACTUATOR_STATUS = "8";
	public static final String ADDR_ACTUATOR_ALARM = "9";
	public static final String ADDR_REMOTE_SETPOINT = "10";
	
	public static boolean isCurrentTemperature(String groupAddr) {
		String[] parts = groupAddr.split("/");
		
		if (parts[2].equals("1")) {
			return true;
		}
		return false;
	}
	
	public static boolean isSetpoint(String groupAddr) {
		String[] parts = groupAddr.split("/");
		
		if (parts[2].equals("2")) {
			return true;
		}
		return false;
	}
	
	public static String getHauptgruppe(String groupAddr) {
		String[] parts = groupAddr.split("/");
		return parts[0];
	}
	
	public static String getMittelgruppe(String groupAddr) {
		String[] parts = groupAddr.split("/");
		return parts[1];
	}
	
	public static String getAdresse(String groupAddr) {
		String[] parts = groupAddr.split("/");
		return parts[2];
	}
	
	public static boolean isDouble(String groupAddr) {
		String[] parts = groupAddr.split("/");
		
		if (parts[2].equals("1")) {
			return true;
		}
		if (parts[2].equals("2")) {
			return true;
		}
		if (parts[2].equals("5")) {
			return true;
		}
		if (parts[2].equals("6")) {
			return true;
		}
		if (parts[2].equals("0")) {
			return true;
		}
		return false;
	}
	
	public static boolean isBoolean(String groupAddr) {
		String[] parts = groupAddr.split("/");
		
		if (parts[2].equals("4")) {
			return true;
		}
		if (parts[2].equals("7")) {
			return true;
		}
		if (parts[2].equals("8")) {
			return true;
		}
		if (parts[2].equals("9")) {
			return true;
		}
		return false;
	}
	
	public static boolean isInt(String groupAddr) {
		String[] parts = groupAddr.split("/");
		
		if (parts[2].equals("3")) {
			return true;
		}
		return false;
	}
	
}
