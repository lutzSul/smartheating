package de.lutz.smartheating.uponor;

import static tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat.DPT_TEMPERATURE;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lutz.smartheating.Properties;
import de.lutz.smartheating.database.InfluxDBAccess;
import de.lutz.smartheating.model.TempData;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

public class KnxListener implements ProcessListener {

	final static Logger logger = LoggerFactory.getLogger(KnxListener.class);

	private Map<String, TempData> proxyTemp;

	private InfluxDBAccess influxDBAccess;

	public KnxListener(Map<String, TempData> proxyTemp) {
		this.proxyTemp = proxyTemp;
		if (Properties.USE_INFLUXDB) {
			influxDBAccess = new InfluxDBAccess();
		}
	}

	public KnxListener(Map<String, TempData> proxyTemp, InfluxDBAccess influxDBAccess) {
		this.proxyTemp = proxyTemp;
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

					TempData oldSetpoint = proxyTemp.get(e.getDestination().toString());
					TempData tempData = new TempData(t.getNumericValue(), TempData.TYPE_TEMP_SETPOINT,
							e.getDestination().toString());
					if (oldSetpoint == null) {
						logger.debug("Keinen alten Setpoint gefunden...");
						proxyTemp.put(e.getDestination().toString(), tempData);
					} else {
						if (oldSetpoint.getTemperature().doubleValue() != t.getNumericValue()) {
							if ("GroupWrite".equals(svc)) {
								logger.debug("Neuer Setpoint wird geschrieben - Remote-Setpoint aus Speicher löschen");
								String hauptgruppe = UponorKnxHelper.getHauptgruppe(e.getDestination().toString());
								String mittelgruppe = UponorKnxHelper.getMittelgruppe(e.getDestination().toString());
								proxyTemp.remove(
										hauptgruppe + "/" + mittelgruppe + "/" + UponorKnxHelper.ADDR_REMOTE_SETPOINT);
							}
							logger.debug("Neuen Setpoint in Map speichern");
							proxyTemp.put(e.getDestination().toString(), tempData);
						}
					}

				}

				if (UponorKnxHelper.isCurrentTemperature(e.getDestination().toString())) {
					logger.debug("Temperatur ist " + t.getValue() + " (" + t.getNumericValue() + ")");
					TempData tempData = new TempData(t.getNumericValue(), TempData.TYPE_TEMP,
							e.getDestination().toString());
					proxyTemp.put(e.getDestination().toString(), tempData);
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

}
