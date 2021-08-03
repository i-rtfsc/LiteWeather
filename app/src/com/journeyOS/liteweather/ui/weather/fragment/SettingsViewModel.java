package com.journeyOS.liteweather.ui.weather.fragment;

import android.app.Application;

import androidx.annotation.NonNull;

import com.journeyOS.liteframework.base.BaseViewModel;
import com.journeyOS.liteweather.data.DataRepository;


public class SettingsViewModel extends BaseViewModel<DataRepository> {

    public SettingsViewModel(@NonNull Application application, DataRepository repository) {
        super(application, repository);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
