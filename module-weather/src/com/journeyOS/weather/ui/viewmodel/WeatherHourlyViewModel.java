package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.data.entity.WeatherHourly;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.widget.weather.HourlyForecastView;

import java.util.ArrayList;
import java.util.List;

public class WeatherHourlyViewModel extends MultiItemViewModel<WeatherViewModel> {
    public ObservableField<List<HourlyForecastView.HourlyData>> hourlyDataList = new ObservableField<>();

    public WeatherHourlyViewModel(@NonNull WeatherViewModel viewModel, WeatherHourly weatherHourly) {
        super(viewModel);
        parseWeatherHourly(weatherHourly);
    }

    private void parseWeatherHourly(WeatherHourly weatherHourly) {
        if (weatherHourly == null) {
            return;
        }
        if (weatherHourly.hourly == null) {
            return;
        }

        List<WeatherHourly.HourlyBean> hoursEntityList = weatherHourly.hourly;
        List<HourlyForecastView.HourlyData> hourlyDatas = new ArrayList<>();

        int all_max = Integer.MIN_VALUE;
        int all_min = Integer.MAX_VALUE;
        //只要间隔3个小时的数据
        for (int i = 0; i < hoursEntityList.size(); i = i + 3) {
            WeatherHourly.HourlyBean hoursEntity = hoursEntityList.get(i);
            HourlyForecastView.HourlyData hourlyData = new HourlyForecastView.HourlyData();
            int tmp = Integer.valueOf(hoursEntity.temp);
            if (all_max < tmp) {
                all_max = tmp;
            }
            if (all_min > tmp) {
                all_min = tmp;
            }
            hourlyData.temperature = tmp;
            hourlyData.date = TimeUtils.parseHour(hoursEntity.fxTime);
            hourlyData.windLevel = hoursEntity.windScale;
            hourlyData.humidity = hoursEntity.humidity;
            hourlyDatas.add(hourlyData);
        }
        float all_distance = Math.abs(all_max - all_min);
        float average_distance = (all_max + all_min) / 2f;

        for (HourlyForecastView.HourlyData d : hourlyDatas) {
            d.offsetPercent = (d.temperature - average_distance) / all_distance;
        }

        hourlyDataList.set(hourlyDatas);
    }
}