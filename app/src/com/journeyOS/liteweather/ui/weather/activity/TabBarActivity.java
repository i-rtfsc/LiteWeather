package com.journeyOS.liteweather.ui.weather.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.journeyOS.liteframework.base.BaseActivity;
import com.journeyOS.liteframework.base.BaseViewModel;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.bus.RxSubscriptions;
import com.journeyOS.liteweather.BR;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.databinding.ActivityTabBarBinding;
import com.journeyOS.liteweather.ui.weather.fragment.SettingsFragment;
import com.journeyOS.liteweather.ui.weather.fragment.WeatherFragment;
import com.journeyOS.widget.sky.SkyType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class TabBarActivity extends BaseActivity<ActivityTabBarBinding, BaseViewModel> {
    private static final String TAG = TabBarActivity.class.getSimpleName();
    private List<Fragment> mFragments;
    private Disposable mSubscription;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_tab_bar;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        //初始化Fragment
        initFragment();
        //初始化底部Button
        initBottomTab();
        //订阅天空变化
        subscribeSky();
        //设置默认的天空
        initSky(SkyType.DEFAULT);
    }


    @Override
    protected void onResume() {
        super.onResume();
        binding.sky.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.sky.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.sky.onDestroy();
        unsubscribe();
    }

    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new WeatherFragment());
        mFragments.add(new SettingsFragment());
        //默认选中第一个
        commitAllowingStateLoss(0);
    }

    public void initSky(SkyType skyType) {
        Log.d(TAG, "set sky drawer = [" + skyType + "]");
        binding.sky.setDrawerType(skyType);
    }

    private void initBottomTab() {
        NavigationController navigationController = binding.pagerBottomTab.material()
                .addItem(R.drawable.svg_weather, "天气", ContextCompat.getColor(this, R.color.main_background))
                .addItem(R.drawable.svg_settings, "设置", ContextCompat.getColor(this, R.color.main_background))
                .setDefaultColor(ContextCompat.getColor(this, R.color.text_primary_dark))
                .build();
        //底部按钮的点击事件监听
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                Fragment currentFragment = commitAllowingStateLoss(index);
                if (currentFragment instanceof SettingsFragment) {
                    initSky(SkyType.DEFAULT);
                }
            }

            @Override
            public void onRepeat(int index) {
            }
        });
    }

    private Fragment commitAllowingStateLoss(int position) {
        hideAllFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(position + "");
        if (currentFragment != null) {
            transaction.show(currentFragment);
        } else {
            currentFragment = mFragments.get(position);
            transaction.add(R.id.frameLayout, currentFragment, position + "");
        }
        transaction.commitAllowingStateLoss();
        return currentFragment;
    }

    //隐藏所有Fragment
    private void hideAllFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 订阅天空变化
     */
    public void subscribeSky() {
        mSubscription = RxBus.getDefault().toObservable(SkyType.class)
                .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
                .subscribe(new Consumer<SkyType>() {
                    @Override
                    public void accept(final SkyType skyType) throws Exception {
                        initSky(skyType);
                    }
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
    }

    /**
     * 取消订阅，防止内存泄漏
     */
    public void unsubscribe() {
        RxSubscriptions.remove(mSubscription);
    }
}