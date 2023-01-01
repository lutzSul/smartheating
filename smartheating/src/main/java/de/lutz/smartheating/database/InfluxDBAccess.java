package de.lutz.smartheating.database;

import java.time.Instant;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxDBAccess {

	InfluxDBClient influxDBClient;

	final static String DPT_HEATING = "heating";

	final static String DPT_COOLING = "cooling";

	final static Integer ON = 1;

	final static Integer OFF = 0;

	private String url;

	private String token;

	private String org;

	private String bucket;

	public InfluxDBAccess(String url, String token, String org, String bucket) {
		this.url = url;
		this.token = token;
		this.org = org;
		this.bucket = bucket;
		initDBConnection();
	}

	public void initDBConnection() {
		influxDBClient = InfluxDBClientFactory.create(this.url, this.token.toCharArray(), this.org, this.bucket);
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
