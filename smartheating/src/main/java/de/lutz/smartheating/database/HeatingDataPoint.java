package de.lutz.smartheating.database;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "temp")
public class HeatingDataPoint {

	@Column(name = "time")
	private Instant time;

	@Column(name = "temperature")
	private Double temperature;

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

}
