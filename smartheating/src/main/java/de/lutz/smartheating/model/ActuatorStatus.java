package de.lutz.smartheating.model;

import java.time.LocalDateTime;

public class ActuatorStatus {

	private String address;

	private Boolean status;

	private LocalDateTime timestamp;

	public ActuatorStatus() {
		this.timestamp = LocalDateTime.now();
	}

	public ActuatorStatus(Boolean status) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
	}

	public ActuatorStatus(Boolean status, String address) {
		this.timestamp = LocalDateTime.now();
		this.status = status;
		this.address = address;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}
