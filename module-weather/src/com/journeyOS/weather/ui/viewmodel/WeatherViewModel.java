package com.journeyOS.weather.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.core.data.DataRepository;
import com.journeyOS.core.data.source.local.base.DBConfigs;
import com.journeyOS.core.data.source.local.city.City;
import com.journeyOS.core.data.source.local.weather.Weather;
import com.journeyOS.core.entity.AirNow;
import com.journeyOS.core.entity.Indices;
import com.journeyOS.core.entity.NowBase;
import com.journeyOS.core.entity.Sun;
import com.journeyOS.core.entity.WeatherDaily;
import com.journeyOS.core.entity.WeatherHourly;
import com.journeyOS.liteframework.base.BaseViewModel;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.liteframework.binding.command.BindingAction;
import com.journeyOS.liteframework.binding.command.BindingCommand;
import com.journeyOS.liteframework.bus.event.SingleLiveEvent;
import com.journeyOS.liteframework.rxhttp.HttpObserver;
import com.journeyOS.liteframework.rxhttp.HttpResponse;
import com.journeyOS.liteframework.utils.JsonUtils;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.liteframework.utils.StringUtils;
import com.journeyOS.weather.BR;
import com.journeyOS.weather.R;
import com.journeyOS.widget.sky.SkyType;
import com.journeyOS.widget.sky.WeatherUitls;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class WeatherViewModel extends BaseViewModel<DataRepository> {
    private static final String TAG = WeatherViewModel.class.getSimpleName();

    private static final int NOW = 0;
    private static final int DAILY = 1;
    private static final int HOURLY = 2;
    private static final int AIR = 3;
    private static final int SUN = 4;
    private static final int INDICES = 5;
    private static final int WEBSITE = 6;

    private static final String SUCCESS = "200";

    //查询天气的key和location信息
    private String mKey = null;
    private String locationId = DBConfigs.Settings.LOCATION_ID_DEFAULT;
    public City mCity = DBConfigs.City.getDefaultCity();

    //封装一个界面发生改变的观察者
    public UIChangeObservable uiChange = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //天空
        public SingleLiveEvent<SkyType> skyTypeEvent = new SingleLiveEvent<>();
    }

    //查询到的天气结果存下来，传给sun
    private NowBase mWeatherNow = null;
    private WeatherDaily mWeatherDaily = null;

    public WeatherViewModel(@NonNull Application application, DataRepository repository) {
        super(application, repository);
        mKey = model.getString(DBConfigs.Settings.WEATHER_KEY, DBConfigs.Settings.WEATHER_KEY_DEFAULT);
        ensureMultiItemViewPos();
    }

    //给RecyclerView添加ObservableList
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();

    //RecyclerView多布局添加ItemBinding
    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局
            int itemType = (int) item.getItemType();
            switch (itemType) {
                case NOW:
                    itemBinding.set(BR.viewModel, R.layout.weather_now);
                    break;
                case DAILY:
                    itemBinding.set(BR.viewModel, R.layout.weather_daily);
                    break;
                case HOURLY:
                    itemBinding.set(BR.viewModel, R.layout.weather_hourly);
                    break;
                case AIR:
                    itemBinding.set(BR.viewModel, R.layout.weather_air);
                    break;
                case SUN:
                    itemBinding.set(BR.viewModel, R.layout.weather_sun);
                    break;
                case INDICES:
                    itemBinding.set(BR.viewModel, R.layout.weather_indices);
                    break;
                case WEBSITE:
                    itemBinding.set(BR.viewModel, R.layout.weather_website);
                    break;
            }
        }
    });

    //下拉刷新
    public BindingCommand onRefreshCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            requestNetWork(mKey, locationId);
        }
    });

    private void ensureMultiItemViewPos() {
        WeatherNowViewModel wNVM = new WeatherNowViewModel(this, null, null);
        wNVM.multiItemType(NOW);
        observableList.add(NOW, wNVM);

        WeatherDailyViewModel wDVM = new WeatherDailyViewModel(this, null);
        wDVM.multiItemType(DAILY);
        observableList.add(DAILY, wDVM);

        WeatherHourlyViewModel wHVM = new WeatherHourlyViewModel(this, null);
        wHVM.multiItemType(HOURLY);
        observableList.add(HOURLY, wHVM);

        WeatherAirViewModel wAVM = new WeatherAirViewModel(this, null);
        wAVM.multiItemType(AIR);
        observableList.add(AIR, wAVM);

        WeatherSunViewModel wSVM = new WeatherSunViewModel(this, null, null, null);
        wSVM.multiItemType(SUN);
        observableList.add(SUN, wSVM);

        WeatherIndicesViewModel wIVM = new WeatherIndicesViewModel(this, null);
        wIVM.multiItemType(INDICES);
        observableList.add(INDICES, wIVM);

        WeatherWebsiteViewModel wWVM = new WeatherWebsiteViewModel(this);
        wWVM.multiItemType(WEBSITE);
        observableList.add(WEBSITE, wWVM);

        KLog.d(TAG, "after add size = [" + observableList.size() + "]");
//        observableList.clear();
//        KLog.d(TAG, "after clear size = [" + observableList.size() + "]");
    }

    /***************************************************queryWeather***************************************************/
    public synchronized void requestData(boolean force) {
        final boolean[] needed = {false};
        String locationJson = model.getString(DBConfigs.Settings.LOCATION_ID, DBConfigs.Settings.LOCATION_ID_DEFAULT);
        try {
            City city = JsonUtils.fromJson(locationJson, City.class);
            if (city != null && city.locationId != null) {
                mCity = city;
            }
            if (!locationId.equals(city.locationId)) {
                needed[0] = true;
            }
        } catch (Exception exception) {
            try {
                Observable<City> observable = Observable.just(model.searchCity(DBConfigs.Settings.LOCATION_ID_DEFAULT));
                observable.subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(new Observer<City>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull City city) {
                                if (city != null && city.locationId != null) {
                                    mCity = city;
                                    model.put(DBConfigs.Settings.LOCATION_ID, JsonUtils.toJson(city));
                                    if (!locationId.equals(city.locationId)) {
                                        needed[0] = true;
                                    }
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.e(TAG, "get city error = " + e);
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "get city complete");
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mCity != null && mCity.locationId != null
                && !StringUtils.isSpace(mCity.locationId)) {
            locationId = mCity.locationId;
        }

        if (!needed[0] && !force) {
            return;
        }

        //get data from local
        Observable.just(getWeather(locationId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Weather weather) {
                        parseLocalWeather(weather, locationId);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        requestNetWork(mKey, locationId);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "get weather complete");
                    }
                });
    }

    private void parseLocalWeather(Weather weather, String locationId) {
        if (weather == null) {
            KLog.d(TAG, "local weather was null");
            requestNetWork(mKey, locationId);
        } else {
            String startTime = weather.time;
            String endTime = Long.toString(System.currentTimeMillis());
            long diffHours = TimeUtils.getDiffHours(startTime, endTime);
            KLog.d(TAG, "startTime = [" + startTime + "], endTime = [" + endTime + "], diffHours = [" + diffHours + "]");
            if (diffHours < 4) {
                KLog.d(TAG, "weather now exists = [" + (weather.now != null) + "]");
                if (StringUtils.isSpace(weather.now)) {
                    requestWeatherNow(mKey, locationId);
                } else {
                    try {
                        NowBase nowBase = JsonUtils.fromJson(weather.now, NowBase.class);
                        initWeatherNowVm(nowBase);
                        initSunVm(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestWeatherNow(mKey, locationId);
                    }
                }

                KLog.d(TAG, "weather daily exists = [" + (weather.daily != null) + "]");
                if (StringUtils.isSpace(weather.daily)) {
                    requestWeatherDaily(mKey, locationId);
                } else {
                    try {
                        WeatherDaily weatherDaily = JsonUtils.fromJson(weather.daily, WeatherDaily.class);
                        initWeatherDailyVm(weatherDaily);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestWeatherDaily(mKey, locationId);
                    }
                }

                KLog.d(TAG, "weather hourly exists = [" + (weather.hourly != null) + "]");
                if (StringUtils.isSpace(weather.hourly)) {
                    requestWeatherHourly(mKey, locationId);
                } else {
                    try {
                        WeatherHourly weatherHourly = JsonUtils.fromJson(weather.hourly, WeatherHourly.class);
                        initWeatherHourlyVm(weatherHourly);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestWeatherHourly(mKey, locationId);
                    }
                }

                KLog.d(TAG, "weather air exists = [" + (weather.air != null) + "]");
                if (StringUtils.isSpace(weather.air)) {
                    requestAirNow(mKey, locationId);
                } else {
                    try {
                        AirNow airNow = JsonUtils.fromJson(weather.air, AirNow.class);
                        initAirNowVm(airNow);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestAirNow(mKey, locationId);
                    }
                }

                KLog.d(TAG, "weather sun exists = [" + StringUtils.isSpace(weather.sun) + "]");
                KLog.d(TAG, "weather sun json = [" + weather.sun + "]");
                if (StringUtils.isSpace(weather.sun)) {
                    requestSun(mKey, locationId);
                } else {
                    try {
                        Sun sun = JsonUtils.fromJson(weather.sun, Sun.class);
                        initSunVm(sun);
                    } catch (Exception e) {
                        e.printStackTrace();
                        initSunVm(null);
                    }
                }

                KLog.d(TAG, "weather indices exists = [" + (weather.indices != null) + "]");
                if (StringUtils.isSpace(weather.indices)) {
                    requestIndices(mKey, locationId);
                } else {
                    try {
                        Indices indices = JsonUtils.fromJson(weather.indices, Indices.class);
                        initIndicesVm(indices);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestIndices(mKey, locationId);
                    }
                }
//                initWebsiteVm();
            } else {
                requestNetWork(mKey, locationId);
            }
        }
    }

    public void requestNetWork(String key, String locationId) {
        KLog.d(TAG, "locationId = [" + locationId + "]");
        requestWeatherNow(key, locationId);
        requestWeatherDaily(key, locationId);
        requestWeatherHourly(key, locationId);
        requestAirNow(key, locationId);
        requestSun(key, locationId);
        requestIndices(key, locationId);
        initWebsiteVm();
    }

    /***************************************************WeatherNow***************************************************/
    private void requestWeatherNow(String key, String location) {
        KLog.d(TAG, "locationId = [" + locationId + "]");
        HttpObserver<NowBase> httpObserver = new HttpObserver<NowBase>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<NowBase> response) {
                NowBase nowBase = response.body();
                if (nowBase != null && nowBase.now != null && SUCCESS.equals(nowBase.code)) {
                    initWeatherNowVm(nowBase);
                    initSunVm(null);
                    //save db
                    Weather weather = getWeatherWithInitNowTime(locationId);
                    weather.now = JsonUtils.toJson(nowBase);
                    model.saveWeather(weather);
                }
                //请求刷新完成收回
                uiChange.finishRefreshing.call();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                //请求刷新完成
                uiChange.finishRefreshing.call();
            }
        };

        model.getWeatherNow(httpObserver, key, location);
    }

    private void initWeatherNowVm(NowBase nowBase) {
        if (nowBase == null) {
            return;
        }
        if (nowBase.now == null) {
            return;
        }

        try {
            observableList.remove(NOW);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mWeatherNow = nowBase;
        WeatherNowViewModel vm = new WeatherNowViewModel(this, nowBase, mCity);
        vm.multiItemType(NOW);
        observableList.add(NOW, vm);

        //init fake sun time
        String sunSet = "19:00";
        String sunRise = "06:00";
        updateSky(nowBase.now.icon, sunSet, sunRise);
    }

    /***************************************************WeatherDaily***************************************************/
    private void requestWeatherDaily(String key, String location) {
        KLog.d(TAG, "locationId = [" + locationId + "]");
        HttpObserver<WeatherDaily> httpObserver = new HttpObserver<WeatherDaily>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<WeatherDaily> response) {
                WeatherDaily weatherDaily = response.body();
                if (weatherDaily != null && weatherDaily.daily != null && SUCCESS.equals(weatherDaily.code)) {
                    initWeatherDailyVm(weatherDaily);
                    //save db
                    Weather weather = getWeatherWithInitNowTime(locationId);
                    weather.daily = JsonUtils.toJson(weatherDaily);
                    model.saveWeather(weather);
                }
            }

            @Override
            public void onError(@NonNull Throwable error) {
            }
        };

        model.getWeather7D(httpObserver, key, location);
    }

    private void initWeatherDailyVm(WeatherDaily weatherDaily) {
        try {
            observableList.remove(DAILY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWeatherDaily = weatherDaily;
        WeatherDailyViewModel vm = new WeatherDailyViewModel(this, weatherDaily);
        vm.multiItemType(DAILY);
        observableList.add(DAILY, vm);
    }

    /***************************************************WeatherHourly***************************************************/
    private void requestWeatherHourly(String key, String location) {
        KLog.d(TAG, "locationId = [" + locationId + "]");
        HttpObserver<WeatherHourly> httpObserver = new HttpObserver<WeatherHourly>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<WeatherHourly> response) {
                WeatherHourly weatherHourly = response.body();
                if (weatherHourly != null && weatherHourly.hourly != null && SUCCESS.equals(weatherHourly.code)) {
                    initWeatherHourlyVm(weatherHourly);
                    //save db
                    Weather weather = getWeatherWithInitNowTime(locationId);
                    weather.hourly = JsonUtils.toJson(weatherHourly);
                    model.saveWeather(weather);
                }
            }

            @Override
            public void onError(@NonNull Throwable error) {
            }
        };

        model.getWeather24Hourly(httpObserver, key, location);
    }

    private void initWeatherHourlyVm(WeatherHourly weatherHourly) {
        try {
            observableList.remove(HOURLY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WeatherHourlyViewModel vm = new WeatherHourlyViewModel(this, weatherHourly);
        vm.multiItemType(HOURLY);
        observableList.add(HOURLY, vm);
    }

    /***************************************************WeatherHourly***************************************************/
    private void requestAirNow(String key, String location) {
        KLog.d(TAG, "locationId = [" + locationId + "]");
        HttpObserver<AirNow> httpObserver = new HttpObserver<AirNow>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<AirNow> response) {
                AirNow airNow = response.body();
                if (airNow != null && airNow.now != null && SUCCESS.equals(airNow.code)) {
                    initAirNowVm(airNow);
                    //save db
                    Weather weather = getWeatherWithInitNowTime(locationId);
                    weather.air = JsonUtils.toJson(airNow);
                    model.saveWeather(weather);
                }
            }

            @Override
            public void onError(@NonNull Throwable error) {
            }
        };

        model.getAirNow(httpObserver, key, location);
    }

    private void initAirNowVm(AirNow airNow) {
        try {
            observableList.remove(AIR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WeatherAirViewModel vm = new WeatherAirViewModel(this, airNow);
        vm.multiItemType(AIR);
        observableList.add(AIR, vm);
    }

    /***************************************************WeatherHourly***************************************************/
    private void requestSun(String key, String location) {
        KLog.d(TAG, "locationId = [" + locationId + "]");
        HttpObserver<Sun> httpObserver = new HttpObserver<Sun>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<Sun> response) {
                Sun sun = response.body();
                if (sun != null && SUCCESS.equals(sun.code)) {
                    initSunVm(sun);
                    //save db
                    Weather weather = getWeatherWithInitNowTime(locationId);
                    weather.sun = JsonUtils.toJson(sun);
                    model.saveWeather(weather);
                } else {
                    initSunVm(null);
                }
            }

            @Override
            public void onError(@NonNull Throwable error) {
                initSunVm(null);
            }
        };

        model.getSun(httpObserver, key, location, TimeUtils.getMonthDay());
    }

    private void initSunVm(Sun sun) {
        try {
            observableList.remove(SUN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WeatherSunViewModel vm = new WeatherSunViewModel(this, mWeatherNow, mWeatherDaily, sun);
        vm.multiItemType(SUN);
        observableList.add(SUN, vm);
    }


    /***************************************************Indices***************************************************/
    private void requestIndices(String key, String location) {
        KLog.d(TAG, "locationId = [" + locationId + "]");
        HttpObserver<Indices> httpObserver = new HttpObserver<Indices>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<Indices> response) {
                Indices indices = response.body();
                if (indices != null && indices.daily != null && SUCCESS.equals(indices.code)) {
                    initIndicesVm(indices);
                    //save db
                    Weather weather = getWeatherWithInitNowTime(locationId);
                    weather.indices = JsonUtils.toJson(indices);
                    model.saveWeather(weather);
                }
            }

            @Override
            public void onError(@NonNull Throwable error) {

            }
        };

        model.get1DIndices(httpObserver, key, location);
    }

    private void initIndicesVm(Indices indices) {
        try {
            observableList.remove(INDICES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        KLog.d(TAG, "WeatherIndicesViewModel indices = " + indices);
        WeatherIndicesViewModel vm = new WeatherIndicesViewModel(this, indices);
        vm.multiItemType(INDICES);
        observableList.add(INDICES, vm);
        initWebsiteVm();
    }

    /***************************************************Website***************************************************/
    private void initWebsiteVm() {
        try {
            observableList.remove(WEBSITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            WeatherWebsiteViewModel vm = new WeatherWebsiteViewModel(WeatherViewModel.this);
            vm.multiItemType(WEBSITE);
            observableList.add(WEBSITE, vm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSky(String weatherCode, String sunset, String sunrise) {
        SkyType skyType = SkyType.CLEAR_D;
        boolean nightSky = model.getBoolean(DBConfigs.Settings.NIGHT_SKY, DBConfigs.Settings.NIGHT_SKY_DEFAULT);
        if (nightSky) {
            skyType = WeatherUitls.convertWeatherType(weatherCode, sunset, sunrise);
        } else {
            skyType = WeatherUitls.convertWeatherType(weatherCode);
        }
        uiChange.skyTypeEvent.setValue(skyType);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /************************************************************************************************/
    /**
     * 这个方法给当前时间为为了保存数据
     */
    private Weather getWeatherWithInitNowTime(String locationId) {
        Weather weather = getWeather(locationId);
        weather.time = Long.toString(System.currentTimeMillis());
        return weather;
    }

    private Weather getWeather(String locationId) {
        Weather weather = model.getWeather(locationId);
        if (weather == null) {
            weather = new Weather();
            weather.locationId = locationId;
        }
        if (mCity != null && mCity.locationId != null
                && !StringUtils.isSpace(mCity.locationId)) {
            weather.adm1 = mCity.adm1;
            weather.cityName = mCity.cityName;
        }

        return weather;
    }

}
