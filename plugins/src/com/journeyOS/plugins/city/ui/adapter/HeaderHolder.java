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

package com.journeyOS.plugins.city.ui.adapter;

import android.app.Activity;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.journeyOS.base.adapter.BaseAdapterData;
import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.adapter.BaseViewHolder;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.literouter.RouterMsssage;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.cityprovider.City;
import com.journeyOS.core.api.weatherprovider.IWeatherProvider;
import com.journeyOS.core.location.ILocationApi;
import com.journeyOS.literouter.Router;
import com.journeyOS.literouter.RouterListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HeaderHolder extends BaseViewHolder<HeaderData> implements RouterListener {

    @BindView(R2.id.tv_located_city)
    TextView mTvLocatedCity;
    @BindView(R2.id.city_header_recyclerView)
    RecyclerView mRecyclerView;
    private boolean mLocationSucceeded;

    private BaseRecyclerAdapter mHotCityAdapter;


    public HeaderHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
        Router.getDefault().register(this);
        initViews();

    }

    @Override
    public void updateItem(HeaderData data, int position) {
        if (BaseUtils.isNull(data)) {
            return;
        }
        mHotCityAdapter.clear();
        List<HotCity> hotCities = new ArrayList<>();
        for (Pair<String, String> city : data.getCities()) {
            hotCities.add(new HotCity(city.first, city.second));
        }
        mHotCityAdapter.registerHolder(HotCityHolder.class, hotCities);
    }

    @Override
    public int getContentViewId() {
        return R.layout.city_item_city_select_header;
    }


    public void initViews() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mHotCityAdapter = new BaseRecyclerAdapter(getContext());
        mRecyclerView.setAdapter(mHotCityAdapter);

        City locatedCity = CoreManager.getImpl(ILocationApi.class).getLocatedCity();
        showLocation(locatedCity != null);

    }


    private void showLocation(boolean locationSuccess) {
        mLocationSucceeded = locationSuccess;
        if (locationSuccess) {
            mTvLocatedCity.setText(CoreManager.getImpl(ILocationApi.class).getLocatedCity().country);
        } else {
            mTvLocatedCity.setText(R.string.city_located_failed);
        }
    }

    @OnClick(R2.id.location_layout)
    void locate() {
        if (mLocationSucceeded) {
            String cityId = CoreManager.getImpl(ILocationApi.class).getLocatedCityId();
            CoreManager.getImpl(ICityProvider.class).saveCityId(cityId);
            CoreManager.getImpl(IWeatherProvider.class).updateWeather(cityId);
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
        } else {
            CoreManager.getImpl(ILocationApi.class).startLocation();
            mTvLocatedCity.setText(R.string.city_locating);
        }
    }

    @Override
    public void onShowMessage(RouterMsssage message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_LOCATION:
                boolean success = msg.arg1 == 1;
                showLocation(success);
                break;
        }
    }

    static final class HotCity implements BaseAdapterData {
        String mCityName;
        String mCityId;

        HotCity(String cityName, String cityId) {
            mCityName = cityName;
            mCityId = cityId;
        }

        @Override
        public int getContentViewId() {
            return R.layout.city_item_hot_city;
        }
    }

    public static final class HotCityHolder extends BaseViewHolder<HotCity> {

        @BindView(R2.id.tv_hot_city_name)
        TextView mTvHotCityName;
        HotCity mHotCity;

        public HotCityHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
            super(itemView, baseRecyclerAdapter);
        }

        @Override
        public void updateItem(HotCity data, int position) {
            mTvHotCityName.setText(data.mCityName);
            mHotCity = data;
        }

        @Override
        public int getContentViewId() {
            return R.layout.city_item_hot_city;
        }

        @OnClick(R2.id.tv_hot_city_name)
        void navigationWeather() {
            String cityId = mHotCity.mCityId;
            CoreManager.getImpl(ICityProvider.class).saveCityId(cityId);
            CoreManager.getImpl(IWeatherProvider.class).updateWeather(cityId);
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
        }
    }
}
