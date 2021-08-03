package com.journeyOS.liteweather.ui.weather.fragment;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.liteframework.base.BaseViewModel;
import com.journeyOS.liteframework.bus.event.SingleLiveEvent;
import com.journeyOS.liteframework.rxhttp.HttpObserver;
import com.journeyOS.liteframework.rxhttp.HttpResponse;
import com.journeyOS.liteframework.utils.JsonUtils;
import com.journeyOS.liteframework.utils.StringUtils;
import com.journeyOS.liteweather.BR;
import com.journeyOS.liteweather.R;
import com.journeyOS.liteweather.data.DataRepository;
import com.journeyOS.liteweather.data.source.local.base.DBConfigs;
import com.journeyOS.liteweather.data.source.local.city.City;
import com.journeyOS.liteweather.data.source.local.weather.Weather;
import com.journeyOS.liteweather.entity.AirNow;
import com.journeyOS.liteweather.entity.Indices;
import com.journeyOS.liteweather.entity.NowBase;
import com.journeyOS.liteweather.entity.Sun;
import com.journeyOS.liteweather.entity.WeatherDaily;
import com.journeyOS.liteweather.entity.WeatherHourly;
import com.journeyOS.widget.weather.AqiView;
import com.journeyOS.widget.weather.AstroView;
import com.journeyOS.widget.weather.DailyForecastView;
import com.journeyOS.widget.weather.HourlyForecastView;
import com.journeyOS.widget.sky.SkyType;
import com.journeyOS.widget.sky.WeatherUitls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class WeatherViewModel extends BaseViewModel<DataRepository> {
    private static final String TAG = "solo-debug";
    private String mKey = "8aeec77017724b518a5f0ba5d1888820";

    //使用LiveData
    public SingleLiveEvent<SkyType> skyTypeEvent = new SingleLiveEvent<>();
//    //封装一个界面发生改变的观察者
//    public UIChangeObservable uiChange = new UIChangeObservable();
//    public class UIChangeObservable {
//        public SingleLiveEvent<SkyType> skyTypeEvent = new SingleLiveEvent<>();
//    }

    String locationId = DBConfigs.Settings.LOCATION_ID_DEFAULT;
    public SingleLiveEvent<City> cityEvent = new SingleLiveEvent<>();

    public ObservableField<NowBase.NowBean> nowEntity = new ObservableField<>();

    public ObservableField<List<DailyForecastView.DailyData>> dailyDataList = new ObservableField<>();

    public ObservableField<List<HourlyForecastView.HourlyData>> hourlyDataList = new ObservableField<>();

    public ObservableField<AqiView.AqiData> aqiEntity = new ObservableField<>();

    public ObservableField<AirNow.NowBean> airNowEntity = new ObservableField<>();

    private AstroView.AstroData astroData = new AstroView.AstroData();
    public ObservableField<AstroView.AstroData> astroEntity = new ObservableField<>();


    public WeatherViewModel(@NonNull Application application, DataRepository repository) {
        super(application, repository);
    }

    //给RecyclerView添加ObservableList
    public ObservableList<IndicesViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<IndicesViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_indices);

    public void test() {

//        Single<City> citySingle = RxManager.createAsyncSingle(new Callable<City>() {
//            @Override
//            public City call() throws Exception {
//                Log.d(TAG, "-------");
////                return model.searchCity(DBConfigs.Settings.LOCATION_ID_DEFAULT);
//                return model.searchCity("331");
//            }
//        });
//        citySingle.doAfterSuccess(new Consumer<City>() {
//            @Override
//            public void accept(City city) throws Exception {
//                Log.d(TAG, "test() called = " + city.toString());
//            }
//        });


    }


    /***************************************************queryWeather***************************************************/
    public void requestData() {
        String locationJson = model.getString(DBConfigs.Settings.LOCATION_ID, DBConfigs.Settings.LOCATION_ID_DEFAULT);
        try {
            City city = JsonUtils.fromJson(locationJson, City.class);
            cityEvent.setValue(city);
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
                                cityEvent.setValue(city);
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

        if (cityEvent != null && cityEvent.getValue() != null
                && cityEvent.getValue().locationId != null
                && !StringUtils.isSpace(cityEvent.getValue().locationId)) {
            locationId = cityEvent.getValue().locationId;
        }

        //get data from local
        Observable.just(getWeather(locationId))
                .subscribeOn(Schedulers.io())
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
                        requestNetWork(locationId);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "init city complete");
                    }
                });
    }

    private void parseLocalWeather(Weather weather, String locationId) {
        if (weather == null) {
            requestNetWork(locationId);
        } else {
            String startTime = weather.time;
            String endTime = Long.toString(System.currentTimeMillis());
            long diffHours = TimeUtils.getDiffHours(startTime, endTime);
            if (diffHours > 4) {
                if (StringUtils.isSpace(weather.now)) {
                    requestWeatherNow(mKey, locationId);
                } else {
                    try {
                        NowBase nowBase = JsonUtils.fromJson(weather.now, NowBase.class);
                        parseWeatherNow(nowBase);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestWeatherNow(mKey, locationId);
                    }
                }

                if (StringUtils.isSpace(weather.daily)) {
                    requestWeatherDaily(mKey, locationId);
                } else {
                    try {
                        WeatherDaily weatherDaily = JsonUtils.fromJson(weather.daily, WeatherDaily.class);
                        parseWeatherDaily(weatherDaily);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestWeatherDaily(mKey, locationId);
                    }
                }

                if (StringUtils.isSpace(weather.hourly)) {
                    requestWeatherHourly(mKey, locationId);
                } else {
                    try {
                        WeatherHourly weatherHourly = JsonUtils.fromJson(weather.hourly, WeatherHourly.class);
                        parseWeatherHourly(weatherHourly);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestWeatherHourly(mKey, locationId);
                    }
                }

                if (StringUtils.isSpace(weather.air)) {
                    requestAirNow(mKey, locationId);
                } else {
                    try {
                        AirNow airNow = JsonUtils.fromJson(weather.air, AirNow.class);
                        parseAirNow(airNow);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestAirNow(mKey, locationId);
                    }
                }

                if (StringUtils.isSpace(weather.sun)) {
                    requestSun(mKey, locationId);
                } else {
                    try {
                        Sun sun = JsonUtils.fromJson(weather.sun, Sun.class);
                        parseSun(sun);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestSun(mKey, locationId);
                    }
                }

                if (StringUtils.isSpace(weather.sun)) {
                    requestIndices(mKey, locationId);
                } else {
                    try {
                        Indices indices = JsonUtils.fromJson(weather.indices, Indices.class);
                        parseIndices(indices);
                    } catch (Exception e) {
                        e.printStackTrace();
                        requestIndices(mKey, locationId);
                    }
                }

            } else {
                requestNetWork(locationId);
            }
        }
    }

    public void requestNetWork(String locationId) {
        String key = "8aeec77017724b518a5f0ba5d1888820";
        String cityId = "CN101020100";
        requestWeatherNow(key, cityId);
        requestWeatherDaily(key, cityId);
        requestWeatherHourly(key, cityId);
        requestAirNow(key, cityId);
        requestSun(key, cityId);
        requestIndices(key, cityId);
    }

    /***************************************************WeatherNow***************************************************/
    private void requestWeatherNow(String key, String location) {
        HttpObserver<NowBase> httpObserver = new HttpObserver<NowBase>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<NowBase> response) {
                NowBase nowBase = response.body();
                if (nowBase != null && nowBase.now != null) {
                    parseWeatherNow(nowBase);
                    //save db
                    Weather weather = getWeather(locationId);
                    weather.now = JsonUtils.toJson(nowBase);
                    model.saveWeather(weather);
                }

            }

            @Override
            public void onError(@NonNull Throwable error) {
            }
        };

        model.getWeatherNow(httpObserver, key, location);
    }

    private void parseWeatherNow(NowBase nowBase) {
        if (nowBase == null) {
            return;
        }
        if (nowBase.now == null) {
            return;
        }

        nowEntity.set(nowBase.now);

        //init fake sun time
        astroData.sunSet = "19:00";
        astroData.sunRise = "06:00";

        astroData.pressure = nowBase.now.pressure;
        astroData.windSpeed = nowBase.now.windSpeed;
        astroData.windDirection = nowBase.now.windDir;

        SkyType skyType = WeatherUitls.convertWeatherType(nowBase.now.icon, astroData.sunSet, astroData.sunRise);
        skyTypeEvent.setValue(skyType);
        astroEntity.set(astroData);
    }


    /***************************************************WeatherDaily***************************************************/
    private void requestWeatherDaily(String key, String location) {
        HttpObserver<WeatherDaily> httpObserver = new HttpObserver<WeatherDaily>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<WeatherDaily> response) {
                WeatherDaily weatherDaily = response.body();
                if (weatherDaily != null && weatherDaily.daily != null) {
                    parseWeatherDaily(weatherDaily);
                    //save db
                    Weather weather = getWeather(locationId);
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

    private void parseWeatherDaily(WeatherDaily weatherDaily) {
        if (weatherDaily == null) {
            return;
        }
        if (weatherDaily.daily == null) {
            return;
        }

        List<WeatherDaily.DailyBean> daily = weatherDaily.daily;

        int all_max = Integer.MIN_VALUE;
        int all_min = Integer.MAX_VALUE;

        DailyForecastView.DailyData[] dailyDatas = new DailyForecastView.DailyData[daily.size()];

        for (int i = 0; i < daily.size(); i++) {
            DailyForecastView.DailyData data = new DailyForecastView.DailyData();
            WeatherDaily.DailyBean dailyEntity = daily.get(i);

            int max = Integer.parseInt(dailyEntity.tempMax);
            int min = Integer.parseInt(dailyEntity.tempMin);
            if (all_max < max) {
                all_max = max;
            }
            if (all_min > min) {
                all_min = min;
            }

            data.Tmax = max;
            data.Tmin = min;
            data.date = TimeUtils.prettyDate(dailyEntity.fxDate);
            data.pop = dailyEntity.precip;
            data.weather = dailyEntity.textDay;

            dailyDatas[i] = data;
        }

        float all_distance = Math.abs(all_max - all_min);
        float average_distance = (all_max + all_min) / 2f;
        for (DailyForecastView.DailyData d : dailyDatas) {
            d.maxOffsetPercent = (d.Tmax - average_distance) / all_distance;
            d.minOffsetPercent = (d.Tmin - average_distance) / all_distance;
        }

        dailyDataList.set(Arrays.asList(dailyDatas));
    }

    /***************************************************WeatherHourly***************************************************/
    private void requestWeatherHourly(String key, String location) {
        HttpObserver<WeatherHourly> httpObserver = new HttpObserver<WeatherHourly>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<WeatherHourly> response) {
                WeatherHourly weatherHourly = response.body();
                if (weatherHourly != null && weatherHourly.hourly != null) {
                    parseWeatherHourly(weatherHourly);
                    //save db
                    Weather weather = getWeather(locationId);
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

    private void parseWeatherHourly(WeatherHourly weatherHourly) {
        if (weatherHourly == null) {
            return;
        }
        if (weatherHourly.hourly == null) {
            return;
        }

        List<WeatherHourly.HourlyBean> hoursEntityList = weatherHourly.hourly;
        List<HourlyForecastView.HourlyData> hourlyDatas = new ArrayList<>();

        int all_max = Integer.MIN_VALUE;
        int all_min = Integer.MAX_VALUE;
        //只要间隔3个小时的数据
        for (int i = 0; i < hoursEntityList.size(); i = i + 3) {
            WeatherHourly.HourlyBean hoursEntity = hoursEntityList.get(i);
            HourlyForecastView.HourlyData hourlyData = new HourlyForecastView.HourlyData();
            int tmp = Integer.valueOf(hoursEntity.temp);
            if (all_max < tmp) {
                all_max = tmp;
            }
            if (all_min > tmp) {
                all_min = tmp;
            }
            hourlyData.temperature = tmp;
            hourlyData.date = TimeUtils.parseHour(hoursEntity.fxTime);
            hourlyData.windLevel = hoursEntity.windScale;
            hourlyData.humidity = hoursEntity.humidity;
            hourlyDatas.add(hourlyData);
        }
        float all_distance = Math.abs(all_max - all_min);
        float average_distance = (all_max + all_min) / 2f;

        for (HourlyForecastView.HourlyData d : hourlyDatas) {
            d.offsetPercent = (d.temperature - average_distance) / all_distance;
        }

        hourlyDataList.set(hourlyDatas);
    }


    /***************************************************WeatherHourly***************************************************/
    private void requestAirNow(String key, String location) {
        HttpObserver<AirNow> httpObserver = new HttpObserver<AirNow>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<AirNow> response) {
                AirNow airNow = response.body();
                if (airNow != null && airNow.now != null) {
                    parseAirNow(airNow);
                    //save db
                    Weather weather = getWeather(locationId);
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

    private void parseAirNow(AirNow airNow) {
        if (airNow == null) {
            return;
        }
        if (airNow.now == null) {
            return;
        }

        airNowEntity.set(airNow.now);

        AqiView.AqiData aqiData = new AqiView.AqiData();
        aqiData.aqi = airNow.now.aqi;
        aqiData.pm25 = airNow.now.pm2p5;
        aqiData.pm10 = airNow.now.pm10;
        aqiData.so2 = airNow.now.so2;
        aqiData.no2 = airNow.now.no2;
        aqiData.quality = airNow.now.category;

        aqiEntity.set(aqiData);
    }


    /***************************************************WeatherHourly***************************************************/
    private void requestSun(String key, String location) {
        HttpObserver<Sun> httpObserver = new HttpObserver<Sun>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<Sun> response) {
                Sun sun = response.body();
                if (sun != null) {
                    parseSun(sun);
                    //save db
                    Weather weather = getWeather(locationId);
                    weather.sun = JsonUtils.toJson(sun);
                    model.saveWeather(weather);
                }

            }

            @Override
            public void onError(@NonNull Throwable error) {
            }
        };

        model.getSun(httpObserver, key, location, "20210731");
    }

    private void parseSun(Sun sun) {
        if (sun != null) {
            astroData.sunSet = TimeUtils.parseHour(sun.sunset);
            astroData.sunRise = TimeUtils.parseHour(sun.sunrise);
        } else {
            astroData.sunSet = "19:00";
            astroData.sunRise = "06:00";
        }

        if (nowEntity.get() != null) {
            SkyType skyType = WeatherUitls.convertWeatherType(nowEntity.get().icon, astroData.sunSet, astroData.sunRise);
            skyTypeEvent.setValue(skyType);
        }

        astroEntity.set(astroData);
    }


    /***************************************************Indices***************************************************/
    private void requestIndices(String key, String location) {
        HttpObserver<Indices> httpObserver = new HttpObserver<Indices>() {
            @Override
            public void onSuccess(@NonNull HttpResponse<Indices> response) {
                Indices indices = response.body();
                if (indices != null && indices.daily != null) {
                    parseIndices(indices);
                    //save db
                    Weather weather = getWeather(locationId);
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

    private void parseIndices(Indices indices) {
        if (indices == null) {
            return;
        }
        if (indices.daily == null) {
            return;
        }

        Collections.sort(indices.daily, new NumberComparator());

        //清除列表
        observableList.clear();
        //请求成功
        for (Indices.DailyBean entity : indices.daily) {
            IndicesViewModel.Suggestions.Suggestion suggestion = new IndicesViewModel.Suggestions.Suggestion();

            //https://dev.qweather.com/docs/api/indices/
            switch (Integer.parseInt(entity.type)) {
                case 1:
                    suggestion.drawable = R.drawable.svg_suggestion_sport;
                    break;
                case 2:
                    suggestion.drawable = R.drawable.svg_suggestion_car;
                    break;
                case 3:
                    suggestion.drawable = R.drawable.svg_suggestion_clothes;
                    break;
                case 4:
                    suggestion.drawable = R.drawable.svg_suggestion_fishing;
                    break;
                case 5:
                    suggestion.drawable = R.drawable.svg_suggestion_uv;
                    break;
                case 6:
                    suggestion.drawable = R.drawable.svg_suggestion_trav;
                    break;
                case 7:
                    suggestion.drawable = R.drawable.svg_suggestion_allergic;
                    break;
                case 8:
                    suggestion.drawable = R.drawable.svg_suggestion_village;
                    break;
                case 9:
                    suggestion.drawable = R.drawable.svg_suggestion_influenza;
                    break;
                case 10:
                    suggestion.drawable = R.drawable.svg_suggestion_pollute;
                    break;
                case 11:
                    suggestion.drawable = R.drawable.svg_suggestion_air_conditioner;
                    break;
                case 12:
                    suggestion.drawable = R.drawable.svg_suggestion_sunglasses;
                    break;
                case 13:
                    suggestion.drawable = R.drawable.svg_suggestion_dressing;
                    break;
                case 14:
                    suggestion.drawable = R.drawable.svg_suggestion_hang;
                    break;
                case 15:
                    suggestion.drawable = R.drawable.svg_suggestion_traffic_light;
                    break;
                case 16:
                    suggestion.drawable = R.drawable.svg_suggestion_anti_sunburn;
                    break;
                default:
                    suggestion.drawable = R.drawable.svg_weather;
                    break;
            }

            String name = entity.name.split("指数")[0];
            if (name.length() > 4) {
                suggestion.name = name.substring(0, 4);
            } else {
                suggestion.name = name;
            }
            suggestion.category = entity.category;
            suggestion.text = entity.text;
            IndicesViewModel itemViewModel = new IndicesViewModel(WeatherViewModel.this, suggestion);
            //双向绑定动态添加Item
            observableList.add(itemViewModel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /************************************************************************************************/
    private Weather getWeather(String locationId) {
        Weather weather = model.getWeather(locationId);
        if (weather == null) {
            weather = new Weather();
            weather.locationId = locationId;
        }
        if (cityEvent != null && cityEvent.getValue() != null
                && cityEvent.getValue().locationId != null
                && !StringUtils.isSpace(cityEvent.getValue().locationId)) {
            weather.adm1 = cityEvent.getValue().adm1;
            weather.cityName = cityEvent.getValue().cityName;
        }

        weather.time = Long.toString(System.currentTimeMillis());
        return weather;
    }


    /**
     * 升序
     */
    private static class NumberComparator implements Comparator<Indices.DailyBean> {
        @Override
        public int compare(Indices.DailyBean left, Indices.DailyBean right) {
            int a = Integer.parseInt(left.type);
            int b = Integer.parseInt(right.type);
            return a - b;
        }
    }
}
