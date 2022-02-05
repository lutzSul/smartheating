package de.lutz.smartheating.model;

import java.time.LocalDateTime;

public class TempData {

	public static final int TYPE_TEMP = 0;

	public static final int TYPE_TEMP_SETPOINT = 1;

	public static final int TYPE_TEMP_REMOTE_SETPOINT = 2;

	private String address;

	private Double temperature;

	private LocalDateTime timestamp;

	private int type = 0;

	public TempData() {
		this.timestamp = LocalDateTime.now();
	}

	public TempData(Double temperature) {
		this.timestamp = LocalDateTime.now();
		this.temperature = temperature;
	}

	public TempData(Double temperature, int type) {
		this.timestamp = LocalDateTime.now();
		this.temperature = temperature;
		this.type = type;
	}

	public TempData(Double temperature, int type, String address) {
		this.timestamp = LocalDateTime.now();
		this.temperature = temperature;
		this.type = type;
		this.address = address;
	}

	public void setTypeTemperature() {
		this.type = TYPE_TEMP;
	}

	public void setTypeSetpoint() {
		this.type = TYPE_TEMP_SETPOINT;
	}

	public void setTypeRemoteSetpoint() {
		this.type = TYPE_TEMP_REMOTE_SETPOINT;
	}

	public boolean isSetpoint() {
		return TYPE_TEMP_SETPOINT == this.type;
	}

	public boolean isTemperature() {
		return TYPE_TEMP == this.type;
	}

	public boolean isRemoteSetpoint() {
		return TYPE_TEMP_REMOTE_SETPOINT == this.type;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
