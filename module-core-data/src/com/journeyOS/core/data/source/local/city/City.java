package com.journeyOS.core.data.source.local.city;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.journeyOS.core.data.source.local.base.DBConfigs;

@Entity(tableName = DBConfigs.City.TABLE_NAME, primaryKeys = {DBConfigs.City.LOCATION_ID})
public class City {

    @NonNull
    @ColumnInfo(name = DBConfigs.City.LOCATION_ID)
    public String locationId = "";

    @ColumnInfo(name = DBConfigs.City.LOCATION_NAME_EN)
    public String locationNameEn;

    @ColumnInfo(name = DBConfigs.City.CITY_NAME)
    public String cityName;

    @ColumnInfo(name = DBConfigs.City.COUNTRY_CODE)
    public String countryCode;

    @ColumnInfo(name = DBConfigs.City.COUNTRY_EN)
    public String countryEn;

    @ColumnInfo(name = DBConfigs.City.ADM1_EN)
    public String adm1En;

    @ColumnInfo(name = DBConfigs.City.ADM1)
    public String adm1;

    @ColumnInfo(name = DBConfigs.City.ADM2_EN)
    public String adm2En;

    @ColumnInfo(name = DBConfigs.City.ADM2)
    public String adm2;

    @ColumnInfo(name = DBConfigs.City.LONGITUDE)
    public String longitude;

    @ColumnInfo(name = DBConfigs.City.LATITUDE)
    public String latitude;


    @Ignore
    @Override
    public String toString() {
        return "City{" +
                "locationId='" + locationId + '\'' +
                ", locationNameEn='" + locationNameEn + '\'' +
                ", cityName='" + cityName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryEn='" + countryEn + '\'' +
                ", adm1En='" + adm1En + '\'' +
                ", adm1='" + adm1 + '\'' +
                ", adm2En='" + adm2En + '\'' +
                ", adm2='" + adm2 + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }
}
