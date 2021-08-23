package com.journeyOS.setting.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.journeyOS.base.router.RouterPath;
import com.journeyOS.data.app.AppViewModelFactory;
import com.journeyOS.liteframework.base.BaseFragment;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.setting.BR;
import com.journeyOS.setting.R;
import com.journeyOS.setting.databinding.FragmentSettingsBinding;
import com.journeyOS.setting.ui.viewmodel.SettingsViewModel;
import com.journeyOS.setting.utils.StringUtils;
import com.journeyOS.widget.sky.SkyType;

import java.util.ArrayList;
import java.util.Collections;

@Route(path = RouterPath.Fragment.Setting.PAGER_SETTING)
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding, SettingsViewModel> {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private SkyType mSkyType = SkyType.RAIN_SNOW_D;
    //天空名称
    private ArrayList<String> mSkyTitle = new ArrayList<String>();

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
        //因为AppViewModelFactory所在的组件不依赖setting组件，所以只能反射构造SettingsViewModel
        //为了不用每一个XXXViewModel都有到AppViewModelFactory中写死其className
        //所以setMode的目的是为了得到className以方便反射
        //如果不调用setModel，则默认会调用SettingsViewModel(@NonNull Application application)构造方法
        factory.setModel(SettingsViewModel.class);
        return ViewModelProviders.of(this, factory).get(SettingsViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        String[] types = getActivity().getResources().getStringArray(R.array.weather_drawer_type);
        Collections.addAll(mSkyTitle, types);
        int sky = viewModel.getSettingSky();
        viewModel.weatherSky.set(mSkyTitle.get(sky));
        mSkyType = SkyType.values()[sky];
        RxBus.getDefault().post(mSkyType);

        viewModel.initData();
        binding.expandableWeatherPort.collapse(false);
        binding.expandableWeatherRefresh.collapse(false);
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
            RxBus.getDefault().post(mSkyType);
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

        //监听时间点击事情
        viewModel.uiChange.timeClick.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                if (binding.expandableWeatherRefresh.isExpanded()) {
                    binding.expandableWeatherRefresh.collapse();
                } else {
                    binding.expandableWeatherRefresh.expand();
                }
            }
        });
        //监听选中的时间
        viewModel.uiChange.weatherTimeClick.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String key) {
                if (binding.expandableWeatherRefresh.isExpanded()) {
                    binding.expandableWeatherRefresh.collapse();
                } else {
                    binding.expandableWeatherRefresh.expand();
                }
                viewModel.weatherTime.set(String.format(viewModel.getApplication().getResources().getString(R.string.weather_time_diff), key));
            }
        });

        //监听天空点击事情
        viewModel.uiChange.weatherSkyClick.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                showSkyDialog();
            }
        });
    }

    private void showSkyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), com.journeyOS.theme.R.style.theme_corners_dialog);
        int index = 0;
        for (int i = 0; i < SkyType.values().length; i++) {
            if (SkyType.values()[i] == mSkyType) {
                index = i;
                break;
            }
        }
        builder.setSingleChoiceItems(mSkyTitle.toArray(new String[]{}), index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSkyType = SkyType.values()[which];
                viewModel.weatherSky.set(mSkyTitle.get(which));
                viewModel.saveSettingSky(which);
                RxBus.getDefault().post(mSkyType);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
