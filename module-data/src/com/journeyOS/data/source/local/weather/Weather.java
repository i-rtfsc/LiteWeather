package com.journeyOS.data.source.local.weather;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.journeyOS.data.source.local.base.DBConfigs;

@Entity(tableName = DBConfigs.Weather.TABLE_NAME, primaryKeys = {DBConfigs.Weather.LOCATION_ID})
public class Weather {

    @NonNull
    @ColumnInfo(name = DBConfigs.Weather.LOCATION_ID)
    public String locationId = "";

    @ColumnInfo(name = DBConfigs.Weather.ADM1)
    public String adm1 = "";

    @ColumnInfo(name = DBConfigs.Weather.CITY_NAME)
    public String cityName = "";

    @ColumnInfo(name = DBConfigs.Weather.AIR)
    public String air = "";

    @ColumnInfo(name = DBConfigs.Weather.INDICES)
    public String indices = "";

    @ColumnInfo(name = DBConfigs.Weather.NOW)
    public String now = "";

    @ColumnInfo(name = DBConfigs.Weather.SUN)
    public String sun = "";

    @ColumnInfo(name = DBConfigs.Weather.DAILY)
    public String daily = "";

    @ColumnInfo(name = DBConfigs.Weather.HOURLY)
    public String hourly = "";

    @ColumnInfo(name = DBConfigs.Weather.TIME)
    public String time = "";

}
