package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.data.entity.WeatherDaily;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.widget.weather.DailyForecastView;

import java.util.Arrays;
import java.util.List;

public class WeatherDailyViewModel extends MultiItemViewModel<WeatherViewModel> {
    public ObservableField<List<DailyForecastView.DailyData>> dailyDataList = new ObservableField<>();

    public WeatherDailyViewModel(@NonNull WeatherViewModel viewModel, WeatherDaily weatherDaily) {
        super(viewModel);
        parseWeatherDaily(weatherDaily);
    }

    private void parseWeatherDaily(WeatherDaily weatherDaily) {
        if (weatherDaily == null) {
            return;
        }
        if (weatherDaily.daily == null) {
            return;
        }

        List<WeatherDaily.DailyBean> daily = weatherDaily.daily;

        int all_max = Integer.MIN_VALUE;
        int all_min = Integer.MAX_VALUE;

        DailyForecastView.DailyData[] dailyDatas = new DailyForecastView.DailyData[daily.size()];

        for (int i = 0; i < daily.size(); i++) {
            DailyForecastView.DailyData data = new DailyForecastView.DailyData();
            WeatherDaily.DailyBean dailyEntity = daily.get(i);

            int max = Integer.parseInt(dailyEntity.tempMax);
            int min = Integer.parseInt(dailyEntity.tempMin);
            if (all_max < max) {
                all_max = max;
            }
            if (all_min > min) {
                all_min = min;
            }

            data.Tmax = max;
            data.Tmin = min;
            data.date = TimeUtils.prettyDate(dailyEntity.fxDate);
            data.pop = dailyEntity.precip;
            data.weather = dailyEntity.textDay;

            dailyDatas[i] = data;
        }

        float all_distance = Math.abs(all_max - all_min);
        float average_distance = (all_max + all_min) / 2f;
        for (DailyForecastView.DailyData d : dailyDatas) {
            d.maxOffsetPercent = (d.Tmax - average_distance) / all_distance;
            d.minOffsetPercent = (d.Tmin - average_distance) / all_distance;
        }

        dailyDataList.set(Arrays.asList(dailyDatas));
    }
}