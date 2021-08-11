package com.journeyOS.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sun {

    @SerializedName("code")
    public String code;
    @SerializedName("updateTime")
    public String updateTime;
    @SerializedName("fxLink")
    public String fxLink;
    @SerializedName("sunrise")
    public String sunrise;
    @SerializedName("sunset")
    public String sunset;
    @SerializedName("refer")
    public ReferBean refer;

    public static class ReferBean {
        @SerializedName("sources")
        public List<String> sources;
        @SerializedName("license")
        public List<String> license;
    }
}
