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

public class CityItemViewModel extends MultiItemViewModel<CityViewModel> {
    private static final String TAG = CityItemViewModel.class.getSimpleName();

    public ObservableField<Boolean> cityLetterVisibility = new ObservableField<>(false);
    public ObservableField<City> city = new ObservableField<>();
    public ObservableField<String> cityLetter = new ObservableField<>();

    private DataRepository mRepository = null;

    public CityItemViewModel(@NonNull CityViewModel viewModel, DataRepository repository, City city, String cityLetter) {
        super(viewModel);
        this.mRepository = repository;
        this.city.set(city);
        this.cityLetter.set(cityLetter);
    }

    public void setCityLetterVisibility(boolean visibility) {
        cityLetterVisibility.set(visibility);
    }

    public BindingCommand onCityClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click city item");
            mRepository.put(DBConfigs.Settings.LOCATION_ID, JsonUtils.toJson(city.get()));
            RxBus.getDefault().post(city.get());
            viewModel.finish();
        }
    });
}
