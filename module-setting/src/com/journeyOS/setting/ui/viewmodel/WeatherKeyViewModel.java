package com.journeyOS.setting.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.liteframework.base.ItemViewModel;
import com.journeyOS.liteframework.binding.command.BindingAction;
import com.journeyOS.liteframework.binding.command.BindingCommand;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.setting.utils.StringUtils;

public class WeatherKeyViewModel extends ItemViewModel<SettingsViewModel> {
    private static final String TAG = WeatherKeyViewModel.class.getSimpleName();
    public ObservableField<String> entity = new ObservableField<>();
    private String mKey = "";

    public WeatherKeyViewModel(@NonNull SettingsViewModel viewModel, String key) {
        super(viewModel);
        mKey = key;
        entity.set(StringUtils.hideId(key));
    }

    public BindingCommand onClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            KLog.d(TAG, "click item");
            viewModel.saveWeatherKey(mKey);
            viewModel.uiChange.weatherKeyClick.setValue(mKey);
        }
    });

}
