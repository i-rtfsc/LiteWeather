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

package com.journeyOS.liteweather.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.journeyOS.base.network.NetWork;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.weatherprovider.IWeatherProvider;
import com.journeyOS.core.api.weatherprovider.WeatherData;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.base.StatusDataResource;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.literouter.Message;
import com.journeyOS.literouter.Router;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.litetask.TaskScheduler;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.sky.SkyType;
import com.journeyOS.liteweather.utils.WeatherUitls;
import com.journeyOS.liteweather.view.AqiView;
import com.journeyOS.liteweather.view.AstroView;
import com.journeyOS.liteweather.view.DailyForecastView;
import com.journeyOS.liteweather.view.FirstMatchInScrollViewLinearLayout;
import com.journeyOS.liteweather.view.HourlyForecastView;
import com.journeyOS.liteweather.view.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnLongClick;

import static com.journeyOS.base.Constant.ENABLE_LONGE_CLICK;
import static com.journeyOS.core.api.weatherprovider.WeatherData.AqiEntity;
import static com.journeyOS.core.api.weatherprovider.WeatherData.LifeIndexEntity;


public class WeatherFragment extends BaseFragment implements RouterListener {
    private static final String TAG = WeatherFragment.class.getSimpleName();

    private Context mContext;

    @BindView(R.id.weatherLinearLayout)
    FirstMatchInScrollViewLinearLayout mWeatherLinearLayout;

    @BindView(R.id.dailyForecastView)
    DailyForecastView mDailyForecastView;
    @BindView(R.id.pullRefreshLayout)
    PullRefreshLayout pullRefreshLayout;
    @BindView(R.id.hourlyForecastView)
    HourlyForecastView mHourlyForecastView;
    @BindView(R.id.aqi_view)
    AqiView mAqiView;
    @BindView(R.id.astroView)
    AstroView mAstroView;
    @BindView(R.id.weatherScrollView)
    ScrollView mScrollView;

    @BindView(R.id.todaydetail_bottomline)
    TextView mTodayTailBottomline;
    @BindView(R.id.todaydetail_temp)
    TextView mTodaydeDailTemp;
    @BindView(R.id.now_fl)
    TextView mNowFl;
    @BindView(R.id.now_hum)
    TextView mNowHum;
    @BindView(R.id.now_vis)
    TextView mNowVis;
    @BindView(R.id.now_pcpn)
    TextView mNowPcpn;
    @BindView(R.id.aqi_detail_text)
    TextView mAqiDetailText;
    @BindView(R.id.aqi_pm25)
    TextView mAqiPm25;
    @BindView(R.id.aqi_pm10)
    TextView mAqiPm10;
    @BindView(R.id.aqi_so2)
    TextView mAqiSo2;
    @BindView(R.id.aqi_no2)
    TextView mAqiNo2;

    @BindView(R.id.suggestion_comf)
    TextView mSuggestionComf;
    @BindView(R.id.suggestion_cw)
    TextView mSuggestionCw;
    @BindView(R.id.suggestion_drsg)
    TextView mSuggestionDrsg;
    @BindView(R.id.suggestion_flu)
    TextView mSuggestionFlu;
    @BindView(R.id.suggestion_sport)
    TextView mSuggestionSport;
    @BindView(R.id.suggestion_tarv)
    TextView mSuggestionTarv;
    @BindView(R.id.suggestion_uv)
    TextView mSuggestionUv;

    @BindView(R.id.suggestion_comf_brf)
    TextView mSuggestionComfBrf;
    @BindView(R.id.suggestion_cw_brf)
    TextView mSuggestionCwBrf;
    @BindView(R.id.suggestion_drsg_brf)
    TextView mSuggestionDrsgBrf;
    @BindView(R.id.suggestion_flu_brf)
    TextView mSuggestionFluBrf;
    @BindView(R.id.suggestion_sport_brf)
    TextView mSuggestionSportBrf;
    @BindView(R.id.suggestion_tarv_brf)
    TextView mSuggestionTarvBrf;
    @BindView(R.id.suggestion_uv_brf)
    TextView mSuggestionUvBrf;


    private static final int MIN_REFRESH_MILLS = 2000;
    private long mStartRefresh;

    private WeatherModel mWeatherModel;
    private WeatherData mWeatherData;
    private String mCityId;

    private SkyType mDrawerType = SkyType.UNKNOWN_D;

    private static final String BUNDLE_EXTRA_CITY_ID = "BUNDLE_EXTRA_CITY_ID";

    public static WeatherFragment makeInstance(String cityId) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_EXTRA_CITY_ID, cityId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initBeforeView() {
        mWeatherModel = ModelProvider.getModel(getActivity(), WeatherModel.class);
    }

    @Override
    public int getContentViewId() {
        return R.layout.fragment_weather;
    }

    @Override
    public void initViews() {
        mContext = CoreManager.getContext();
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchWeather(true);
            }
        });
    }

    @Override
    protected void initDataObserver() {
        mWeatherModel.getGetWeatherStatus().observe(this, new Observer<StatusDataResource.Status>() {
            @Override
            public void onChanged(@Nullable StatusDataResource.Status status) {
                if (StatusDataResource.Status.LOADING.equals(status)) {
                    startRefresh();
                } else {
                    if (SystemClock.currentThreadTimeMillis() - mStartRefresh > MIN_REFRESH_MILLS) {
                        WeatherData weatherData = mWeatherModel.getWeatherData();
                        if (isCurrentCity(weatherData)) {
                            mWeatherData = weatherData;
                            updateWeatherUI(weatherData);
                        }
                    } else {
                        TaskScheduler.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                WeatherData weatherData = mWeatherModel.getWeatherData();
                                if (isCurrentCity(weatherData)) {
                                    mWeatherData = weatherData;
                                    updateWeatherUI(weatherData);
                                }
                            }
                        }, MIN_REFRESH_MILLS + SystemClock.currentThreadTimeMillis() - mStartRefresh);
                    }
                }

            }
        });
    }

    @Override
    public Object getObject() {
        return getDrawerType(mWeatherData);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchArguments();
        LogUtils.d(TAG, "weather fragment resume, current city = [" + (BaseUtils.isNull(mWeatherData) ? null : mWeatherData.basic.city + "]" + ", current city id = " + mCityId));
    }

    private void startRefresh() {
        mStartRefresh = SystemClock.currentThreadTimeMillis();
    }

    private void fetchArguments() {
        onAnimation(true);
        if (BaseUtils.isNull(this.mCityId)) {
            try {
                this.mCityId = (String) getArguments().getSerializable(BUNDLE_EXTRA_CITY_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (BaseUtils.isNull(this.mWeatherData)) {
            fetchWeather(false);
        }
    }

    private void updateWeatherUI(WeatherData weatherData) {
        if (BaseUtils.isNull(weatherData)) {
            LogUtils.w(TAG, "can be update ui while weather data was NULL");
            return;
        }
        onAnimation(false);
        pullRefreshLayout.setRefreshing(false);
        updateCurrentDrawerType(weatherData);

        mDailyForecastView.setDailyData(mWeatherModel.parseDailyData(weatherData));
        mHourlyForecastView.setHourlyData(mWeatherModel.parseHourlyData(weatherData));
        mAqiView.setAqiData(mWeatherModel.parseAqiData(weatherData));
        mAstroView.setAstroData(mWeatherModel.parseAstroData(weatherData));

        WeatherData.NowEntity now = weatherData.now;
        if (!BaseUtils.isNull(now)) {
            setTextViewString(mTodayTailBottomline, weatherData.basic.city + " " + now.weather);
            setTextViewString(mTodaydeDailTemp, now.temperature + mContext.getString(R.string.unit_t));
            setTextViewString(mNowFl, now.bodyT + mContext.getString(R.string.unit_t));
            setTextViewString(mNowHum, now.humidity + mContext.getString(R.string.unit_percent));
            setTextViewString(mNowVis, now.visibility + mContext.getString(R.string.unit_km));
            setTextViewString(mNowPcpn, now.precipitation + mContext.getString(R.string.unit_mm));
        }

        AqiEntity aqiEntity = weatherData.aqi;
        if (!BaseUtils.isNull(aqiEntity)) {
            setTextViewString(mAqiDetailText, aqiEntity.quality);
            setTextViewString(mAqiPm25, aqiEntity.pm25 + mContext.getString(R.string.unit_air));
            setTextViewString(mAqiPm10, aqiEntity.pm10 + mContext.getString(R.string.unit_air));
            setTextViewString(mAqiSo2, aqiEntity.so2 + mContext.getString(R.string.unit_air));
            setTextViewString(mAqiNo2, aqiEntity.no2 + mContext.getString(R.string.unit_air));
        }

        List<LifeIndexEntity> lifeIndexEntities = weatherData.life;
        if (!BaseUtils.isNull(lifeIndexEntities)) {
            for (LifeIndexEntity lifeIndexEntity : lifeIndexEntities) {
                String name = lifeIndexEntity.name;
                String level = lifeIndexEntity.level;
                String content = lifeIndexEntity.content;
                if (name.equals("comf")) {
                    setTextViewString(mSuggestionComf, content);
                    setTextViewString(mSuggestionComfBrf, level);
                } else if (name.equals("drsg")) {
                    setTextViewString(mSuggestionDrsg, content);
                    setTextViewString(mSuggestionDrsgBrf, level);
                } else if (name.equals("flu")) {
                    setTextViewString(mSuggestionFlu, content);
                    setTextViewString(mSuggestionFluBrf, level);
                } else if (name.equals("sport")) {
                    setTextViewString(mSuggestionSport, content);
                    setTextViewString(mSuggestionSportBrf, level);
                } else if (name.equals("trav")) {
                    setTextViewString(mSuggestionTarv, content);
                    setTextViewString(mSuggestionTarvBrf, level);
                } else if (name.equals("uv")) {
                    setTextViewString(mSuggestionUv, content);
                    setTextViewString(mSuggestionUvBrf, level);
                } else if (name.equals("cw")) {
                    setTextViewString(mSuggestionCw, content);
                    setTextViewString(mSuggestionCwBrf, level);
                }
            }
        }

    }

    private void setTextViewString(TextView tv, String str) {
        if (tv != null) {
            tv.setText(str);
        } else {
        }
    }

    private void updateCurrentDrawerType(WeatherData weatherData) {
        if (BaseUtils.isNull(weatherData)) {
            return;
        }
        final SkyType curType = WeatherUitls.convertWeatherType(weatherData);
        this.mDrawerType = curType;
        updateDrawerType(curType);
    }

    private SkyType getDrawerType(WeatherData weatherData) {
        if (BaseUtils.isNull(weatherData)) {
            return SkyType.DEFAULT;
        }
        return WeatherUitls.convertWeatherType(weatherData);
    }

    @OnLongClick({R.id.weatherLinearLayout})
    public boolean showDebug() {
        boolean enableLongClick = SpUtils.getInstant().getBoolean(ENABLE_LONGE_CLICK, false);
        if (!enableLongClick) {
            LogUtils.d(TAG, "you has been " + enableLongClick + " long click!");
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ArrayList<String> strs = new ArrayList<String>();
        String[] types = mContext.getResources().getStringArray(R.array.weather_drawer_type);
        for (String type : types) {
            strs.add(type);
        }
        int index = 0;
        for (int i = 0; i < SkyType.values().length; i++) {
            if (SkyType.values()[i] == mDrawerType) {
                index = i;
                break;
            }
        }
        builder.setSingleChoiceItems(strs.toArray(new String[]{}), index,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDrawerType = SkyType.values()[which];
                        updateDrawerType(mDrawerType);
                        dialog.dismiss();
                    }
                });
//        builder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog alertDialog = builder.create();
//        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
        alertDialog.show();
        return true;
    }

    private void postRefresh() {
        if (pullRefreshLayout != null) {
            Activity activity = getActivity();
            if (activity != null) {
                if (NetWork.isAvailable(CoreManager.getContext()))
                    pullRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullRefreshLayout.setRefreshing(true, true);
                        }
                    }, 100);
            }

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            fetchWeather(false);
        }
    }

    private void fetchWeather(boolean forceRefresh) {
        LogUtils.d(TAG, "fetch weather by city id = " + this.mCityId + " current city = [" + (BaseUtils.isNull(mWeatherData) ? null : mWeatherData.basic.city) + "]");
        if (!BaseUtils.isNull(mCityId)) {

            if (BaseUtils.isNull(mWeatherData)) {
                WeatherData weatherData = CoreManager.getImpl(IWeatherProvider.class).fetchWeather(mCityId);
                if (weatherData != null) {
                    if (isCurrentCity(weatherData)) {
                        mWeatherData = weatherData;
                        updateWeatherUI(mWeatherData);
                    } else {
                        mWeatherModel.updateWeather(mCityId);
                        mWeatherData = mWeatherModel.getWeatherData();
                    }
                } else {
                    mWeatherModel.updateWeather(mCityId);
                    mWeatherData = mWeatherModel.getWeatherData();
                }
            } else {
                if (isCurrentCity(mWeatherData)) {
                    if (forceRefresh) {
                        updateWeatherUI(mWeatherData);
                    }
                } else {
                    mWeatherModel.updateWeather(mCityId);
                    mWeatherData = mWeatherModel.getWeatherData();
                }
            }
        }
    }

    private boolean isCurrentCity(WeatherData weatherData) {
        if (BaseUtils.isNull(weatherData)) {
            return false;
        }
        boolean isNeedUpdate = (!BaseUtils.isNull(mCityId) && mCityId.equals(weatherData.cityId));
        return isNeedUpdate;
    }

    private void updateDrawerType(SkyType type) {
        Messages msg = new Messages();
        msg.what = Messages.MSG_DRAWER_TYPE_UPDATE;
        msg.arg2 = mCityId;
        msg.obj = type;
        Router.getDefault().post(msg);
    }


    private void onAnimation(boolean isStart) {
        mWeatherLinearLayout.setVisibility(isStart ? View.GONE : View.VISIBLE);
        Messages msg = new Messages();
        msg.what = Messages.MSG_ANIMATION;
        msg.arg1 = isStart ? 1 : 0;
        Router.getDefault().post(msg);
    }

    @Override
    public void onShowMessage(Message message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
        }
    }
}