package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.core.entity.AirNow;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.widget.weather.AqiView;

public class WeatherAirViewModel extends MultiItemViewModel<WeatherViewModel> {
    public ObservableField<AirNow.NowBean> airNowEntity = new ObservableField<>();
    public ObservableField<AqiView.AqiData> aqiEntity = new ObservableField<>();

    public WeatherAirViewModel(@NonNull WeatherViewModel viewModel, AirNow airNow) {
        super(viewModel);
        parseAirNow(airNow);
    }

    private void parseAirNow(AirNow airNow) {
        if (airNow == null) {
            return;
        }
        if (airNow.now == null) {
            return;
        }

        airNowEntity.set(airNow.now);

        AqiView.AqiData aqiData = new AqiView.AqiData();
        aqiData.aqi = airNow.now.aqi;
        aqiData.pm25 = airNow.now.pm2p5;
        aqiData.pm10 = airNow.now.pm10;
        aqiData.so2 = airNow.now.so2;
        aqiData.no2 = airNow.now.no2;
        aqiData.quality = airNow.now.category;

        aqiEntity.set(aqiData);
    }
}