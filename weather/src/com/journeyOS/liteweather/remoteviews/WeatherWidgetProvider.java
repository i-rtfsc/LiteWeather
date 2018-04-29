/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.liteweather.remoteviews;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.weatherprovider.IWeatherProvider;
import com.journeyOS.core.api.weatherprovider.WeatherData;
import com.journeyOS.core.base.StatusDataResource;
import com.journeyOS.liteweather.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class WeatherWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_ACTION = "update_ui";

    public WeatherWidgetProvider() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (UPDATE_ACTION.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            updateWidget(context, appWidgetManager);
        }

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidget(context, appWidgetManager);
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager) {
        WeatherData weatherData = null;
        LiveData<StatusDataResource<WeatherData>> liveWeatherData = CoreManager.getImpl(IWeatherProvider.class).getWeatherData();
        if (liveWeatherData.getValue() != null && liveWeatherData.getValue().isSucceed()) {
            weatherData = liveWeatherData.getValue().data;
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        if (weatherData == null) {
            return;
        }

        WeatherData.NowEntity nowEntity = weatherData.now;
        WeatherData.AqiEntity aqi = weatherData.aqi;
        WeatherData.DailyEntity dailyEntity = weatherData.daily.get(0);
        WeatherData.BasicEntity basic = weatherData.basic;

        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        SimpleDateFormat day = new SimpleDateFormat("MM月dd日");
        Date date = new Date();
        remoteViews.setTextViewText(R.id.widget_time, time.format(date));
        remoteViews.setTextViewText(R.id.widget_day, day.format(date) + "," + TimeUtils.parseWeek(dailyEntity.date));

        remoteViews.setTextViewText(R.id.widget_weather, nowEntity.weather + " " + nowEntity.temperature + context.getResources().getString(R.string.unit_t));
        remoteViews.setTextViewText(R.id.widget_city, basic.city);
        remoteViews.setTextViewText(R.id.widget_aqi, context.getResources().getString(R.string.weather_air_quality) + ":" + aqi.aqi + "(" + aqi.quality + ")");
    }

}
