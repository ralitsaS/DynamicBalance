package com.example.bobloos.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bob.loos on 13/05/16.
 */
public class MonitorDataModel {
    private int id;
    private String sensorId;
    private String userId;
    private String sensorVal;
    private String sensorUpdateTime;
    private String measurementTime;
    private String accuracy;
    private String hour;
    private String day;

    public MonitorDataModel () {

    }

    public  MonitorDataModel (int id, String sensorId, String userId, String sensorVal, String sensorUpdateTime, String accuracy, String measurementTime, String hour, String day) {
        this.id = id;
        this.sensorId = sensorId;
        this.userId = userId;
        this.sensorVal = sensorVal;
        this.sensorUpdateTime = sensorUpdateTime;
        this.accuracy = accuracy;
        this.measurementTime = measurementTime;
        this.hour = hour;
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSensorVal() {
        return sensorVal;
    }

    public void setSensorVal(String sensorVal) {
        this.sensorVal = sensorVal;
    }

    public String getSensorUpdateTime() {
        return sensorUpdateTime;
    }

    public void setSensorUpdateTime(String sensorUpdateTime) {
        this.sensorUpdateTime = sensorUpdateTime;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getMeasurementTime() {
        return measurementTime;
    }

    public void setMeasurementTime(String measurementTime) {
        this.measurementTime = measurementTime;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "MonitorData [id=" + id + ", userId=" + userId + ", sensorId=" + sensorId+ ", sensorVal=" + sensorVal +
                ", sensorUpdateTime=" + getDate(Long.getLong(sensorUpdateTime, 1)/1000000L) + ", accuracy=" + accuracy+
                ", measurementTime=" + measurementTime + ", hour=" + hour + ", day=" + day + "]";
    }

    private String getDate(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "00:00";
        }
    }
}