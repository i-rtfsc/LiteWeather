package com.journeyOS.weather.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.journeyOS.base.router.RouterPath;
import com.journeyOS.core.app.AppViewModelFactory;
import com.journeyOS.core.data.source.local.city.City;
import com.journeyOS.liteframework.base.BaseFragment;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.bus.RxSubscriptions;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.weather.BR;
import com.journeyOS.weather.R;
import com.journeyOS.weather.databinding.FragmentWeatherBinding;
import com.journeyOS.weather.ui.viewmodel.WeatherViewModel;
import com.journeyOS.widget.sky.SkyType;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@Route(path = RouterPath.Fragment.Weather.PAGER_WEATHER)
public class WeatherFragment extends BaseFragment<FragmentWeatherBinding, WeatherViewModel> {
    private static final String TAG = WeatherFragment.class.getSimpleName();
    private Disposable mSubscription;

    private SkyType mSkyType = SkyType.DEFAULT;

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
        viewModel.requestData(true);
    }

    @Override
    public void initViewObservable() {
        //监听天空
        viewModel.uiChange.skyTypeEvent.observe(this, new Observer<SkyType>() {
            @Override
            public void onChanged(@Nullable SkyType skyType) {
                KLog.d(TAG, "on sky type changed = [" + skyType + "]");
                mSkyType = skyType;
                RxBus.getDefault().post(mSkyType);
            }
        });

        //监听下拉刷新完成
        viewModel.uiChange.finishRefreshing.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                //结束刷新
                binding.swipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        RxBus.getDefault().post(mSkyType);
//        长期订阅城市变化可能会有内存泄漏，改成在onHiddenChanged判断城市ID不一样则更新
//        subscribeSky();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        KLog.d(TAG, "on hidden changed, hidden = [" + hidden + "], sky type = [" + mSkyType + "]");
        if (!hidden) {
            viewModel.requestData(false);
            RxBus.getDefault().post(mSkyType);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        subscribeSky();
    }

    /**
     * 订阅城市变化
     */
    public void subscribeSky() {
        mSubscription = RxBus.getDefault().toObservable(City.class)
                .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
                .subscribe(new Consumer<City>() {
                    @Override
                    public void accept(final City city) throws Exception {
                        KLog.d(TAG, "city = [" + city + "]");
                        viewModel.requestData(false);
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
