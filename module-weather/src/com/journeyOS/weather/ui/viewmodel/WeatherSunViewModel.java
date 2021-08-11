package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.core.entity.NowBase;
import com.journeyOS.core.entity.WeatherDaily;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.widget.weather.AstroView;

//日出日落要求用商业版本API，可是用开发版API也可以...啊这...
//日出日落经常查不到数据，发现每天的天气里有日出日落信息（WeatherDaily）
//考虑在做把WeatherDaily传进来，如果Sun数据为空则优先解析WeatherDaily
//而且还有月升月落信息，
public class WeatherSunViewModel extends MultiItemViewModel<WeatherViewModel> {
    public ObservableField<AstroView.AstroData> astroEntity = new ObservableField<>();

    public WeatherSunViewModel(@NonNull WeatherViewModel viewModel, WeatherDaily weatherDaily, NowBase nowBase) {
        super(viewModel);
        parseWeatherNow(weatherDaily, nowBase);
    }

    private void parseWeatherNow(WeatherDaily weatherDaily, NowBase nowBase) {
        if (weatherDaily == null) {
            return;
        }
        if (weatherDaily.daily.size() == 0) {
            return;
        }

        AstroView.AstroData astro = new AstroView.AstroData();
        //fake data
        astro.sunSet = "19:00";
        astro.sunRise = "06:00";
        astro.moonSet = "21:20";
        astro.moonRise = "07:30";

        String cloud = null;
        if (nowBase != null) {
            //用now的天气相当比较准
            cloud = nowBase.now.icon;
        }

        for (WeatherDaily.DailyBean dailyBean : weatherDaily.daily) {
            String today = TimeUtils.getMonthDay();
            String weatherDay = TimeUtils.parseMonthDay(dailyBean.fxDate);
            if (today.equals(weatherDay)) {
                if (cloud == null) {
                    cloud = dailyBean.cloud;
                }
                astro.sunSet = TimeUtils.parseHour(dailyBean.sunset);
                astro.sunRise = TimeUtils.parseHour(dailyBean.sunrise);

                astro.pressure = dailyBean.pressure;
                astro.windSpeed = dailyBean.windSpeedDay;
                astro.windDirection = dailyBean.windDirDay;

                astro.moonSet = TimeUtils.parseHour(dailyBean.moonset);
                astro.moonRise = TimeUtils.parseHour(dailyBean.moonrise);
                astro.moonPhase = dailyBean.moonPhase;
                astro.windDirectionNight = dailyBean.windDirNight;
                astro.windSpeedNight = dailyBean.windSpeedNight;
            }
        }

        astroEntity.set(astro);
        //在这里更新天空皮肤，为啥了根据太阳时间确定是否用夜间天空
        viewModel.updateSky(cloud, astro.sunSet, astro.sunRise);
    }

}