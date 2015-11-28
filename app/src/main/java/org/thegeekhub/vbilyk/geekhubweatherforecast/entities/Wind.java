package org.thegeekhub.vbilyk.geekhubweatherforecast.entities;

import io.realm.RealmObject;

public class Wind extends RealmObject {

    private double speed;

    private double deg;

    public Wind() {
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDeg() {
        return deg;
    }

    public void setDeg(double deg) {
        this.deg = deg;
    }
}
