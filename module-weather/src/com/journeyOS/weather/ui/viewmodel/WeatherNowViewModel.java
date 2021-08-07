package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.core.data.source.local.city.City;
import com.journeyOS.core.entity.NowBase;
import com.journeyOS.liteframework.base.MultiItemViewModel;

public class WeatherNowViewModel extends MultiItemViewModel<WeatherViewModel> {
    public ObservableField<NowBase.NowBean> nowEntity = new ObservableField<>();
    public ObservableField<City> cityEvent = new ObservableField<>();

    public WeatherNowViewModel(@NonNull WeatherViewModel viewModel, NowBase nowBase, City city) {
        super(viewModel);
        if (nowBase != null) {
            nowEntity.set(nowBase.now);
        }
        if (city != null) {
            cityEvent.set(city);
        }
    }

}