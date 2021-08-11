package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.core.data.source.local.city.City;
import com.journeyOS.core.entity.NowBase;
import com.journeyOS.liteframework.base.MultiItemViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherNowViewModel extends MultiItemViewModel<WeatherViewModel> {
    public ObservableField<NowBase.NowBean> nowEntity = new ObservableField<>();
    public ObservableField<City> cityEvent = new ObservableField<>();
    public ObservableField<String> updateTime = new ObservableField<>();

    public WeatherNowViewModel(@NonNull WeatherViewModel viewModel, NowBase nowBase, City city) {
        super(viewModel);
        if (nowBase != null) {
            nowEntity.set(nowBase.now);
            parseTime(nowBase.updateTime);
        }
        if (city != null) {
            cityEvent.set(city);
        }
    }

    public void parseTime(String time) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date = df.parse(time);
            updateTime.set((new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}