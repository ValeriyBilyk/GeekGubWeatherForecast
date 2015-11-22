package org.thegeekhub.vbilyk.geekhubweatherforecast.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Forecast implements Parcelable {

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        public Forecast createFromParcel(Parcel source) {
            return new Forecast(source);
        }

        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };
    @SerializedName("dt_txt")
    private Date date;
    private Main main;
    private List<Weather> weather;
    private Clouds clouds;
    private Wind wind;
    private Rain rain;

    public Forecast() {
    }

    protected Forecast(Parcel in) {
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.main = in.readParcelable(Main.class.getClassLoader());
        this.weather = in.createTypedArrayList(Weather.CREATOR);
        this.clouds = in.readParcelable(Clouds.class.getClassLoader());
        this.wind = in.readParcelable(Wind.class.getClassLoader());
        this.rain = in.readParcelable(Rain.class.getClassLoader());
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Clouds clouds) {
        this.clouds = clouds;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Rain getRain() {
        return rain;
    }

    public void setRain(Rain rain) {
        this.rain = rain;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date != null ? date.getTime() : -1);
        dest.writeParcelable(this.main, 0);
        dest.writeTypedList(weather);
        dest.writeParcelable(this.clouds, 0);
        dest.writeParcelable(this.wind, 0);
        dest.writeParcelable(this.rain, 0);
    }
}

