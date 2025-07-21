package com.example.optifi;

import android.os.Parcel;
import android.os.Parcelable;

public class WiFiScanResult implements Parcelable {
    public double latitude;
    public double longitude;
    public int signalStrength;

    public WiFiScanResult(double lat, double lng, int signal) {
        this.latitude = lat;
        this.longitude = lng;
        this.signalStrength = signal;
    }

    protected WiFiScanResult(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        signalStrength = in.readInt();
    }

    public static final Creator<WiFiScanResult> CREATOR = new Creator<WiFiScanResult>() {
        @Override
        public WiFiScanResult createFromParcel(Parcel in) {
            return new WiFiScanResult(in);
        }

        @Override
        public WiFiScanResult[] newArray(int size) {
            return new WiFiScanResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(signalStrength);
    }
}