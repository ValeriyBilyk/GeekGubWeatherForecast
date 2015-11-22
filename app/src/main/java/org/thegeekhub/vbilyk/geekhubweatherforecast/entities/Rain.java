package org.thegeekhub.vbilyk.geekhubweatherforecast.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Rain implements Parcelable {

    public static final Parcelable.Creator<Rain> CREATOR = new Parcelable.Creator<Rain>() {
        public Rain createFromParcel(Parcel source) {
            return new Rain(source);
        }

        public Rain[] newArray(int size) {
            return new Rain[size];
        }
    };
    @SerializedName("3h")
    private double threeHours;

    public Rain() {
    }

    protected Rain(Parcel in) {
        this.threeHours = in.readDouble();
    }

    public double getThreeHours() {
        return threeHours;
    }

    public void setThreeHours(double threeHours) {
        this.threeHours = threeHours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.threeHours);
    }
}
