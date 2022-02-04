package de.lutz.smartheating.database;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

import de.lutz.smartheating.Properties;
import okhttp3.OkHttpClient;

public class InfluxDBAccess {

	InfluxDB influxDB;

	public InfluxDBAccess() {
		initDBConnection();
	}

	public void initDBConnection() {
		OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder().connectTimeout(40, TimeUnit.SECONDS)
				.readTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS);
		influxDB = InfluxDBFactory.connect(Properties.INFLUXDB_URL, Properties.INFLUXDB_USERNAME,
				Properties.INFLUXDB_PASSWORD, okHttpClientBuilder);
		influxDB.setDatabase(Properties.INFLUXDB_DBNAME);
	}

	public void saveValue(String groupAdress, Double value) {

		if (influxDB == null) {
			initDBConnection();
		}

		// String rpName = "aRetentionPolicy";
		// influxDB.createRetentionPolicy(rpName, dbName, "30d", "30m", 2, true);
		// influxDB.setRetentionPolicy(rpName);
		// TODO: Im Hintergrund schreiben??
		// influxDB.enableBatch(BatchOptions.DEFAULTS);
		Point point = Point.measurement(groupAdress).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.addField("temperature", value).build();
		try {
			influxDB.write(point);
		} catch (Exception e) {
			initDBConnection();
			influxDB.write(point);
		}
	}

	public Double readValue(String groupAdress) {
		if (influxDB == null) {
			initDBConnection();
		}

		Query query = new Query("SELECT * FROM " + groupAdress + " ORDER BY DESC LIMIT 1", Properties.INFLUXDB_DBNAME);
		QueryResult queryResult = null;

		try {
			queryResult = influxDB.query(query);
		} catch (Exception e) {
			initDBConnection();
			queryResult = influxDB.query(query);
		}
		if (queryResult != null) {
			InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
			List<HeatingDataPoint> results = resultMapper.toPOJO(queryResult, HeatingDataPoint.class);

			if (results != null && results.size() > 0) {
				return results.get(0).getTemperature();
			}
		}
		return 0.0d;

	}

}
