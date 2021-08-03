package com.journeyOS.liteweather.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherHourly {

    @SerializedName("code")
    public String code;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("fxLink")
    public String fxLink;
    @SerializedName("hourly")
    public List<HourlyBean> hourly;
    @SerializedName("refer")
    public ReferBean refer;

    public static class ReferBean {
        @SerializedName("sources")
        public List<String> sources;
        @SerializedName("license")
        public List<String> license;
    }

    public static class HourlyBean {
        @SerializedName("fxTime")
        public String fxTime;
        @SerializedName("temp")
        public String temp;
        @SerializedName("icon")
        public String icon;
        @SerializedName("text")
        public String text;
        @SerializedName("wind360")
        public String wind360;
        @SerializedName("windDir")
        public String windDir;
        @SerializedName("windScale")
        public String windScale;
        @SerializedName("windSpeed")
        public String windSpeed;
        @SerializedName("humidity")
        public String humidity;
        @SerializedName("pop")
        public String pop;
        @SerializedName("precip")
        public String precip;
        @SerializedName("pressure")
        public String pressure;
        @SerializedName("cloud")
        public String cloud;
        @SerializedName("dew")
        public String dew;

        @Override
        public String toString() {
            return "HourlyBean{" +
                    "fxTime='" + fxTime + '\'' +
                    ", temp='" + temp + '\'' +
                    ", icon='" + icon + '\'' +
                    ", text='" + text + '\'' +
                    ", wind360='" + wind360 + '\'' +
                    ", windDir='" + windDir + '\'' +
                    ", windScale='" + windScale + '\'' +
                    ", windSpeed='" + windSpeed + '\'' +
                    ", humidity='" + humidity + '\'' +
                    ", pop='" + pop + '\'' +
                    ", precip='" + precip + '\'' +
                    ", pressure='" + pressure + '\'' +
                    ", cloud='" + cloud + '\'' +
                    ", dew='" + dew + '\'' +
                    '}';
        }
    }
}
