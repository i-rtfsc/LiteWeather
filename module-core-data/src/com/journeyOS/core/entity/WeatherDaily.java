package com.journeyOS.core.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class WeatherDaily {

    @SerializedName("code")
    public String code;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("fxLink")
    public String fxLink;
    @SerializedName("daily")
    public List<DailyBean> daily;
    @SerializedName("refer")
    public ReferBean refer;

    public static class ReferBean {
        @SerializedName("sources")
        public List<String> sources;
        @SerializedName("license")
        public List<String> license;
    }

    public static class DailyBean {
        @SerializedName("fxDate")
        public String fxDate;
        @SerializedName("sunrise")
        public String sunrise;
        @SerializedName("sunset")
        public String sunset;
        @SerializedName("moonrise")
        public String moonrise;
        @SerializedName("moonset")
        public String moonset;
        @SerializedName("moonPhase")
        public String moonPhase;
        @SerializedName("tempMax")
        public String tempMax;
        @SerializedName("tempMin")
        public String tempMin;
        @SerializedName("iconDay")
        public String iconDay;
        @SerializedName("textDay")
        public String textDay;
        @SerializedName("iconNight")
        public String iconNight;
        @SerializedName("textNight")
        public String textNight;
        @SerializedName("wind360Day")
        public String wind360Day;
        @SerializedName("windDirDay")
        public String windDirDay;
        @SerializedName("windScaleDay")
        public String windScaleDay;
        @SerializedName("windSpeedDay")
        public String windSpeedDay;
        @SerializedName("wind360Night")
        public String wind360Night;
        @SerializedName("windDirNight")
        public String windDirNight;
        @SerializedName("windScaleNight")
        public String windScaleNight;
        @SerializedName("windSpeedNight")
        public String windSpeedNight;
        @SerializedName("humidity")
        public String humidity;
        @SerializedName("precip")
        public String precip;
        @SerializedName("pressure")
        public String pressure;
        @SerializedName("vis")
        public String vis;
        @SerializedName("cloud")
        public String cloud;
        @SerializedName("uvIndex")
        public String uvIndex;
    }
}
