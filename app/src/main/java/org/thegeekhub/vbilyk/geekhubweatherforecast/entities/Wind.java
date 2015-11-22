package org.thegeekhub.vbilyk.geekhubweatherforecast.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Wind implements Parcelable {

    public static final Parcelable.Creator<Wind> CREATOR = new Parcelable.Creator<Wind>() {
        public Wind createFromParcel(Parcel source) {
            return new Wind(source);
        }

        public Wind[] newArray(int size) {
            return new Wind[size];
        }
    };
    private double speed;

    //    @SerializedName("dt")
//    private Date date;
//    private Wind wind;
//    private List<Wind> winds;
    private double deg;

    public Wind() {
    }

    protected Wind(Parcel in) {
        this.speed = in.readDouble();
        this.deg = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.speed);
        dest.writeDouble(this.deg);
    }
}
