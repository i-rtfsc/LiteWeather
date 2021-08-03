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

package com.journeyOS.core.location;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.SmartLog;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.cityprovider.City;
import com.journeyOS.core.api.cityprovider.ICityProvider;
import com.journeyOS.literouter.Router;
import com.journeyOS.literouter.annotation.ARouterInject;


@ARouterInject(api = ILocationApi.class)
public class LocationImpl implements ILocationApi {
    private static final String TAG = LocationImpl.class.getSimpleName();
    private AMapLocationClient mLocationClient;
    private City mLocatedCity;

    @Override
    public void onCreate() {
        getCurrentCityId();
    }

    private void getCurrentCityId() {
        String currentCityId = CoreManager.getImpl(ICityProvider.class).getCurrentCityId();
        if (BaseUtils.isNull(currentCityId)) {
            initLocation();
        } else {
            Messages msg = new Messages();
            msg.what = Messages.MSG_LOCATION;
            msg.arg1 = 1;
            msg.obj = currentCityId;
            Router.getDefault().post(msg);
        }

    }

    private void initLocation() {
        mLocationClient = new AMapLocationClient(CoreManager.getContext());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocationLatest(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(final AMapLocation aMapLocation) {
                SmartLog.i(TAG, "onLocationChanged = " + aMapLocation);
                if (aMapLocation != null) {

                    CoreManager.getImpl(ICityProvider.class).getCityWorkHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            boolean locationSucceed = false;
                            try {
                                String city = aMapLocation.getCity().substring(0, 2);
                                String district = aMapLocation.getDistrict().substring(0, 2);

                                mLocatedCity = CoreManager.getImpl(ICityProvider.class).searchCity(city, district);

                                //城市库全名不匹配
                                if (mLocatedCity == null) {
                                    city = city.substring(0, 2);
                                    district = district.substring(0, 2);
                                    mLocatedCity = CoreManager.getImpl(ICityProvider.class).searchCity(city, district);

                                }

                                locationSucceed = mLocatedCity != null;

                                if (locationSucceed) {
                                    mLocatedCity.latitude = String.valueOf(aMapLocation.getLatitude());
                                    mLocatedCity.longitude = String.valueOf(aMapLocation.getLongitude());
                                }
                            } catch (Exception e) {
                                SmartLog.e(TAG, "location error = " + e);
                            }

                            Messages msg = new Messages();
                            msg.what = Messages.MSG_LOCATION;
                            msg.arg1 = locationSucceed ? 1 : 0;
                            msg.obj = locationSucceed ? mLocatedCity.cityId : null;
                            Router.getDefault().post(msg);
                            SmartLog.i(TAG, "located city = " + mLocatedCity);
                        }
                    });

                }
            }
        });
    }

    @Override
    public void startLocation() {
        if (!BaseUtils.isNull(mLocationClient)) mLocationClient.startLocation();
    }

    @Override
    public String getLocatedCityId() {
        return mLocatedCity != null ? mLocatedCity.cityId : "$";
    }

    @Override
    public City getLocatedCity() {
        return mLocatedCity;
    }

}
