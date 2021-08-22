package com.journeyOS.setting.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.data.source.local.base.DBConfigs;
import com.journeyOS.liteframework.base.ItemViewModel;
import com.journeyOS.liteframework.binding.command.BindingAction;
import com.journeyOS.liteframework.binding.command.BindingCommand;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.setting.R;

public class WeatherTimeViewModel extends ItemViewModel<SettingsViewModel> {
    private static final String TAG = WeatherTimeViewModel.class.getSimpleName();
    public ObservableField<String> entity = new ObservableField<>();
    private int mTime = DBConfigs.Settings.WEATHER_TIME_DEFAULT;

    public WeatherTimeViewModel(@NonNull SettingsViewModel viewModel, int key) {
        super(viewModel);
        mTime = key;
        entity.set(String.format(viewModel.getApplication().getResources().getString(R.string.weather_time_diff), String.valueOf(key)));
    }

    public BindingCommand onClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click item");
            viewModel.saveWeatherTime(mTime);
            viewModel.uiChange.weatherTimeClick.setValue(String.valueOf(mTime));
        }
    });

}
