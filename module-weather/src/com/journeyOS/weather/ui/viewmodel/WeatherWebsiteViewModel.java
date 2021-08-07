package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;

import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.liteframework.binding.command.BindingAction;
import com.journeyOS.liteframework.binding.command.BindingCommand;
import com.journeyOS.liteframework.utils.KLog;

public class WeatherWebsiteViewModel extends MultiItemViewModel<WeatherViewModel> {
    private static final String TAG = WeatherWebsiteViewModel.class.getSimpleName();

    public WeatherWebsiteViewModel(@NonNull WeatherViewModel viewModel) {
        super(viewModel);
    }

    public BindingCommand onClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click item");
        }
    });

}