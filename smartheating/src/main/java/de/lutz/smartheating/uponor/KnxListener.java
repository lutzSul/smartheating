package de.lutz.smartheating.uponor;

import static tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat.DPT_TEMPERATURE;
import static tuwien.auto.calimero.dptxlator.DPTXlatorBoolean.DPT_HEAT_COOL;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lutz.smartheating.database.InfluxDBAccess;
import de.lutz.smartheating.model.ActuatorStatus;
import de.lutz.smartheating.model.TempData;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

public class KnxListener implements ProcessListener {

	final static Logger logger = LoggerFactory.getLogger(KnxListener.class);

	final static String DPT_HEATING = "heating";

	final static String DPT_COOLING = "cooling";

	private Map<String, TempData> proxyTemp;

	private Map<String, ActuatorStatus> proxyActuator;

	private InfluxDBAccess influxDBAccess;

	public KnxListener(Map<String, TempData> proxyTemp, Map<String, ActuatorStatus> proxyActuator) {
		this.proxyTemp = proxyTemp;
		this.proxyActuator = proxyActuator;
	}

	public KnxListener(Map<String, TempData> proxyTemp, Map<String, ActuatorStatus> proxyActuator, InfluxDBAccess influxDBAccess) {
		this.proxyTemp = proxyTemp;
		this.influxDBAccess = influxDBAccess;
		this.proxyActuator = proxyActuator;
	}

	public void run() {
	}

	@Override
	public void groupWrite(final ProcessEvent e) {
		process("GroupWrite", e);
	}

	@Override
	public void groupReadRequest(final ProcessEvent e) {
		process("GroupReadRequest", e);
	}

	@Override
	public void groupReadResponse(final ProcessEvent e) {
		process("GroupReadResponse", e);
	}

	@Override
	public void detached(final DetachEvent e) {
	}

	private void process(final String svc, final ProcessEvent e) {
		try {
			logger.info(svc + " :" + e.getSourceAddr() + "->" + e.getDestination() + " " + ": ");
			if (UponorKnxHelper.isDouble(e.getDestination().toString())) {
				DPTXlator t;

				t = TranslatorTypes.createTranslator(DPT_TEMPERATURE);
				t.setData(e.getASDU());

				if (influxDBAccess != null) {
					influxDBAccess.saveValue(e.getDestination().toString(), t.getNumericValue());
				}

				if (UponorKnxHelper.isSetpoint(e.getDestination().toString())) {
					logger.info("Temperatur Setpoint ist " + t.getValue() + " (" + t.getNumericValue() + ")");

					TempData oldSetpoint = proxyTemp.get(e.getDestination().toString());
					TempData tempData = new TempData(t.getNumericValue(), TempData.TYPE_TEMP_SETPOINT,
							e.getDestination().toString());
					if (oldSetpoint == null) {
						logger.info("Keinen alten Setpoint gefunden...");
						proxyTemp.put(e.getDestination().toString(), tempData);
					} else {
						if (oldSetpoint.getTemperature().doubleValue() != t.getNumericValue()) {
							if ("GroupWrite".equals(svc)) {
								logger.info("Neuer Setpoint wird geschrieben - Remote-Setpoint aus Speicher löschen");
								String hauptgruppe = UponorKnxHelper.getHauptgruppe(e.getDestination().toString());
								String mittelgruppe = UponorKnxHelper.getMittelgruppe(e.getDestination().toString());
								proxyTemp.remove(
										hauptgruppe + "/" + mittelgruppe + "/" + UponorKnxHelper.ADDR_REMOTE_SETPOINT);
								if (influxDBAccess != null) {
									logger.info("Remote-Setpoint in Influx-DB auf 0 setzen...");
									influxDBAccess.saveValue(hauptgruppe + "/" + mittelgruppe + "/"
											+ UponorKnxHelper.ADDR_REMOTE_SETPOINT, 0.0d);
								}
							}
							logger.info("Neuen Setpoint in Map speichern");
							proxyTemp.put(e.getDestination().toString(), tempData);
						}
					}

				}

				if (UponorKnxHelper.isCurrentTemperature(e.getDestination().toString())) {
					logger.info("Temperatur ist " + t.getValue() + " (" + t.getNumericValue() + ")");
					TempData tempData = new TempData(t.getNumericValue(), TempData.TYPE_TEMP,
							e.getDestination().toString());
					proxyTemp.put(e.getDestination().toString(), tempData);
				}

			}

			if (UponorKnxHelper.isActuatorStatus(e.getDestination().toString())) {
				DPTXlator t;

				t = TranslatorTypes.createTranslator(DPT_HEAT_COOL);
				t.setData(e.getASDU());
				logger.info("Actuator Status ist " + t.getValue());

				if (influxDBAccess != null) {
					influxDBAccess.saveActuatorValue(e.getDestination().toString(), t.getValue());
				}

				if (DPT_HEATING.equals(t.getValue())) {
					ActuatorStatus actuatorStatus = new ActuatorStatus(true, e.getDestination().toString());
					proxyActuator.put(e.getDestination().toString(), actuatorStatus);
				}
				if (DPT_COOLING.equals(t.getValue())) {
					ActuatorStatus actuatorStatus = new ActuatorStatus(false, e.getDestination().toString());
					proxyActuator.put(e.getDestination().toString(), actuatorStatus);
				}
			}

		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	public Map<String, TempData> getProxyTemp() {
		return proxyTemp;
	}

	public void setProxyTemp(Map<String, TempData> proxyTemp) {
		this.proxyTemp = proxyTemp;
	}

	public InfluxDBAccess getInfluxDBAccess() {
		return influxDBAccess;
	}

	public void setInfluxDBAccess(InfluxDBAccess influxDBAccess) {
		this.influxDBAccess = influxDBAccess;
	}

}
