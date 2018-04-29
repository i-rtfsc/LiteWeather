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
import android.view.View;
import android.widget.TextView;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.adapter.BaseViewHolder;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.weatherprovider.IWeatherProvider;

import butterknife.BindView;
import butterknife.OnClick;

public class CityHolder extends BaseViewHolder<CityInfoData> {

    @BindView(R2.id.tv_item_city_letter)
    TextView mTvItemCityLetter;
    @BindView(R2.id.tv_item_city_name)
    TextView mTvItemCityName;

    private String mCityId;

    public CityHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(CityInfoData data, int position) {
        mCityId = data.getCityId();

        mTvItemCityName.setText(data.getCityName());
        if (data.getInitial() != null) {
            mTvItemCityLetter.setVisibility(View.VISIBLE);
            mTvItemCityLetter.setText(data.getInitial());
        } else {
            mTvItemCityLetter.setVisibility(View.GONE);
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.city_item_city;
    }

    @OnClick(R2.id.tv_item_city_name)
    public void onClick() {
        CoreManager.getImpl(ICityProvider.class).saveCityId(mCityId);
        CoreManager.getImpl(IWeatherProvider.class).updateWeather(mCityId);
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }
}
