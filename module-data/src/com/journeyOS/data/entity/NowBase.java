package com.journeyOS.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class NowBase {


    @SerializedName("code")
    public String code;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("fxLink")
    public String fxLink;
    @SerializedName("now")
    public NowBean now;
    @SerializedName("refer")
    public ReferBean refer;

    public static class NowBean {
        @SerializedName("obsTime")
        public String obsTime;
        @SerializedName("temp")
        public String temp;
        @SerializedName("feelsLike")
        public String feelsLike;
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
        @SerializedName("precip")
        public String precip;
        @SerializedName("pressure")
        public String pressure;
        @SerializedName("vis")
        public String vis;
        @SerializedName("cloud")
        public String cloud;
        @SerializedName("dew")
        public String dew;

        @Override
        public String toString() {
            return "NowBean{" +
                    "obsTime='" + obsTime + '\'' +
                    ", temp='" + temp + '\'' +
                    ", feelsLike='" + feelsLike + '\'' +
                    ", icon='" + icon + '\'' +
                    ", text='" + text + '\'' +
                    ", wind360='" + wind360 + '\'' +
                    ", windDir='" + windDir + '\'' +
                    ", windScale='" + windScale + '\'' +
                    ", windSpeed='" + windSpeed + '\'' +
                    ", humidity='" + humidity + '\'' +
                    ", precip='" + precip + '\'' +
                    ", pressure='" + pressure + '\'' +
                    ", vis='" + vis + '\'' +
                    ", cloud='" + cloud + '\'' +
                    ", dew='" + dew + '\'' +
                    '}';
        }
    }

    public static class ReferBean {
        @SerializedName("sources")
        public List<String> sources;
        @SerializedName("license")
        public List<String> license;
    }
}
