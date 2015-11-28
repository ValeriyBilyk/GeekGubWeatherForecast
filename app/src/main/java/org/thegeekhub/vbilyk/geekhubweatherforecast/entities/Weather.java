package org.thegeekhub.vbilyk.geekhubweatherforecast.entities;

import io.realm.RealmObject;

/**
 * Created by Admin on 20.11.2015
 */
public class Weather extends RealmObject {
    private long id;
    private String main;
    private String description;
    private String icon;

    public Weather() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
