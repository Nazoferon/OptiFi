// WiFiNetwork.java - Data Model
package com.example.optifi;

import android.os.Parcel;
import android.os.Parcelable;

public class WiFiNetwork implements Parcelable {
    private String ssid;
    private String bssid;
    private int signalLevel;
    private int frequency;
    private String capabilities;
    private SignalQuality quality;

    public enum SignalQuality {
        EXCELLENT("Відмінно"),
        GOOD("Добре"),
        FAIR("Задовільно"),
        POOR("Погано");

        private final String ukrainianName;

        SignalQuality(String ukrainianName) {
            this.ukrainianName = ukrainianName;
        }

        public String getUkrainianName() {
            return ukrainianName;
        }
    }

    public WiFiNetwork(String ssid, String bssid, int signalLevel, int frequency, String capabilities) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.signalLevel = signalLevel;
        this.frequency = frequency;
        this.capabilities = capabilities;
        this.quality = calculateQuality();
    }

    private SignalQuality calculateQuality() {
        if (signalLevel >= -50) return SignalQuality.EXCELLENT;
        else if (signalLevel >= -60) return SignalQuality.GOOD;
        else if (signalLevel >= -70) return SignalQuality.FAIR;
        else return SignalQuality.POOR;
    }

    // Getters
    public String getSsid() { return ssid; }
    public String getBssid() { return bssid; }
    public int getSignalLevel() { return signalLevel; }
    public int getFrequency() { return frequency; }
    public String getCapabilities() { return capabilities; }
    public SignalQuality getQuality() { return quality; }

    public String getFrequencyBand() {
        return frequency > 3000 ? "5GHz" : "2.4GHz";
    }

    // Parcelable implementation
    protected WiFiNetwork(Parcel in) {
        ssid = in.readString();
        bssid = in.readString();
        signalLevel = in.readInt();
        frequency = in.readInt();
        capabilities = in.readString();
        quality = SignalQuality.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ssid);
        dest.writeString(bssid);
        dest.writeInt(signalLevel);
        dest.writeInt(frequency);
        dest.writeString(capabilities);
        dest.writeString(quality.name());
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<WiFiNetwork> CREATOR = new Creator<WiFiNetwork>() {
        @Override
        public WiFiNetwork createFromParcel(Parcel in) {
            return new WiFiNetwork(in);
        }

        @Override
        public WiFiNetwork[] newArray(int size) {
            return new WiFiNetwork[size];
        }
    };
}