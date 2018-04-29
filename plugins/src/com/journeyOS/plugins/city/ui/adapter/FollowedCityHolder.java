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

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.adapter.BaseViewHolder;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.city.ui.CityModel;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.core.api.weatherprovider.IWeatherProvider;
import com.journeyOS.core.base.ResourceProvider;
import com.journeyOS.core.location.ILocationApi;
import com.journeyOS.core.viewmodel.ModelProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class FollowedCityHolder extends BaseViewHolder<FollowedCityData> {
    public static final int[] BLUR_IMAGE = {
            R.mipmap.city_blur0,
            R.mipmap.city_blur1,
            R.mipmap.city_blur2,
            R.mipmap.city_blur3,
            R.mipmap.city_blur4,
            R.mipmap.city_blur5};

    @BindView(R2.id.image)
    ImageView mImage;
    @BindView(R2.id.city_temp)
    TextView mCityTemp;
    @BindView(R2.id.city_name)
    TextView mCityName;
    @BindView(R2.id.city_status)
    TextView mCityStatus;
    @BindView(R2.id.content)
    RelativeLayout mContent;
    @BindView(R2.id.checked)
    ImageView mChecked;
    @BindView(R2.id.delete)
    ImageView mDelete;
    @BindView(R2.id.hover)
    View mHover;

    private CityWeatherAdapter mCityWeatherAdapter;
    private FollowedCityData mFollowedCityData;
    private Drawable mDrawableLocation;
    private CityModel mCityModel;


    public FollowedCityHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
        mCityWeatherAdapter = (CityWeatherAdapter) mBaseAdapter;
        mCityModel = ModelProvider.getModel(getContext(),CityModel.class);
        initViews();

    }


    private void initViews() {
        mDrawableLocation = UIUtils.getDrawable(getContext(), R.drawable.svg_location);
        mDrawableLocation.setBounds(0, 0, UIUtils.dipToPx(getContext(), R.dimen.common_location_size), UIUtils.dipToPx(getContext(), R.dimen.common_location_size));
    }

    @Override
    public void updateItem(FollowedCityData data, int position) {
        if (data == null) {
            return;
        }

        mFollowedCityData = data;
        mImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mImage.setImageResource(data.backgroundId);
        mCityTemp.setText(data.temp);
        mCityName.setText(data.cityName);
        mCityStatus.setText(data.weatherStatus);

        Drawable drawableLeft = UIUtils.getDrawable(getContext(), ResourceProvider.getIconId(data.weatherStatus));
        drawableLeft.setBounds(0, 0, UIUtils.dipToPx(getContext(), R.dimen.common_icon_size_small), UIUtils.dipToPx(getContext(), R.dimen.common_icon_size_small));
        mCityStatus.setCompoundDrawables(drawableLeft, null, null, null);

        mDelete.setVisibility(mCityWeatherAdapter.mIsDeleting ? View.VISIBLE : View.GONE);
        mHover.setVisibility(mCityWeatherAdapter.mIsDeleting ? View.VISIBLE : View.GONE);


        if (data.cityId.equals(CoreManager.getImpl(ILocationApi.class).getLocatedCityId())) {
            mDelete.setVisibility(View.GONE);
            mHover.setVisibility(View.GONE);

            mCityName.setCompoundDrawables(mDrawableLocation, null, null, null);
        } else {
            mCityName.setCompoundDrawables(null, null, null, null);
        }

        boolean isDefault = data.cityId.equals(CoreManager.getImpl(ICityProvider.class).getCurrentCityId());
        mChecked.setVisibility(isDefault ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getContentViewId() {
        return R.layout.city_item_followed_city;
    }

    @OnLongClick(R2.id.content)
    boolean showDelete() {
        updateAdapter(true);
        return true;
    }

    private void updateAdapter(boolean deleting) {
        mCityWeatherAdapter.setDeleteAction(deleting);
        mCityWeatherAdapter.notifyDataSetChanged();
    }

    @OnClick({R2.id.content, R2.id.delete})
    void onClick(View view) {
        int i = view.getId();
        if (i == R.id.content) {
            if (!mCityWeatherAdapter.mIsDeleting) {
                //CoreManager.getImpl(IWeatherProvider.class).updateWeather(mFollowedCityData.cityId);
                String cityId = mFollowedCityData.cityId;
                CoreManager.getImpl(ICityProvider.class).saveCurrentCityId(cityId);
            }
            updateAdapter(false);

        } else if (i == R.id.delete) {
            mCityModel.deleteFollowedWeather(mFollowedCityData.cityId);
            updateAdapter(true);
        }
    }

}
