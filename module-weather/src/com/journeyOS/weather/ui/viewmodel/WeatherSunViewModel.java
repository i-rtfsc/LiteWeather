package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.core.entity.NowBase;
import com.journeyOS.core.entity.Sun;
import com.journeyOS.core.entity.WeatherDaily;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.widget.weather.AstroView;

//日出日落要求用商业版本API，可是用开发版API也可以...啊这...
//日出日落经常查不到数据，发现每天的天气里用日出日落信息（WeatherDaily）
//考虑在做把WeatherDaily传进来，如果Sun数据为空则优先解析WeatherDaily
//实在都没有数据最后才写死06:00-19:00
public class WeatherSunViewModel extends MultiItemViewModel<WeatherViewModel> {
    public ObservableField<AstroView.AstroData> astroEntity = new ObservableField<>();

    public WeatherSunViewModel(@NonNull WeatherViewModel viewModel, NowBase nowBase, WeatherDaily weatherDaily, Sun sun) {
        super(viewModel);
        parseWeatherNow(nowBase, weatherDaily, sun);
    }

    private void parseWeatherNow(NowBase nowBase, WeatherDaily weatherDaily, Sun sun) {
        if (nowBase == null) {
            return;
        }
        if (nowBase.now == null) {
            return;
        }

        AstroView.AstroData astro = new AstroView.AstroData();
        astro.sunSet = "19:00";
        astro.sunRise = "06:00";

        if (sun != null) {
            //"moonrise": "04:31"
            //返回的数据变了，所以解析时间的时候注意适配
            astro.sunSet = TimeUtils.parseHour(sun.sunset);
            astro.sunRise = TimeUtils.parseHour(sun.sunrise);
        } else {
            if (weatherDaily != null && weatherDaily.daily != null) {
                for (WeatherDaily.DailyBean dailyBean : weatherDaily.daily) {
                    String today = TimeUtils.getMonthDay();
                    String weatherDay = TimeUtils.parseMonthDay(dailyBean.fxDate);
                    if (today.equals(weatherDay)) {
                        astro.sunSet = TimeUtils.parseHour(dailyBean.sunset);
                        astro.sunRise = TimeUtils.parseHour(dailyBean.sunrise);
//                    astro.pressure = dailyBean.pressure;
//                    astro.windSpeed = dailyBean.windSpeedDay;
//                    astro.windDirection = dailyBean.windDirDay;
                    }
                }
            }
        }

        astro.pressure = nowBase.now.pressure;
        astro.windSpeed = nowBase.now.windSpeed;
        astro.windDirection = nowBase.now.windDir;
        astroEntity.set(astro);
        //更新天气
        viewModel.updateSky(nowBase.now.icon, astro.sunSet, astro.sunRise);
    }

}