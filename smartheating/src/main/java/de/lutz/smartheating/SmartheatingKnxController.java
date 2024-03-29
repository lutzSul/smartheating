package de.lutz.smartheating;

import static tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat.DPT_TEMPERATURE;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.lutz.smartheating.model.TempData;
import de.lutz.smartheating.model.homebridge.Status;
import de.lutz.smartheating.uponor.UponorKnxClient;
import de.lutz.smartheating.uponor.UponorKnxHelper;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;

@CrossOrigin(origins = "*")
@RestController
public class SmartheatingKnxController {

	final static Logger logger = LoggerFactory.getLogger(SmartheatingKnxController.class);

	@Autowired
	private SmartheatingProperties props;

	public static final Integer HEATING_COOLING_STATE_OFF = 0;
	public static final Integer HEATING_COOLING_STATE_HEAT = 1;
	public static final Integer HEATING_COOLING_STATE_COOL = 2;
	public static final Integer HEATING_COOLING_STATE_AUTO = 3;
	public static final byte[] tempRemove = new byte[] { (byte) 0xFF, (byte) 0x7F };

	@Autowired
	private UponorKnxClient uponorKnxClient;

	public SmartheatingKnxController() {

	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	@RequestMapping("/knx/{floor}/{roomnumber}/status")
	public synchronized Status getStatus(@PathVariable String floor, @PathVariable String roomnumber) {
		logger.info("Lese Status für Flur " + floor + " und Raum " + roomnumber + "...");
		Status result = new Status();
		result.setCurrentHeatingCoolingState(HEATING_COOLING_STATE_AUTO);
		result.setTargetHeatingCoolingState(HEATING_COOLING_STATE_AUTO);

		String groupAddrTemp = floor + "/" + roomnumber + "/" + UponorKnxHelper.ADDR_TEMPERATURE;

		double tempKnx = 0.0d;
		if (props.getUSE_PROXYMAP()) {
			tempKnx = uponorKnxClient.readDoubleViaProxyMap(groupAddrTemp);
		} else {
			tempKnx = uponorKnxClient.readDouble(groupAddrTemp);
		}

		logger.info("Temperatur ist " + tempKnx);
		if (tempKnx == 0.0d) {
			logger.info("Temperatur ist 0 - daher in der Map mit gespeicherten Werten lesen...");
			TempData tempFromMap = uponorKnxClient.getProxyData().get(groupAddrTemp);
			if (tempFromMap != null) {
				tempKnx = tempFromMap.getTemperature().doubleValue();
				logger.info("...gefunden");
			}
		}

		result.setCurrentTemperature(round(tempKnx, 1));

		String groupAddrSetpoint = floor + "/" + roomnumber + "/" + UponorKnxHelper.ADDR_SETPOINT;
		String groupAddrRemoteSetpoint = floor + "/" + roomnumber + "/" + UponorKnxHelper.ADDR_REMOTE_SETPOINT;

		double targetTempKnx = 0.0d;
		if (props.getUSE_PROXYMAP()) {
			targetTempKnx = uponorKnxClient.readDoubleViaProxyMap(groupAddrSetpoint);
		} else {
			targetTempKnx = uponorKnxClient.readDouble(groupAddrSetpoint);
		}

		logger.info("Soll-Temperatur ist " + targetTempKnx);
		if (targetTempKnx == 0.0d) {
			logger.info("Soll-Temperatur ist 0 - daher in der Map mit gespeicherten Werten lesen...");
			TempData targetTempFromMap = uponorKnxClient.getProxyData().get(groupAddrSetpoint);
			if (targetTempFromMap != null) {
				targetTempKnx = targetTempFromMap.getTemperature().doubleValue();
				logger.info("...gefunden");
			}
		}
		TempData remoteSetpoint = uponorKnxClient.getProxyData().get(groupAddrRemoteSetpoint);

		if (remoteSetpoint != null) {
			logger.info("Remote-Setpoint in der Map gefunden: " + remoteSetpoint.getTemperature().doubleValue());
			result.setTargetTemperature(remoteSetpoint.getTemperature().doubleValue());
			result.setTargetHeatingCoolingState(HEATING_COOLING_STATE_HEAT);
		} else {
			result.setTargetTemperature(targetTempKnx);
			logger.info("keinen Remote-Setpoint in der Map gefunden.");
		}

		boolean heat = false;
		String groupAddrActuator = floor + "/" + roomnumber + "/" + UponorKnxHelper.ADDR_ACTUATOR_STATUS;

		if (props.getUSE_PROXYMAP()) {
			heat = uponorKnxClient.readBooleanViaProxyMap(groupAddrActuator);
		} else {
			heat = uponorKnxClient.readBoolean(groupAddrActuator);
		}

		if (heat)
			result.setCurrentHeatingCoolingState(HEATING_COOLING_STATE_HEAT);
		else
			result.setCurrentHeatingCoolingState(HEATING_COOLING_STATE_COOL);
		return result;
	}

	@RequestMapping("/knx/{floor}/{roomnumber}/targetHeatingCoolingState")
	public synchronized boolean setTargetHeatingCoolingState(@PathVariable String floor,
			@PathVariable String roomnumber, @RequestParam Integer value) {
		if (HEATING_COOLING_STATE_AUTO.equals(value)) {
			logger.info("Raumtemperatur für Raum " + roomnumber + " einstellen auf Automatik");
			String groupAddr = floor + "/" + roomnumber + "/" + UponorKnxHelper.ADDR_REMOTE_SETPOINT;

			String groupAddrSetpoint = floor + "/" + roomnumber + "/" + UponorKnxHelper.ADDR_SETPOINT;
			double targetTempKnx = uponorKnxClient.readDouble(groupAddrSetpoint);
			logger.info("Setpoint ist " + targetTempKnx);
			if (targetTempKnx == 0.0d) {
				logger.info("Setpoint ist 0 - Suche in Map...");
				TempData targetTempFromMap = uponorKnxClient.getProxyData().get(groupAddrSetpoint);
				if (targetTempFromMap != null) {
					logger.info("Gefunden: " + targetTempFromMap.getTemperature().doubleValue());
					targetTempKnx = targetTempFromMap.getTemperature().doubleValue();
				}
			}
			boolean success = false;
			if (targetTempKnx == 0.0d) {
				logger.info("Setpoint ist weiterhin 0 - Lösche Remote-Setpoint...");
				success = removeRemoteSetpoint(groupAddr);
			} else {
				logger.info("Stelle Remote-Setpoint auf Setpoint aus Thermometer");
				success = uponorKnxClient.writeDouble(groupAddr, targetTempKnx);
			}

			if (success) {
				logger.info("Erfolgreich - Lösche Remote-Setpoint aus Map...");
				uponorKnxClient.getProxyData().remove(groupAddr);
				if (props.getUSE_INFLUXDB()) {
					uponorKnxClient.getInfluxDBAccess().saveValue(groupAddr, targetTempKnx);
				}
			}
		}
		return true;
	}

	private boolean removeRemoteSetpoint(String groupAddr) {
		DPTXlator t;
		try {
			t = TranslatorTypes.createTranslator(DPT_TEMPERATURE);
			t.setData(tempRemove);

			uponorKnxClient.writeValue(groupAddr, t);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@RequestMapping("/knx/{floor}/{roomnumber}/targetTemperature")
	public synchronized boolean setTargetTemperature(@PathVariable String floor, @PathVariable String roomnumber,
			@RequestParam Double value) {
		logger.info("Raumtemperatur für Raum " + roomnumber + " einstellen auf " + value + " Grad!");
		String groupAddr = floor + "/" + roomnumber + "/" + UponorKnxHelper.ADDR_REMOTE_SETPOINT;
		boolean success = uponorKnxClient.writeDouble(groupAddr, value);
		if (success) {
			TempData tempData = new TempData(value, TempData.TYPE_TEMP_REMOTE_SETPOINT, groupAddr);
			uponorKnxClient.getProxyData().put(groupAddr, tempData);
			if (props.getUSE_INFLUXDB()) {
				uponorKnxClient.getInfluxDBAccess().saveValue(groupAddr, value);
			}
		}
		return true;
	}

	@RequestMapping("/knx/{floor}/{roomnumber}/coolingThresholdTemperature")
	public boolean setCoolingThresholdTemperature(@PathVariable String floor, @PathVariable Integer roomnumber,
			@RequestParam Integer value) {
		return true;
	}

	@RequestMapping("/knx/{floor}/{roomnumber}/heatingThresholdTemperature")
	public boolean setHeatingThresholdTemperature(@PathVariable String floor, @PathVariable Integer roomnumber,
			@RequestParam Integer value) {
		return true;
	}

}
