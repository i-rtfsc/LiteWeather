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

import androidx.core.util.Pair;

import com.journeyOS.base.adapter.BaseAdapterData;
import com.journeyOS.plugins.R;

import java.util.ArrayList;
import java.util.List;


public class HeaderData implements BaseAdapterData {


    private List<Pair<String, String>> mHotCities;

    public HeaderData() {
        mHotCities = new ArrayList<>();
        mHotCities.add(new Pair<>("北京", "CN101010100"));
        mHotCities.add(new Pair<>("上海", "CN101020100"));
        mHotCities.add(new Pair<>("广州", "CN101280101"));
        mHotCities.add(new Pair<>("深圳", "CN101280601"));
        mHotCities.add(new Pair<>("杭州", "CN101210101"));
        mHotCities.add(new Pair<>("南京", "CN101190101"));
        mHotCities.add(new Pair<>("武汉", "CN101200101"));
        mHotCities.add(new Pair<>("重庆", "CN101040100"));

    }

    List<Pair<String, String>> getCities() {
        return mHotCities;
    }

    @Override
    public int getContentViewId() {
        return R.layout.city_item_city_select_header;
    }
}
