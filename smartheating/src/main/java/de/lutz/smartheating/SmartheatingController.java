package de.lutz.smartheating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.lutz.smartheating.model.homebridge.Status;
import de.lutz.smartheating.uponor.UponorClient;
import de.lutz.smartheating.uponor.UponorHelper;

@CrossOrigin(origins = "*")
@RestController
public class SmartheatingController {

	final static Logger logger = LoggerFactory.getLogger(SmartheatingController.class);

	public static final Integer HEATING_COOLING_STATE_OFF = 0;
	public static final Integer HEATING_COOLING_STATE_HEAT = 1;
	public static final Integer HEATING_COOLING_STATE_COOL = 2;
	public static final Integer HEATING_COOLING_STATE_AUTO = 3;

	ScheduledExecutorService executorService;

	public SmartheatingController() {

	}

	@RequestMapping("/{installation}/{roomnumber}/status")
	public Status getStatus(@PathVariable String installation, @PathVariable Integer roomnumber) {
		Status result = new Status();
		result.setCurrentHeatingCoolingState(HEATING_COOLING_STATE_COOL);
		result.setTargetHeatingCoolingState(HEATING_COOLING_STATE_AUTO);
		result.setTargetTemperature(0.0d);
		result.setCurrentTemperature(0.0d);
		boolean override = false;
		List<Integer> parameter = new ArrayList<Integer>();

		parameter.add(UponorHelper.getRoomControlServerId(roomnumber, UponorHelper.PARAM_ROOM_SETVALUE));
		parameter.add(UponorHelper.getRoomControlServerId(roomnumber, UponorHelper.PARAM_ROOM_TEMP));
		parameter.add(UponorHelper.getRoomControlServerId(roomnumber, UponorHelper.PARAM_THERMOSTAT_OVERRIDE));
		parameter.add(UponorHelper.getRoomControlServerId(roomnumber, UponorHelper.PARAM_ACTUATOR_STATUS));

		Map<Integer, Double> ergebnisse = new HashMap<Integer, Double>();
		ergebnisse = UponorClient.readValuesFromApi(installation, parameter);
		logger.debug("Daten für Raum " + roomnumber + ": " + ergebnisse.toString());

		for (Map.Entry<Integer, Double> entry : ergebnisse.entrySet()) {
			String nameWert = UponorHelper.getRoomControlName(entry.getKey());
			if (UponorHelper.PARAM_ROOM_SETVALUE.equals(nameWert)) {
				if (!override) {
					if (!override)
						result.setTargetTemperature(entry.getValue());
				}
			}
			if (UponorHelper.PARAM_ROOM_TEMP.equals(nameWert)) {
				result.setCurrentTemperature(entry.getValue());
			}
			if (UponorHelper.PARAM_THERMOSTAT_OVERRIDE.equals(nameWert)) {
				if (entry.getValue() != null) {
					if (!entry.getValue().equals(255d)) {
						override = true;
						result.setTargetHeatingCoolingState(HEATING_COOLING_STATE_HEAT);
						result.setTargetTemperature(entry.getValue());
					}
				}
			}
			if (UponorHelper.PARAM_ACTUATOR_STATUS.equals(nameWert)) {
				if (entry.getValue() != null) {
					if (!entry.getValue().equals(0d)) {
						result.setCurrentHeatingCoolingState(HEATING_COOLING_STATE_HEAT);
					}
				}
			}
		}

		return result;
	}

	@RequestMapping("/{installation}/rooms")
	public Map<Integer, Integer> getRooms(@PathVariable String installation) {
		Map<Integer, Integer> roomIdsWithServerId = UponorClient.getRoomIds(installation);
		return roomIdsWithServerId;
	}

	@RequestMapping("/{installation}/{roomnumber}/targetHeatingCoolingState")
	public boolean setTargetHeatingCoolingState(@PathVariable String installation, @PathVariable Integer roomnumber,
			@RequestParam Integer value) {
		if (HEATING_COOLING_STATE_AUTO.equals(value)) {
			logger.debug("Raumtemperatur für Raum " + roomnumber + " einstellen auf Automatik");
			return setTargetTemperature(installation, roomnumber, 255d);
		}
		return true;
	}

	@RequestMapping("/{installation}/{roomnumber}/targetTemperature")
	public boolean setTargetTemperature(@PathVariable String installation, @PathVariable Integer roomnumber,
			@RequestParam Double value) {
		logger.debug("Raumtemperatur für Raum " + roomnumber + " einstellen auf " + value + " Grad!");
		Map<Integer, Double> writeValues = new HashMap<Integer, Double>();
		writeValues.put(UponorHelper.getRoomControlServerId(roomnumber, UponorHelper.PARAM_THERMOSTAT_OVERRIDE), value);
		return UponorClient.writeValuesToApi(installation, writeValues);
	}

	@RequestMapping("/{installation}/{roomnumber}/targetTemperatureTherm")
	public boolean setTargetTemperatureTherm(@PathVariable String installation, @PathVariable Integer roomnumber,
			@RequestParam Double value) {
		logger.debug("Raumtemperatur des Thermostats für Raum " + roomnumber + " einstellen auf " + value + " Grad!");
		Map<Integer, Double> writeValues = new HashMap<Integer, Double>();
		writeValues.put(UponorHelper.getRoomControlServerId(roomnumber, UponorHelper.PARAM_ROOM_SETVALUE), value);
		return UponorClient.writeValuesToApi(installation, writeValues);
	}

	@RequestMapping("/{installation}/{roomnumber}/setValue")
	public boolean setValue(@PathVariable String installation, @PathVariable Integer roomnumber,
			@RequestParam String key, @RequestParam Double value) {
		logger.debug("Wertänderung für Raum " + roomnumber + " - setze " + key + " auf " + value);
		Map<Integer, Double> writeValues = new HashMap<Integer, Double>();
		writeValues.put(UponorHelper.getRoomControlServerId(roomnumber, key), value);
		return UponorClient.writeValuesToApi(installation, writeValues);
	}

	@RequestMapping("/{installation}/{roomnumber}/getValue")
	public Map<Integer, Double> getValue(@PathVariable String installation, @PathVariable Integer roomnumber,
			@RequestParam String key) {
		logger.debug("Lese Wert für Raum " + roomnumber + " mit Key " + key);
		List<Integer> parameter = new ArrayList<Integer>();

		parameter.add(UponorHelper.getRoomControlServerId(roomnumber, key));

		Map<Integer, Double> ergebnisse = new HashMap<Integer, Double>();
		ergebnisse = UponorClient.readValuesFromApi(installation, parameter);
		logger.debug("Daten für Raum " + roomnumber + ": " + ergebnisse.toString());
		return ergebnisse;
	}

	@RequestMapping("/{installation}/{roomnumber}/coolingThresholdTemperature")
	public boolean setCoolingThresholdTemperature(@PathVariable Integer roomnumber, @RequestParam Integer value) {
		return true;
	}

	@RequestMapping("/{installation}/{roomnumber}/heatingThresholdTemperature")
	public boolean setHeatingThresholdTemperature(@PathVariable Integer roomnumber, @RequestParam Integer value) {
		return true;
	}

}
