package com.journeyOS.liteweather.ui.weather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.journeyOS.liteframework.base.BaseFragment;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.liteweather.BR;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.app.AppViewModelFactory;
import com.journeyOS.liteweather.databinding.FragmentWeatherBinding;
import com.journeyOS.widget.sky.SkyType;

public class WeatherFragment extends BaseFragment<FragmentWeatherBinding, WeatherViewModel> {
    private static final String TAG = WeatherFragment.class.getSimpleName();
    private SkyType mSkyType;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_weather;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public WeatherViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用NetWorkViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this, factory).get(WeatherViewModel.class);
    }

    @Override
    public void initData() {
        //请求数据
        viewModel.requestData();
    }

    @Override
    public void initViewObservable() {
        //注册文件下载的监听
        viewModel.skyTypeEvent.observe(this, new Observer<SkyType>() {
            @Override
            public void onChanged(@Nullable SkyType skyType) {
                KLog.d(TAG, "on sky type changed = [" + skyType + "]");
                mSkyType = skyType;
                RxBus.getDefault().post(mSkyType);
            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        KLog.d(TAG, "on hidden changed,  = [" + hidden + "], sky type = [" + mSkyType + "]");
        if (!hidden) {
            RxBus.getDefault().post(mSkyType);
        }
    }
}
