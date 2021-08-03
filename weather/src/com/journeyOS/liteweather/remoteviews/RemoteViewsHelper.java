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


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.core.Version;
import com.journeyOS.core.bean.weather.WeatherData;
import com.journeyOS.core.base.ResourceProvider;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.repository.WeatherRepository;
import com.journeyOS.liteweather.ui.WeatherActivity;

import java.lang.reflect.Field;

import static com.journeyOS.base.Constant.ALARM_ALLOW;
import static com.journeyOS.base.Constant.NOTIFICATION_ALLOW;
import static com.journeyOS.base.Constant.NOTIFICATION_THEME;


public class RemoteViewsHelper {

    private static final int NOTICE_ID_TYPE_0 = R.string.app_name;
    private static final int NOTICE_ID_TYPE_ALARM = 0x0001;


    public static void showNotification(Service service) {
        WeatherData weatherData = WeatherRepository.getInstance().getCachedWeatherData();
        if (weatherData == null) {
            return;
        }
        boolean show = SpUtils.getInstant().getBoolean(NOTIFICATION_ALLOW, true);
        if (!show) {
            return;
        }

        Notification notification = RemoteViewsHelper.generateCustomNotification(service);
        service.startForeground(NOTICE_ID_TYPE_0, notification);
    }

    public static void updateNotification(Service service) {
        WeatherData weatherData = WeatherRepository.getInstance().getCachedWeatherData();
        if (weatherData == null) {
            return;
        }

        boolean show = SpUtils.getInstant().getBoolean(NOTIFICATION_ALLOW, true);
        if (!show || service == null) {
            return;
        }
        try {
            Field mBase = ContextWrapper.class.getDeclaredField("mBase");
            mBase.setAccessible(true);
            Context context = (Context) mBase.get(service);
            if (context == null) {
                return;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = RemoteViewsHelper.generateCustomNotification(service);
        notificationManager.notify(NOTICE_ID_TYPE_0, notification);
    }

    public static void stopNotification(Service service) {
        NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTICE_ID_TYPE_0);
        notificationManager.cancel(NOTICE_ID_TYPE_ALARM);
        service.stopForeground(true);
    }

    public static void stopAlarm(Service service) {
        NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTICE_ID_TYPE_ALARM);
    }

    @TargetApi(16)
    private static void generateAlarmNotification(Context context) {

        if (!(SpUtils.getInstant().getBoolean(ALARM_ALLOW, true) && SpUtils.getInstant().getBoolean(NOTIFICATION_ALLOW, true))) {
            return;
        }
        //和风天气免费用户拿不到警告数据，不做此处做具体功能。

    }

    @TargetApi(16)
    private static Notification generateCustomNotification(Context context) {

        NotificationCompat.Builder
                builder = new NotificationCompat
                .Builder(context)
                .setContent(getNotificationContentView(context))
                .setPriority(NotificationCompat.PRIORITY_MAX).setOngoing(true);

        if (Version.buildVersion() >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.svg_core_icon);
        } else {
            builder.setSmallIcon(R.drawable.svg_core_icon);
        }


        Notification notification = builder.build();
        //wrap_content fit
        if (Version.buildVersion() >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = getNotificationContentView(context);
        }

        generateAlarmNotification(context);

        return notification;
    }

    private static RemoteViews getNotificationContentView(Context context) {
        int themeId = ResourceProvider.getNotificationThemeId(SpUtils.getInstant().getInt(NOTIFICATION_THEME, 1));

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), themeId);

        Intent intent = new Intent(context, WeatherActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, (int) SystemClock.uptimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notification_container, contentIntent);

        WeatherData weatherData = WeatherRepository.getInstance().getCachedWeatherData();

        if (weatherData == null) {
            return remoteViews;
        }

        WeatherData.BasicEntity basic = weatherData.basic;
        WeatherData.NowEntity nowEntity = weatherData.now;
        remoteViews.setTextViewText(R.id.weather_temp, nowEntity.temperature);
        remoteViews.setTextViewText(R.id.weather_status, nowEntity.weather);
        remoteViews.setTextViewText(R.id.city, basic.city);
        remoteViews.setTextViewText(R.id.post_time, TimeUtils.getHourMinute() + " 更新");
        remoteViews.setImageViewResource(R.id.weather_icon, ResourceProvider.getIconId(nowEntity.weather));

        Intent updateIntent = new Intent(WeatherWidgetProvider.UPDATE_ACTION);
        context.sendBroadcast(updateIntent);

        return remoteViews;
    }

}
