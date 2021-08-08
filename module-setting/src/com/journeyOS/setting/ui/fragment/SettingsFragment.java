package com.journeyOS.setting.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.journeyOS.base.router.RouterPath;
import com.journeyOS.core.app.AppViewModelFactory;
import com.journeyOS.liteframework.base.BaseFragment;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.setting.BR;
import com.journeyOS.setting.R;
import com.journeyOS.setting.databinding.FragmentSettingsBinding;
import com.journeyOS.setting.ui.viewmodel.SettingsViewModel;
import com.journeyOS.setting.utils.StringUtils;
import com.journeyOS.widget.sky.SkyType;

@Route(path = RouterPath.Fragment.Setting.PAGER_SETTING)
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding, SettingsViewModel> {
    private static final String TAG = SettingsFragment.class.getSimpleName();

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
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(this, factory).get(SettingsViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.initData();
        binding.expandableWeatherPort.collapse(false);
        RxBus.getDefault().post(SkyType.RAIN_SNOW_D);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        KLog.d(TAG, "on hidden changed,  = [" + hidden + "]");
        if (!hidden) {
            RxBus.getDefault().post(SkyType.RAIN_SNOW_D);
            viewModel.unsubscribe();
        }
    }

    @Override
    public void initViewObservable() {
        //监听账号点击事情
        viewModel.uiChange.keyClick.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                if (binding.expandableWeatherPort.isExpanded()) {
                    binding.expandableWeatherPort.collapse();
                } else {
                    binding.expandableWeatherPort.expand();
                }
            }
        });
        //监听选中的账号
        viewModel.uiChange.weatherKeyClick.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String key) {
                if (binding.expandableWeatherPort.isExpanded()) {
                    binding.expandableWeatherPort.collapse();
                } else {
                    binding.expandableWeatherPort.expand();
                }
                viewModel.weatherKey.set(StringUtils.hideId(key));
            }
        });
    }
}
