package com.journeyOS.main.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.launcher.ARouter;
import com.journeyOS.base.router.RouterPath;
import com.journeyOS.liteframework.base.BaseActivity;
import com.journeyOS.liteframework.base.BaseViewModel;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.bus.RxSubscriptions;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.main.BR;
import com.journeyOS.main.R;
import com.journeyOS.main.databinding.ActivityMainBinding;
import com.journeyOS.widget.sky.SkyType;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends BaseActivity<ActivityMainBinding, BaseViewModel> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Fragment> mFragments = new ArrayList<>();
    private Disposable mSubscription;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
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
        initSky(SkyType.SNOW_D);
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
        //这里需要通过ARouter获取，不能直接new,因为在组件独立运行时，mian是没有依赖其他组件，所以new不到其他组件的Fragment
        Fragment weatherFragment = (Fragment) ARouter.getInstance().build(RouterPath.Fragment.Weather.PAGER_WEATHER).navigation();
        Fragment settingFragment = (Fragment) ARouter.getInstance().build(RouterPath.Fragment.Setting.PAGER_SETTING).navigation();
        KLog.d(TAG, "weatherFragment = [" + weatherFragment + "], settingFragment = [" + settingFragment + "]");
        //有的手机用ARouter获取到的是空
        if (weatherFragment == null) {
            weatherFragment = reflection("com.journeyOS.weather.ui.fragment.WeatherFragment");
        }
        if (settingFragment == null) {
            settingFragment = reflection("com.journeyOS.setting.ui.fragment.SettingsFragment");
        }
        mFragments.add(weatherFragment);
        mFragments.add(settingFragment);
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
                KLog.d(TAG, "index = [" + index + "], old = [" + old + "]");
                commitAllowingStateLoss(index);
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

    private Fragment reflection(String className) {
        try {
            Class<?> cls = Class.forName(className);
            return (Fragment) cls.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }
}