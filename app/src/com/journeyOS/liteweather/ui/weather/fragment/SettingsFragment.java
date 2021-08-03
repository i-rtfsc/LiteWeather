package com.journeyOS.liteweather.ui.weather.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.journeyOS.liteframework.base.BaseFragment;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteweather.BR;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.app.AppViewModelFactory;
import com.journeyOS.liteweather.databinding.FragmentSettingsBinding;
import com.journeyOS.widget.sky.SkyType;

public class SettingsFragment extends BaseFragment<FragmentSettingsBinding, SettingsViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_settings;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public SettingsViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this, factory).get(SettingsViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("solo-debug", "onResume(WeatherFragment) called");
        RxBus.getDefault().post(SkyType.DEFAULT);
    }
}
