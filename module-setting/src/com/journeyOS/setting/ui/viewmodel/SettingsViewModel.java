package com.journeyOS.setting.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.journeyOS.data.DataRepository;
import com.journeyOS.data.source.local.base.DBConfigs;
import com.journeyOS.data.source.local.city.City;
import com.journeyOS.liteframework.base.BaseViewModel;
import com.journeyOS.liteframework.binding.command.BindingAction;
import com.journeyOS.liteframework.binding.command.BindingCommand;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.bus.RxSubscriptions;
import com.journeyOS.liteframework.bus.event.SingleLiveEvent;
import com.journeyOS.liteframework.utils.JsonUtils;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.setting.BR;
import com.journeyOS.setting.BuildConfig;
import com.journeyOS.setting.R;
import com.journeyOS.setting.utils.AppUtils;
import com.journeyOS.setting.utils.StringUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.tatarka.bindingcollectionadapter2.ItemBinding;


public class SettingsViewModel extends BaseViewModel<DataRepository> {
    private static final String TAG = SettingsViewModel.class.getSimpleName();
    private Disposable mSubscription;

    public ObservableField<String> version = new ObservableField<>();
    public ObservableField<String> adm1Event = new ObservableField<>();
    public ObservableField<String> cityNameEvent = new ObservableField<>();
    public ObservableField<String> weatherKey = new ObservableField<>();
    public ObservableField<String> weatherTime = new ObservableField<>();
    public ObservableField<Boolean> nightSky = new ObservableField<>(false);
    public ObservableField<String> weatherSky = new ObservableField<>();

    //封装一个界面发生改变的观察者
    public UIChangeObservable uiChange = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent keyClick = new SingleLiveEvent<>();
        public SingleLiveEvent<String> weatherKeyClick = new SingleLiveEvent<>();

        public SingleLiveEvent timeClick = new SingleLiveEvent<>();
        public SingleLiveEvent<String> weatherTimeClick = new SingleLiveEvent<>();

        public SingleLiveEvent weatherSkyClick = new SingleLiveEvent<>();
    }

    public SettingsViewModel(@NonNull Application application, DataRepository repository) {
        super(application, repository);
        version.set(BuildConfig.BUILD_VERSION_NAME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void initData() {
        weatherKey.set(StringUtils.hideId(model.getString(DBConfigs.Settings.WEATHER_KEY, DBConfigs.Settings.WEATHER_KEY_DEFAULT)));
        weatherTime.set(String.format(getApplication().getResources().getString(R.string.weather_time_diff),
                String.valueOf(model.getInt(DBConfigs.Settings.WEATHER_TIME, DBConfigs.Settings.WEATHER_TIME_DEFAULT))));
        nightSky.set(model.getBoolean(DBConfigs.Settings.NIGHT_SKY, DBConfigs.Settings.NIGHT_SKY_DEFAULT));
        updateCity();
        initWeatherKey();
        initWeatherTime();
    }

    private void updateCity() {
        String locationJson = null;
        try {
            locationJson = model.getString(DBConfigs.Settings.LOCATION_ID, DBConfigs.Settings.LOCATION_ID_DEFAULT);
            City localCity = JsonUtils.fromJson(locationJson, City.class);
            updateCity(localCity);
        } catch (Exception exception) {
            City localCity = model.searchCity(locationJson);
            updateCity(localCity);
        }
    }

    private void updateCity(City city) {
        if (city == null) {
            return;
        }

        adm1Event.set(city.adm1);
        cityNameEvent.set(city.cityName);
    }

    private void initWeatherKey() {
        try {
            //清除列表
            weatherKeyObservableList.clear();
        } catch (Exception e) {
            KLog.d(TAG, "clear observable list error = " + e);
        }

        for (String key : DBConfigs.Settings.getWeatherKeys()) {
            try {
                WeatherKeyViewModel vm = new WeatherKeyViewModel(this, key);
                weatherKeyObservableList.add(vm);
            } catch (Exception e) {
                KLog.d(TAG, "add observable list error = " + e);
            }
        }
    }

    public void saveWeatherKey(String key) {
        model.put(DBConfigs.Settings.WEATHER_KEY, key);
    }

    private void initWeatherTime() {
        try {
            //清除列表
            weatherTimeObservableList.clear();
        } catch (Exception e) {
            KLog.d(TAG, "clear observable list error = " + e);
        }

        for (int time : DBConfigs.Settings.getWeatherTimes()) {
            try {
                WeatherTimeViewModel vm = new WeatherTimeViewModel(this, time);
                weatherTimeObservableList.add(vm);
            } catch (Exception e) {
                KLog.d(TAG, "add observable list error = " + e);
            }
        }
    }

    public void saveWeatherTime(int time) {
        model.put(DBConfigs.Settings.WEATHER_TIME, time);
    }

    public void saveSettingSky(int sky) {
        model.put(DBConfigs.Settings.WEATHER_SKY, sky);
    }

    public int getSettingSky() {
        return model.getInt(DBConfigs.Settings.WEATHER_SKY, DBConfigs.Settings.WEATHER_SKY_DEFAULT);
    }

    public BindingCommand onVersionClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click version item");
            AppUtils.openInMarket(getApplication(), null, getApplication().getString(R.string.open_in_market));
        }
    });

    public BindingCommand onEmailClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click email item");
            AppUtils.openEmail(getApplication(), getApplication().getString(R.string.email_account), getApplication().getString(R.string.send_email));
        }
    });

    public BindingCommand onGithubClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click email item");
            AppUtils.openBrowser(getApplication(), getApplication().getString(R.string.github_account));
        }
    });

    //按钮点击事件
    public BindingCommand onCityClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click city item");
            try {
                Class<?> cityFragment = Class.forName("com.journeyOS.city.ui.fragment.CityFragment");
                startContainerActivity(cityFragment.getCanonicalName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            //监听事件
            subscribeSky();

        }
    });

    public BindingCommand onKeyClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click key item");
            uiChange.keyClick.call();
        }
    });


    public BindingCommand onTimeClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click time item");
            uiChange.timeClick.call();
        }
    });

    public BindingCommand onNightSkyCheckedChangeCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            boolean isChecked = !model.getBoolean(DBConfigs.Settings.NIGHT_SKY, DBConfigs.Settings.NIGHT_SKY_DEFAULT);
            KLog.d(TAG, "click night sky item = " + isChecked);
            nightSky.set(isChecked);
            model.put(DBConfigs.Settings.NIGHT_SKY, isChecked);
        }
    });

    public BindingCommand onWeatherSkyClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click time item");
            uiChange.weatherSkyClick.call();
        }
    });

    //给RecyclerView添加ObservableList
    public ObservableList<WeatherKeyViewModel> weatherKeyObservableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<WeatherKeyViewModel> weatherKeyItemBinding = ItemBinding.of(BR.viewModel, R.layout.item_weather_key);

    //给RecyclerView添加ObservableList
    public ObservableList<WeatherTimeViewModel> weatherTimeObservableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<WeatherTimeViewModel> weatherTimeItemBinding = ItemBinding.of(BR.viewModel, R.layout.item_weather_time);

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
                        updateCity(city);
                        unsubscribe();
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
