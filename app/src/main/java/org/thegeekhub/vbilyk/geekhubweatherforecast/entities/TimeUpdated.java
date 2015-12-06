package org.thegeekhub.vbilyk.geekhubweatherforecast.entities;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TimeUpdated extends RealmObject {

    @PrimaryKey
    private int city;
    private Date updatedAt;

    public TimeUpdated() {
        updatedAt = new Date();
    }

    public TimeUpdated(int cityId) {
        city = cityId;
        updatedAt = new Date();
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
