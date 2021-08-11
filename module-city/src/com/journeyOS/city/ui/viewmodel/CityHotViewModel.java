package com.journeyOS.city.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.data.DataRepository;
import com.journeyOS.data.source.local.base.DBConfigs;
import com.journeyOS.data.source.local.city.City;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.liteframework.binding.command.BindingAction;
import com.journeyOS.liteframework.binding.command.BindingCommand;
import com.journeyOS.liteframework.bus.RxBus;
import com.journeyOS.liteframework.utils.JsonUtils;
import com.journeyOS.liteframework.utils.KLog;

import java.util.ArrayList;
import java.util.List;

public class CityHotViewModel extends MultiItemViewModel<CityViewModel> {
    private static final String TAG = CityHotViewModel.class.getSimpleName();

    public ObservableField<Boolean> titleVisibility = new ObservableField<>(false);
    public ObservableField<City> city = new ObservableField<>();

    private DataRepository mRepository = null;

    public CityHotViewModel(@NonNull CityViewModel viewModel, DataRepository repository, City city, boolean visibility) {
        super(viewModel);
        this.mRepository = repository;
        this.city.set(city);
        titleVisibility.set(visibility);
    }

    //点击事件
    public BindingCommand onCityClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click city item");
            mRepository.put(DBConfigs.Settings.LOCATION_ID, JsonUtils.toJson(city.get()));
            RxBus.getDefault().post(city.get());
            viewModel.finish();
        }
    });

    public static List<String> getHotCityId() {
        List<String> hotCities = new ArrayList<>();
        hotCities.add("101010100");//北京
        hotCities.add("101020100");//上海
        hotCities.add("101280101");//广州
        hotCities.add("101280601");//深圳
        hotCities.add("101210101");//杭州
        hotCities.add("101190101");//南京
        hotCities.add("101200101");//武汉
        hotCities.add("101040100");//重庆

        return hotCities;
    }

}
