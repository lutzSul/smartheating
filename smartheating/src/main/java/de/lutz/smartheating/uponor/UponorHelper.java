package de.lutz.smartheating.uponor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class UponorHelper {

	public static final String PARAM_ROOM_TEMP = "RoomTemp";
	public static final String PARAM_ROOM_SETVALUE = "RoomSetvalue";
	public static final String PARAM_FLOOR_TEMP = "FloorTemp";
	public static final String PARAM_ROOM_HEAT_COOL_DEMAND = "RoomHeatCoolDemand";
	public static final String PARAM_ACTUATOR_STATUS = "ActuatorStatus";
	public static final String PARAM_ROOM_COMFORT_SETPOINT = "RoomComfortSetpoint";
	public static final String PARAM_ROOM_ECO_SETPOINT = "RoomEcoSetpoint";
	public static final String PARAM_LIST_OF_DATAPOINTS = "ListOfDatapoints";
	public static final String PARAM_ROOM_RELATIVE_HUMIDITY = "RoomRelativeHumidity";
	public static final String PARAM_ROOM_SETVALUE_LOW_LIMIT = "RoomSetvalueLowLimit";
	public static final String PARAM_ROOM_SETVALUE_HIGH_LIMIT = "RoomSetvalueHighLimit";
	public static final String PARAM_FLOOR_TEMP_MINIMUM = "FloorTempMinimum";
	public static final String PARAM_FLOOR_TEMP_MAXIMUM = "FloorTempMaximum";
	public static final String PARAM_COOLING_DISABLE = "CoolingDisable";
	public static final String PARAM_ROOM_NAME = "RoomName";
	public static final String PARAM_ROOM_HUMIDITY_SETPOINT = "RoomHumiditySetpoint";
	public static final String PARAM_HUMIDITY_LIMIT_INDICATION = "HumidityLimitIndication";
	public static final String PARAM_ACTIVATE_HUMIDITY_CONTROL = "ActivateHumidityControl";
	public static final String PARAM_HUMIDITY_HYSTERESIS = "HumidityHysteresis";
	public static final String PARAM_HEAT_COOL_HYSTERESIS = "HeatCoolHysteresis";
	public static final String PARAM_DEHUMIDIFIER_CONTROL = "DehumidifierControl";
	public static final String PARAM_THERMOSTAT_OVERRIDE = "ThermostatOverride";
	public static final String PARAM_ECO_SETPOINT = "ECOSetpoint";

	public static Map<Integer, Integer> roomIdsByServerId = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> serverIdsByRoomId = new HashMap<Integer, Integer>();

	public static Map<Integer, Integer> roomNumFromUFServerId = new HashMap<Integer, Integer>();

	public static Map<String, Integer> d = new HashMap<String, Integer>();
	public static Map<Integer, String> dInverse = new HashMap<Integer, String>();
	public static Map<String, Integer> e = new HashMap<String, Integer>();
	public static Map<Integer, String> eInverse = new HashMap<Integer, String>();
	public static Map<String, Map<Integer, Integer>> f = null;
	public static Map<String, Integer> g = new HashMap<String, Integer>();
	public static Map<Integer, String> gInverse = new HashMap<Integer, String>();

	public static Map<Integer, String> codes = new HashMap<Integer, String>();

	static {

		for (int c = 0; 36 > c; c++) {

			int serverid = 2254 + c * 7;
			int roomnumber = c + 1;
			roomIdsByServerId.put(serverid, roomnumber);
		}

		serverIdsByRoomId = roomIdsByServerId.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		d.put("RoomTemp", 0);
		d.put("RoomSetvalue", 1);
		d.put("FloorTemp", 2);
		d.put("RoomHeatCoolDemand", 3);
		d.put("ActuatorStatus", 4);
		d.put("RoomMode", 5);
		d.put("RoomComfortSetpoint", 6);
		d.put("RoomEcoSetpoint", 7);
		d.put("ListOfDatapoints", 8);
		d.put("RoomRelativeHumidity", 9);

		dInverse = d.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		e.put("RoomSetvalueLowLimit", 0);
		e.put("RoomSetvalueHighLimit", 1);
		e.put("FloorTempMinimum", 2);
		e.put("FloorTempMaximum", 3);
		e.put("CoolingDisable", 4);
		e.put("RoomName", 5);
		e.put("RoomHumiditySetpoint", 6);

		eInverse = e.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		f = new HashMap<String, Map<Integer, Integer>>() {
			{
				put("HumidityLimitIndication", new HashMap<Integer, Integer>() {
					{
						put(1, 14);
						put(2, 16);
						put(3, 18);
					}
				});
				put("ActivateHumidityControl", new HashMap<Integer, Integer>() {
					{
						put(1, 2237);
						put(2, 2241);
						put(3, 2245);
					}
				});
				put("HumidityHysteresis", new HashMap<Integer, Integer>() {
					{
						put(1, 2238);
						put(2, 2242);
						put(3, 2246);
					}
				});
				put("HeatCoolHysteresis", new HashMap<Integer, Integer>() {
					{
						put(1, 2239);
						put(2, 2243);
						put(3, 2247);
					}
				});
				put("DehumidifierControl", new HashMap<Integer, Integer>() {
					{
						put(1, 2240);
						put(2, 2244);
						put(3, 2248);
					}
				});
			}
		};

		g.put("ThermostatOverride", 0);
		g.put("ECOSetpoint", 1);

		gInverse = g.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

		for (int count1 = 1; 36 >= count1; count1++) {
			for (int count2 = 0; 10 > count2; count2++) {
				int num = 9 + 10 * count1 + count2;
				codes.put(num, dInverse.get(count2));
			}
		}

		for (int count1 = 1; 36 >= count1; count1++) {
			for (int count2 = 0; 7 > count2; count2++) {
				int num = 2242 + 7 * count1 + count2;
				codes.put(num, eInverse.get(count2));
			}
		}

		for (int count1 = 1; 36 >= count1; count1++) {
			for (int count2 = 0; 2 > count2; count2++) {
				int num = 2510 + 2 * count1 + count2;
				codes.put(num, gInverse.get(count2));
			}
		}

		codes.put(14, "HumidityLimitIndication");
		codes.put(2237, "ActivateHumidityControl");
		codes.put(2238, "HumidityHysteresis");
		codes.put(2239, "HeatCoolHysteresis");
		codes.put(2240, "DehumidifierControl");
		codes.put(123, "DehumidifierStatus");
		codes.put(16, "HumidityLimitIndication");
		codes.put(2241, "ActivateHumidityControl");
		codes.put(2242, "HumidityHysteresis");
		codes.put(2243, "HeatCoolHysteresis");
		codes.put(2244, "DehumidifierControl");
		codes.put(243, "DehumidifierStatus");
		codes.put(18, "HumidityLimitIndication");
		codes.put(2245, "ActivateHumidityControl");
		codes.put(2246, "HumidityHysteresis");
		codes.put(2247, "HeatCoolHysteresis");
		codes.put(2248, "DehumidifierControl");
		codes.put(363, "DehumidifierStatus");

		for (int c = 0; 36 > c; c++) {

			int serverid = 433 + c * 50;
			int roomnumber = c + 1;
			roomNumFromUFServerId.put(roomnumber, serverid);
		}

	}

	public static int getRoomNumberByServerId(int code) {
		return roomIdsByServerId.get(code);
	}

	public static String getRoomControlName(int code) {
		return codes.get(code);
	}

	public static int getRoomControlServerId(int roomnumber, String valueString) {
		int c = 0;

		int h = 0;

		if (d.get(valueString) != null) {
			h = 1;
		}

		else if (e.get(valueString) != null) {
			h = 2;
		}

		else if (f.get(valueString) != null) {
			h = 3;
		} else if (g.get(valueString) != null) {
			h = 4;
		}

		switch (h) {
		case 1:
			c = 9 + 10 * roomnumber + d.get(valueString).intValue();
			break;
		case 2:
			c = 2242 + 7 * roomnumber + e.get(valueString).intValue();
			break;
		case 3:
			c = 0;

			if (0 < roomnumber && 12 >= roomnumber) {
				c = 1;
			} else {
				if (12 < roomnumber && 24 >= roomnumber) {
					c = 2;
				} else {
					if (24 < roomnumber && 36 >= roomnumber) {
						c = 3;
					} else {
						System.out.println("Error Roomnumber");
					}
				}
			}
			Map<Integer, Integer> keys = f.get(valueString);
			c = keys.get(c);

			break;
		case 4:
			c = 2510 + 2 * roomnumber + g.get(valueString).intValue();
			;
			break;
		default:
			c = 0;
		}
		return c;
	}

	public static int getRoomIndexInZone(int b) {
		int a = 0;
		switch (b) {
		case 1:
		case 13:
		case 25:
			a = 0;
			break;
		case 2:
		case 14:
		case 26:
			a = 1;
			break;
		case 3:
		case 15:
		case 27:
			a = 2;
			break;
		case 4:
		case 16:
		case 28:
			a = 3;
			break;
		case 5:
		case 17:
		case 29:
			a = 4;
			break;
		case 6:
		case 18:
		case 30:
			a = 5;
			break;
		case 7:
		case 19:
		case 31:
			a = 6;
			break;
		case 8:
		case 20:
		case 32:
			a = 7;
			break;
		case 9:
		case 21:
		case 33:
			a = 8;
			break;
		case 10:
		case 22:
		case 34:
			a = 9;
			break;
		case 11:
		case 23:
		case 35:
			a = 10;
			break;
		case 12:
		case 24:
		case 36:
			a = 11;
			break;
		default:
			return 0;
		}
		return a;
	}
	
	public static void main(String[] args) {
		System.out.println("Code 14: "+getRoomControlName(14));
		System.out.println("Code 39: "+getRoomControlName(39));
		System.out.println("Code 40: "+getRoomControlName(40));
		System.out.println("Code 41: "+getRoomControlName(41));
		
		System.out.println("Code 42: "+getRoomControlName(42));
		System.out.println("Code 43: "+getRoomControlName(43));
		System.out.println("Code 44: "+getRoomControlName(44));
		System.out.println("Code 45: "+getRoomControlName(45));
		System.out.println("Code 46: "+getRoomControlName(46));
		System.out.println("Code 47: "+getRoomControlName(47));
		System.out.println("Code 48: "+getRoomControlName(48));
		System.out.println("Code 123: "+getRoomControlName(123));
		System.out.println("Code 2232: "+getRoomControlName(2232));
		System.out.println("Code 2237: "+getRoomControlName(2237));
		System.out.println("Code 2238: "+getRoomControlName(2238));
		System.out.println("Code 2239: "+getRoomControlName(2239));
		System.out.println("Code 2240: "+getRoomControlName(2240));
		System.out.println("Code 2263: "+getRoomControlName(2263));
		System.out.println("Code 2264: "+getRoomControlName(2264));
		System.out.println("Code 2265: "+getRoomControlName(2265));
		
		System.out.println("Code 2266: "+getRoomControlName(2266));
		System.out.println("Code 2267: "+getRoomControlName(2267));
		System.out.println("Code 2268: "+getRoomControlName(2268));
		System.out.println("Code 2269: "+getRoomControlName(2269));
		System.out.println("Code 2516: "+getRoomControlName(2516));
		System.out.println("Code 2517: "+getRoomControlName(2517));

	}

}
