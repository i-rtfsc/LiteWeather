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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.SmartLog;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.cityprovider.City;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.permission.IPermissionApi;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.literouter.RouterMsssage;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.sky.SkyType;
import com.journeyOS.liteweather.sky.SkyView;
import com.journeyOS.liteweather.widget.effect.MxxFragmentPagerAdapter;
import com.journeyOS.liteweather.widget.effect.MxxViewPager;
import com.journeyOS.plugins.city.repository.ICityRepositoryApi;
import com.journeyOS.plugins.settings.ui.SettingFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

import static com.journeyOS.base.Constant.IS_FIRST_RUN;

public class WeatherActivity extends BaseActivity implements RouterListener {
    private static final String TAG = WeatherActivity.class.getSimpleName();
    private static Context mContext;
    @BindView(R.id.main_viewpager)
    MxxViewPager viewPager;

    @BindView(R.id.sky)
    SkyView mSkyView;

    @BindView(R.id.loading)
    RelativeLayout mLoading;

    AlphaAnimation alphaAnimation;

    public SimpleFragmentPagerAdapter mFragmentPagerAdapter;
    public static List<BaseFragment> mFragmentList;
    private static List<String> mCityId = new ArrayList<>();
    private static List<String> mCityName = new ArrayList<>();


    @Override
    public void initBeforeView() {
        super.initBeforeView();
        mContext = CoreManager.getContext();
        CoreManager.getImpl(IPermissionApi.class).initUrgentPermission(this);
        if (SpUtils.getInstant().getBoolean(IS_FIRST_RUN)) {
            CoreManager.getImpl(ICityProvider.class).saveCurrentCityId("CN101020100");
            CoreManager.getImpl(ICityProvider.class).saveCityId("CN101020100");
            SpUtils.getInstant().put(IS_FIRST_RUN, false);
        }
        fetchCity();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_weather;
    }

    @Override
    public void initViews() {
        if (Build.VERSION.SDK_INT >= 19) {
            viewPager.setPadding(0, UIUtils.getStatusBarHeight(), 0, 0);
        }
        alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(260);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.BLACK));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        viewPager.setAnimation(alphaAnimation);

        loadAreaToViewPager();
    }

    private void fetchCity() {
        String currentCityId = CoreManager.getImpl(ICityProvider.class).getCurrentCityId();
        final Set<String> cities = CoreManager.getImpl(ICityProvider.class).getCitiesId();
        SmartLog.d(TAG, "  fetchCity  city id list = " + cities);
        if (!BaseUtils.isNull(cities)) {
            mCityId.clear();
            mCityName.clear();
            mCityId.add(currentCityId);
            for (String cityId : cities) {
                if (!currentCityId.equals(cityId)) mCityId.add(cityId);
            }
            CoreManager.getImpl(ICityRepositoryApi.class).getCityWorkHandler().post(new Runnable() {
                @Override
                public void run() {
                    for (String cityId : mCityId) {
                        City city = CoreManager.getImpl(ICityProvider.class).searchCity(cityId);
                        mCityName.add(BaseUtils.isNull(city) ? mContext.getString(R.string.city_unknown) : city.cityName);
                    }
                }
            });
        }
    }

    public void loadAreaToViewPager() {
        fetchCity();
        final BaseFragment[] fragments = new BaseFragment[(BaseUtils.isNull(mCityId) ? 0 : mCityId.size()) + 1];
        viewPager.setOffscreenPageLimit(fragments.length);
        if (!BaseUtils.isNull(mCityId)) {
            int i = 0;
            for (String cityId : mCityId) {
                fragments[i] = WeatherFragment.makeInstance(cityId);
                i++;
            }
        }
        fragments[fragments.length - 1] = SettingFragment.newInstance();

        mFragmentList = Arrays.asList(fragments);
        mFragmentPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        viewPager.setAdapter(mFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new MxxViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                SkyType type = (SkyType) ((SimpleFragmentPagerAdapter) viewPager.getAdapter()).getItem(
                        position).getObject();
                updateDrawerType(type);
            }
        });
    }

    void updateDrawerType(SkyType type) {
        if (!BaseUtils.isNull(mSkyView)) {
            mSkyView.setDrawerType(BaseUtils.isNull(type) ? SkyType.DEFAULT : type);
        }
    }

    void showAnimation(boolean isStart) {
        if (isStart) {
            mLoading.setVisibility(View.VISIBLE);
            mSkyView.setVisibility(View.GONE);
        } else {
            if (mSkyView.getVisibility() != View.GONE) mLoading.setVisibility(View.GONE);
            if (mSkyView.getVisibility() != View.VISIBLE) mSkyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSkyView.onResume();
    }

    @Override
    protected void onPause() {
        mSkyView.onPause();
        viewPager.clearAnimation();
        alphaAnimation.cancel();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mSkyView.onDestroy();
        super.onDestroy();
    }


    @Override
    public void onShowMessage(RouterMsssage message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_DRAWER_TYPE_UPDATE:
                SkyType type = (SkyType) msg.obj;
                updateDrawerType(type);
                break;
            case Messages.MSG_CURRENT_CITY_CHANGED:
            case Messages.MSG_CITY_ADD:
            case Messages.MSG_CITY_DELETE:
                loadAreaToViewPager();
                break;
            case Messages.MSG_ANIMATION:
                boolean isStart = (msg.arg1 == 1);
                showAnimation(isStart);
                break;
        }
    }

    public static class SimpleFragmentPagerAdapter extends MxxFragmentPagerAdapter {

        private List<BaseFragment> fragments;

        public SimpleFragmentPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public BaseFragment getItem(int position) {
            BaseFragment fragment = fragments.get(position);
            fragment.setRetainInstance(true);
            return fragment;
        }

        public CharSequence getPageTitle(int position) {
            String title = null;
            if (!BaseUtils.isNull(mCityName) && position < mCityName.size()) {
                title = mCityName.get(position);
            } else {
                if (position == mFragmentList.size() - 1) {
                    title = mContext.getString(R.string.settings);
                }
            }

            return title;
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(((Fragment) object).getView());
            super.destroyItem(container, position, object);
        }

        public void addItem(BaseFragment baseFragment) {
            fragments.add(baseFragment);
        }
    }
}
