package com.journeyOS.core.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AirNow {

    @SerializedName("code")
    public String code;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("fxLink")
    public String fxLink;
    @SerializedName("now")
    public NowBean now;
    @SerializedName("station")
    public List<StationBean> station;
    @SerializedName("refer")
    public ReferBean refer;

    public static class NowBean {
        @SerializedName("pubTime")
        public String pubTime;
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("level")
        public String level;
        @SerializedName("category")
        public String category;
        @SerializedName("primary")
        public String primary;
        @SerializedName("pm10")
        public String pm10;
        @SerializedName("pm2p5")
        public String pm2p5;
        @SerializedName("no2")
        public String no2;
        @SerializedName("so2")
        public String so2;
        @SerializedName("co")
        public String co;
        @SerializedName("o3")
        public String o3;
    }

    public static class ReferBean {
        @SerializedName("sources")
        public List<String> sources;
        @SerializedName("license")
        public List<String> license;
    }

    public static class StationBean {
        @SerializedName("pubTime")
        public String pubTime;
        @SerializedName("name")
        public String name;
        @SerializedName("id")
        public String id;
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("level")
        public String level;
        @SerializedName("category")
        public String category;
        @SerializedName("primary")
        public String primary;
        @SerializedName("pm10")
        public String pm10;
        @SerializedName("pm2p5")
        public String pm2p5;
        @SerializedName("no2")
        public String no2;
        @SerializedName("so2")
        public String so2;
        @SerializedName("co")
        public String co;
        @SerializedName("o3")
        public String o3;
    }
}
