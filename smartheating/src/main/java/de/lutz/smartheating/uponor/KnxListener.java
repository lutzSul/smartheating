package de.lutz.smartheating.uponor;

import static tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat.DPT_TEMPERATURE;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lutz.smartheating.Properties;
import de.lutz.smartheating.database.InfluxDBAccess;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

public class KnxListener implements ProcessListener {

	final static Logger logger = LoggerFactory.getLogger(KnxListener.class);

	private Map<String, Double> tempSetpoints;

	private Map<String, Double> tempRemoteSetpoints;

	private Map<String, Double> temperatures;

	private InfluxDBAccess influxDBAccess;

	public KnxListener(Map<String, Double> tempSetpoints, Map<String, Double> temperatures,
			Map<String, Double> tempRemoteSetpoints) {
		this.tempSetpoints = tempSetpoints;
		this.temperatures = temperatures;
		this.tempRemoteSetpoints = tempRemoteSetpoints;
		if (Properties.USE_INFLUXDB) {
			influxDBAccess = new InfluxDBAccess();
		}
	}

	public KnxListener(Map<String, Double> tempSetpoints, Map<String, Double> temperatures,
			Map<String, Double> tempRemoteSetpoints, InfluxDBAccess influxDBAccess) {
		this.tempSetpoints = tempSetpoints;
		this.temperatures = temperatures;
		this.tempRemoteSetpoints = tempRemoteSetpoints;
		this.influxDBAccess = influxDBAccess;
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
			logger.debug(svc + " :" + e.getSourceAddr() + "->" + e.getDestination() + " " + ": ");
			if (UponorKnxHelper.isDouble(e.getDestination().toString())) {
				DPTXlator t;

				t = TranslatorTypes.createTranslator(DPT_TEMPERATURE);
				t.setData(e.getASDU());

				if (Properties.USE_INFLUXDB && influxDBAccess != null) {
					influxDBAccess.saveValue(e.getDestination().toString(), t.getNumericValue());
				}

				if (UponorKnxHelper.isSetpoint(e.getDestination().toString())) {
					logger.debug("Temperatur Setpoint ist " + t.getValue() + " (" + t.getNumericValue() + ")");

					Double oldSetpoint = tempSetpoints.get(e.getDestination().toString());
					if (oldSetpoint == null) {
						logger.debug("Keinen alten Setpoint gefunden...");
						tempSetpoints.put(e.getDestination().toString(), t.getNumericValue());
					} else {
						if (oldSetpoint.doubleValue() != t.getNumericValue()) {
							if ("GroupWrite".equals(svc)) {
								logger.debug("Neuer Setpoint wird geschrieben - Remote-Setpoint aus Speicher löschen");
								String hauptgruppe = UponorKnxHelper.getHauptgruppe(e.getDestination().toString());
								String mittelgruppe = UponorKnxHelper.getMittelgruppe(e.getDestination().toString());
								tempRemoteSetpoints.remove(
										hauptgruppe + "/" + mittelgruppe + "/" + UponorKnxHelper.ADDR_REMOTE_SETPOINT);
							}
							logger.debug("Neuen Setpoint in Map speichern");
							tempSetpoints.put(e.getDestination().toString(), t.getNumericValue());
						}
					}

				}

				if (UponorKnxHelper.isCurrentTemperature(e.getDestination().toString())) {
					logger.debug("Temperatur ist " + t.getValue() + " (" + t.getNumericValue() + ")");
					temperatures.put(e.getDestination().toString(), t.getNumericValue());
				}

			}

		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	public Map<String, Double> getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(Map<String, Double> temperatures) {
		this.temperatures = temperatures;
	}
}
