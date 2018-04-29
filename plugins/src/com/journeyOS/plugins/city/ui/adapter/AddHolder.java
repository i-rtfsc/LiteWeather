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


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.adapter.BaseViewHolder;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.city.ui.search.SearchActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class AddHolder extends BaseViewHolder<AddData> {

    @BindView(R2.id.image)
    ImageView mImage;


    public AddHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(AddData data, int position) {

    }

    @Override
    public int getContentViewId() {
        return R.layout.city_item_add_city;
    }

    @OnClick(R2.id.image)
    public void onClick() {
        CoreManager.getImpl(ICityProvider.class).navigationSearchActivity(getContext());
    }
}
