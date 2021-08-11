package com.journeyOS.city.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.journeyOS.city.R;
import com.journeyOS.core.data.DataRepository;
import com.journeyOS.core.data.source.local.base.DBConfigs;
import com.journeyOS.core.data.source.local.city.City;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.liteframework.binding.command.BindingAction;
import com.journeyOS.liteframework.binding.command.BindingCommand;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.utils.JsonUtils;
import com.journeyOS.liteframework.utils.KLog;

import java.util.List;

public class CityHeadViewModel extends MultiItemViewModel<CityViewModel> {
    private static final String TAG = CityHeadViewModel.class.getSimpleName();

    public ObservableField<String> cityInfo = new ObservableField<>();

    private Application mApplication = null;
    private DataRepository mRepository = null;
    private City mCity = null;
    public AMapLocationClient mLocationClient = null;

    //点击定位事件
    public BindingCommand onLocationClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click location icon item");
            startLocation();
        }
    });

    //点击城市事件
    public BindingCommand onCityClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click location city item");
            mRepository.put(DBConfigs.Settings.LOCATION_ID, JsonUtils.toJson(mCity));
            RxBus.getDefault().post(mCity);
            viewModel.finish();
        }
    });

    public CityHeadViewModel(@NonNull Application application, @NonNull CityViewModel viewModel, DataRepository repository) {
        super(viewModel);
        this.mApplication = application;
        this.mRepository = repository;
        startLocation();
    }


    private void startLocation() {
        if (mLocationClient == null) {
            initLocation();
        }
        KLog.d(TAG, "start location");
        cityInfo.set(mApplication.getString(R.string.city_locating));
        mLocationClient.startLocation();
    }

    private void stopLocation() {
        if (mLocationClient != null) {
            KLog.d(TAG, "stop location");
            mLocationClient.stopLocation();
        }
    }

    private void destroyLocation() {
        if (mLocationClient != null) {
            KLog.d(TAG, "destroy location");
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    private void initLocation() {
        mLocationClient = new AMapLocationClient(mApplication);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocationLatest(true);
        option.setInterval(2000);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (AMapLocation.LOCATION_SUCCESS == aMapLocation.getErrorCode()) {
                    String district = aMapLocation.getDistrict();
                    KLog.d(TAG, "aMapLocation = [" + aMapLocation.toString() + "]");
                    if (district == null) {
                        district = aMapLocation.getCity();
                    }
                    List<City> cities = mRepository.matchCity(district);
                    for (City matchCity : cities) {
                        KLog.d(TAG, "matchCity = [" + matchCity.toString() + "]");
                        mCity = matchCity;
                        cityInfo.set(matchCity.adm1 + matchCity.cityName);
                    }

                    stopLocation();
                    destroyLocation();
                } else {
                    KLog.d(TAG, "location error = [" + aMapLocation.getErrorInfo() + "]");
                }
            }
        });
    }

}
