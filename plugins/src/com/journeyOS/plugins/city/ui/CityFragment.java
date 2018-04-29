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

package com.journeyOS.plugins.city.ui;


import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.city.ui.adapter.AddData;
import com.journeyOS.plugins.city.ui.adapter.AddHolder;
import com.journeyOS.plugins.city.ui.adapter.CityWeatherAdapter;
import com.journeyOS.plugins.city.ui.adapter.FollowedCityData;
import com.journeyOS.plugins.city.ui.adapter.FollowedCityHolder;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.viewmodel.ModelProvider;

import java.util.List;

import butterknife.BindView;

public class CityFragment extends BaseFragment {

    @BindView(R2.id.city_follow_list)
    RecyclerView mRecyclerView;

    private CityWeatherAdapter mSubscribeCityAdapter;
    private AddData mAddData = new AddData();
    private CityModel mCityModel;
    private boolean mIsVisibleToUser;

    public static BaseFragment newInstance() {
        CityFragment cityFragment;
        cityFragment = new CityFragment();
        return cityFragment;
    }

    @Override
    public void initBeforeView() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.city_fragment_followed;
    }

    @Override
    public void initViews() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setBackgroundResource(R.color.transparent);

        mSubscribeCityAdapter = new CityWeatherAdapter(getContext());
        mSubscribeCityAdapter.registerHolder(FollowedCityHolder.class, R.layout.city_item_followed_city);
        mSubscribeCityAdapter.registerHolder(AddHolder.class, R.layout.city_item_add_city);
        mRecyclerView.setAdapter(mSubscribeCityAdapter);
    }

    @Override
    protected void initDataObserver() {
        mCityModel = ModelProvider.getModel(getActivity(),CityModel.class);
        mCityModel.getFollowedWeather().observe(this, new Observer<List<FollowedCityData>>() {
            @Override
            public void onChanged(@Nullable List<FollowedCityData> followedCityData) {
                onAllFollowedCities(followedCityData);
            }
        });
    }

    @Override
    public Object getObject() {
        return null;
    }


    public void onAllFollowedCities(List<FollowedCityData> followedCityDatas) {
        mSubscribeCityAdapter.clear();
        mSubscribeCityAdapter.setData(followedCityDatas);
        mSubscribeCityAdapter.addData(mAddData);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
    }
}
