package de.lutz.smartheating.database;

import java.time.Instant;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import de.lutz.smartheating.Properties;

public class InfluxDBAccess {

	InfluxDBClient influxDBClient;

	final static String DPT_HEATING = "heating";

	final static String DPT_COOLING = "cooling";

	final static Integer ON = 1;

	final static Integer OFF = 0;

	public InfluxDBAccess() {
		initDBConnection();
	}

	public void initDBConnection() {
		influxDBClient = InfluxDBClientFactory.create(Properties.INFLUXDB_URL, Properties.INFLUXDB_TOKEN.toCharArray(),
				Properties.INFLUXDB_ORG, Properties.INFLUXDB_BUCKET);
	}

	public void saveValue(String groupAdress, Double value) {

		if (influxDBClient == null) {
			initDBConnection();
		}

		WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

		if (value >= 0.0d) {
			Point point = Point.measurement(groupAdress).addField("temperature", value)
					.time(Instant.now().toEpochMilli(), WritePrecision.MS);

			try {
				writeApi.writePoint(point);
			} catch (Exception e) {
				initDBConnection();
				writeApi.writePoint(point);
			}
		}
	}

	public void saveActuatorValue(String groupAdress, String value) {

		if (influxDBClient == null) {
			initDBConnection();
		}

		WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();

		if (value != null && !"".equals(value)) {
			if (DPT_HEATING.equals(value)) {
				Point point = Point.measurement(groupAdress).addField("actuatorStatus", ON)
						.time(Instant.now().toEpochMilli(), WritePrecision.MS);
				try {
					writeApi.writePoint(point);
				} catch (Exception e) {
					initDBConnection();
					writeApi.writePoint(point);
				}
			}

			if (DPT_COOLING.equals(value)) {
				Point point = Point.measurement(groupAdress).addField("actuatorStatus", OFF)
						.time(Instant.now().toEpochMilli(), WritePrecision.MS);
				try {
					writeApi.writePoint(point);
				} catch (Exception e) {
					initDBConnection();
					writeApi.writePoint(point);
				}
			}

		}
	}

}
