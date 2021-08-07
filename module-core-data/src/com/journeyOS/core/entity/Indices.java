package com.journeyOS.core.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Indices {

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

    public static class DailyBean implements Parcelable {
        @SerializedName("date")
        public String date;
        @SerializedName("type")
        public String type;
        @SerializedName("name")
        public String name;
        @SerializedName("level")
        public String level;
        @SerializedName("category")
        public String category;
        @SerializedName("text")
        public String text;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        protected DailyBean(Parcel in) {
            date = in.readString();
            type = in.readString();
            name = in.readString();
            level = in.readString();
            category = in.readString();
            text = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(date);
            dest.writeString(type);
            dest.writeString(name);
            dest.writeString(level);
            dest.writeString(category);
            dest.writeString(text);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DailyBean> CREATOR = new Creator<DailyBean>() {
            @Override
            public DailyBean createFromParcel(Parcel in) {
                return new DailyBean(in);
            }

            @Override
            public DailyBean[] newArray(int size) {
                return new DailyBean[size];
            }
        };
    }
}
