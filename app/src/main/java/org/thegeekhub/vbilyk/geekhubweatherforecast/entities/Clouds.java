package org.thegeekhub.vbilyk.geekhubweatherforecast.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Clouds implements Parcelable {

    public static final Creator<Clouds> CREATOR = new Creator<Clouds>() {
        public Clouds createFromParcel(Parcel source) {
            return new Clouds(source);
        }

        public Clouds[] newArray(int size) {
            return new Clouds[size];
        }
    };
    private int all;

    public Clouds() {
    }

    protected Clouds(Parcel in) {
        this.all = in.readInt();
    }

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.all);
    }
}
