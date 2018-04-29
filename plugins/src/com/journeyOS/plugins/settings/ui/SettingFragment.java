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

package com.journeyOS.plugins.settings.ui;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.Version;
import com.journeyOS.core.api.weatherprovider.IWeatherProvider;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.literouter.Message;
import com.journeyOS.literouter.Router;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.city.ui.CityModel;
import com.journeyOS.plugins.city.ui.adapter.AddData;
import com.journeyOS.plugins.city.ui.adapter.AddHolder;
import com.journeyOS.plugins.city.ui.adapter.CityWeatherAdapter;
import com.journeyOS.plugins.city.ui.adapter.FollowedCityData;
import com.journeyOS.plugins.city.ui.adapter.FollowedCityHolder;
import com.journeyOS.plugins.settings.Utils;
import com.suke.widget.SwitchButton;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.journeyOS.base.Constant.ENABLE_LONGE_CLICK;
import static com.journeyOS.base.Constant.ENABLE_NIGHT;
import static com.journeyOS.base.Constant.NOTIFICATION_ALLOW;
import static com.journeyOS.base.Constant.POLLING_TIME;
import static com.journeyOS.base.Constant.SHOW_LONG_CLICK_TOOGLE;
import static com.journeyOS.base.Constant.WEATHER_KEY;


public class SettingFragment extends BaseFragment {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private Context mContext;

    @BindView(R2.id.switch_notification)
    SwitchButton mSwitchNotification;
    @BindView(R2.id.switch_night)
    SwitchButton mSwitchNight;
    @BindView(R2.id.switch_long_click)
    SwitchButton mSwitchLongClick;
    @BindView(R2.id.update_schedule)
    TextView mUpdateSchedule;
    @BindView(R2.id.expandable_schedule)
    ExpandableLayout mExpandableSchedule;
    @BindView(R2.id.listView_schedule)
    ListView mListViewSchedule;
    @BindView(R2.id.weather_port)
    TextView mWeatherKey;
    @BindView(R2.id.expandable_weather_port)
    ExpandableLayout mExpandableWeatherPort;
    @BindView(R2.id.listView_weather_port)
    ListView mListViewWeatherKey;
    private String[] mWeatherPort;
    @BindView(R2.id.expandable_about)
    ExpandableLayout mExpandableAbout;
    @BindView(R2.id.city)
    TextView mCity;
    @BindView(R2.id.expandable_city)
    ExpandableLayout mExpandableCity;
    @BindView(R2.id.city_follow_list)
    RecyclerView mRecyclerView;
    @BindView(R2.id.layout_long_click)
    RelativeLayout mLongClickLayout;
    @BindView(R2.id.versoin_txt)
    TextView mVersion;
    @BindView(R2.id.expandable_open_library)
    ExpandableLayout mExpandableOpenLibrary;
    @BindView(R2.id.listView_open_library)
    ListView mListViewOpenLibrary;

    private CityWeatherAdapter mSubscribeCityAdapter;
    private AddData mAddData = new AddData();
    private CityModel mCityModel;
    private boolean mIsVisibleToUser;

    private String[] mScheduleKeys;

    private final static int COUNTS = 5;
    long[] mHints = new long[COUNTS];
    private final static long DURATION = 3 * 1000;

    public static BaseFragment newInstance() {
        SettingFragment settingFragment;
        settingFragment = new SettingFragment();
        return settingFragment;
    }

    @Override
    public int getContentViewId() {
        return R.layout.setting_fragment;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
        mContext = CoreManager.getContext();
        mScheduleKeys = mContext.getResources().getStringArray(R.array.setting_schedule);
        mWeatherPort = mContext.getResources().getStringArray(R.array.weather_key_port);
    }

    @Override
    public void initViews() {
        initNotifitionSwitch();
        initNightSwitch();
        initLongClickSwitch();
        initWeatherKey();
        initUpdateSchedule();
        initCity();
        initAbout();
        initOpenLibrary();
    }

    private void initNotifitionSwitch() {
        mSwitchNotification.setChecked(SpUtils.getInstant().getBoolean(NOTIFICATION_ALLOW, true));
        mSwitchNotification.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SpUtils.getInstant().put(NOTIFICATION_ALLOW, isChecked);
                Messages msg = new Messages();
                msg.what = Messages.MSG_ALLOW_NOTIFICATION;
                msg.arg1 = isChecked ? 1 : 0;
                Router.getDefault().post(msg);
            }
        });
    }

    private void initNightSwitch() {
        boolean isShow = SpUtils.getInstant().getBoolean(SHOW_LONG_CLICK_TOOGLE, false);
        mLongClickLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);

        mSwitchNight.setChecked(SpUtils.getInstant().getBoolean(ENABLE_NIGHT, false));
        mSwitchNight.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SpUtils.getInstant().put(ENABLE_NIGHT, isChecked);
            }
        });
    }

    private void initLongClickSwitch() {
        mSwitchLongClick.setChecked(SpUtils.getInstant().getBoolean(ENABLE_LONGE_CLICK, false));
        mSwitchLongClick.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SpUtils.getInstant().put(ENABLE_LONGE_CLICK, isChecked);
            }
        });
    }

    private void initUpdateSchedule() {
        int scheduleWhich = SpUtils.getInstant().getInt(POLLING_TIME, 3);
        mUpdateSchedule.setText(mScheduleKeys[scheduleWhich]);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.list_item_single_choice, mScheduleKeys);//android.R.layout.simple_list_item_single_choice
        mListViewSchedule.setAdapter(arrayAdapter);
        mListViewSchedule.setItemChecked(scheduleWhich, true);
        mListViewSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpUtils.getInstant().put(POLLING_TIME, i);
                mUpdateSchedule.setText(mScheduleKeys[i]);
                if (mExpandableSchedule.isExpanded()) {
                    mExpandableSchedule.collapse();
                }
            }
        });

        mListViewSchedule.setNestedScrollingEnabled(true);
        if (mExpandableSchedule.isExpanded()) {
            mExpandableSchedule.collapse();
        }

        if (isAdded()) {
            CoreManager.getImpl(IWeatherProvider.class).startService(getActivity(), scheduleWhich != mScheduleKeys.length - 1);
        }
    }

    private void initOpenLibrary() {
        String[] items = mContext.getResources().getStringArray(R.array.open_library);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.list_item_open_source, items);//android.R.layout.simple_list_item_single_choice
        mListViewOpenLibrary.setAdapter(arrayAdapter);
        mListViewOpenLibrary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        mListViewOpenLibrary.setNestedScrollingEnabled(true);
        if (mExpandableOpenLibrary.isExpanded()) {
            mExpandableOpenLibrary.collapse();
        }
    }

    private void initWeatherKey() {
        int scheduleWhich = SpUtils.getInstant().getInt(WEATHER_KEY, 0);
        mWeatherKey.setText(mWeatherPort[scheduleWhich]);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.list_item_single_choice, mWeatherPort);//android.R.layout.simple_list_item_single_choice
        mListViewWeatherKey.setAdapter(arrayAdapter);
        mListViewWeatherKey.setItemChecked(scheduleWhich, true);
        mListViewWeatherKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SpUtils.getInstant().put(WEATHER_KEY, i);
                mWeatherKey.setText(mWeatherPort[i]);
                if (mExpandableWeatherPort.isExpanded()) {
                    mExpandableWeatherPort.collapse();
                }
            }
        });

        mListViewWeatherKey.setNestedScrollingEnabled(true);
        if (mExpandableWeatherPort.isExpanded()) {
            mExpandableWeatherPort.collapse();
        }
    }

    private void initAbout() {
        mExpandableAbout.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
            }
        });
        if (mExpandableAbout.isExpanded()) {
            mExpandableAbout.collapse();
        }

        mVersion.setText(Version.getVersionName(mContext));
    }

    private void initCity() {
        mExpandableCity.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
            }
        });
        if (mExpandableCity.isExpanded()) {
            mExpandableCity.collapse();
        }

        //
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setBackgroundResource(R.color.transparent);

        mSubscribeCityAdapter = new CityWeatherAdapter(getContext());
        mSubscribeCityAdapter.registerHolder(FollowedCityHolder.class, R.layout.city_item_followed_city);
        mSubscribeCityAdapter.registerHolder(AddHolder.class, R.layout.city_item_add_city);
        mRecyclerView.setAdapter(mSubscribeCityAdapter);
        //
    }

    @Override
    protected void initDataObserver() {
        mCityModel = ModelProvider.getModel(getActivity(), CityModel.class);
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

    @OnClick({R2.id.update_allow,
            R2.id.weather_key,
            R2.id.about,
            R2.id.expandable_about,
            R2.id.city,
            R2.id.email,
            R2.id.github,
            R2.id.versoin,
            R2.id.developer,
            R2.id.open_library_txt})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.update_allow) {
            if (mExpandableSchedule.isExpanded()) {
                mExpandableSchedule.collapse();
            } else {
                mExpandableSchedule.expand();
            }
        } else if (i == R.id.weather_key) {
            if (mExpandableWeatherPort.isExpanded()) {
                mExpandableWeatherPort.collapse();
            } else {
                mExpandableWeatherPort.expand();
            }
        } else if (i == R.id.city) {
            if (mExpandableCity.isExpanded()) {
                mExpandableCity.collapse();
            } else {
                mExpandableCity.expand();
            }
        } else if (i == R.id.about) {
            expandOrcollapse();
        } else if (i == R.id.email) {
            Utils.startActivity(mContext, Utils.getEmailIntent());
            //expandOrcollapse();
        } else if (i == R.id.github) {
            Utils.startActivity(mContext, Utils.getGitHubIntent());
            //expandOrcollapse();
        } else if (i == R.id.versoin) {
            Utils.startActivity(mContext, Utils.getAppIntent());
            //expandOrcollapse();
        } else if (i == R.id.developer) {
            startDeveloper();
        } else if (i == R.id.open_library_txt) {
            if (mExpandableOpenLibrary.isExpanded()) {
                mExpandableOpenLibrary.collapse();
            } else {
                mExpandableOpenLibrary.expand();
            }
        }
    }

    private void expandOrcollapse() {
        if (mExpandableAbout.isExpanded()) {
            mExpandableAbout.collapse();
        } else {
            mExpandableAbout.expand();
        }
    }

    private void startDeveloper() {
        boolean isShow = SpUtils.getInstant().getBoolean(SHOW_LONG_CLICK_TOOGLE, false);
        if (isShow) {
            Toast.makeText(mContext, R.string.long_click_toogle_shown, Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (mHints.length == 5) {
                Toast.makeText(mContext, R.string.long_click_toogle_count, Toast.LENGTH_SHORT).show();
            }

        }
        System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
        mHints[mHints.length - 1] = SystemClock.uptimeMillis();
        if (mHints[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            SpUtils.getInstant().put(SHOW_LONG_CLICK_TOOGLE, true);
            mLongClickLayout.setVisibility(View.VISIBLE);
            expandOrcollapse();
        }
    }

}
