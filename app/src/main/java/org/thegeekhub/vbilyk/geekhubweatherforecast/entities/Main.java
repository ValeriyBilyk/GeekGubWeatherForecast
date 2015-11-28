package org.thegeekhub.vbilyk.geekhubweatherforecast.entities;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Main extends RealmObject {

    private double temp;
    @SerializedName("temp_min")
    private double tempMin;
    @SerializedName("temp_max")
    private double tempMax;
    private double pressure;
    @SerializedName("sea_level")
    private double seaLevel;
    @SerializedName("grnd_level")
    private double grndLevel;
    private int humidity;
    @SerializedName("temp_kf")
    private double tempKf;

    public Main() {
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getSeaLevel() {
        return seaLevel;
    }

    public void setSeaLevel(double seaLevel) {
        this.seaLevel = seaLevel;
    }

    public double getGrndLevel() {
        return grndLevel;
    }

    public void setGrndLevel(double grndLevel) {
        this.grndLevel = grndLevel;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getTempKf() {
        return tempKf;
    }

    public void setTempKf(double tempKf) {
        this.tempKf = tempKf;
    }
}
