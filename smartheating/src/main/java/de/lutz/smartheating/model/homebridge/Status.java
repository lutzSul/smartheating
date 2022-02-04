
package de.lutz.smartheating.model.homebridge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Status {

    @SerializedName("targetHeatingCoolingState")
    @Expose
    private Integer targetHeatingCoolingState;
    @SerializedName("targetTemperature")
    @Expose
    private Double targetTemperature;
    @SerializedName("currentHeatingCoolingState")
    @Expose
    private Integer currentHeatingCoolingState;
    @SerializedName("currentTemperature")
    @Expose
    private Double currentTemperature;
    @SerializedName("currentRelativeHumidity")
    @Expose
    private Double currentRelativeHumidity;
    @SerializedName("coolingThresholdTemperature")
    @Expose
    private Double coolingThresholdTemperature;
    @SerializedName("heatingThresholdTemperature")
    @Expose
    private Double heatingThresholdTemperature;

    public Integer getTargetHeatingCoolingState() {
        return targetHeatingCoolingState;
    }

    public void setTargetHeatingCoolingState(Integer targetHeatingCoolingState) {
        this.targetHeatingCoolingState = targetHeatingCoolingState;
    }

    public Double getTargetTemperature() {
        return targetTemperature;
    }

    public void setTargetTemperature(Double targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public Integer getCurrentHeatingCoolingState() {
        return currentHeatingCoolingState;
    }

    public void setCurrentHeatingCoolingState(Integer currentHeatingCoolingState) {
        this.currentHeatingCoolingState = currentHeatingCoolingState;
    }

    public Double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(Double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public Double getCurrentRelativeHumidity() {
        return currentRelativeHumidity;
    }

    public void setCurrentRelativeHumidity(Double currentRelativeHumidity) {
        this.currentRelativeHumidity = currentRelativeHumidity;
    }

    public Double getCoolingThresholdTemperature() {
        return coolingThresholdTemperature;
    }

    public void setCoolingThresholdTemperature(Double coolingThresholdTemperature) {
        this.coolingThresholdTemperature = coolingThresholdTemperature;
    }

    public Double getHeatingThresholdTemperature() {
        return heatingThresholdTemperature;
    }

    public void setHeatingThresholdTemperature(Double heatingThresholdTemperature) {
        this.heatingThresholdTemperature = heatingThresholdTemperature;
    }

}
