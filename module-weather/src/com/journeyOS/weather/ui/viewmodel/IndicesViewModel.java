package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.liteframework.base.MultiItemViewModel;


public class IndicesViewModel extends MultiItemViewModel<WeatherViewModel> {
    public ObservableField<Suggestions.Suggestion> entity = new ObservableField<>();
    public ObservableField<Boolean> titleVisibility = new ObservableField<>(false);

    public IndicesViewModel(@NonNull WeatherViewModel viewModel, Suggestions.Suggestion entity, boolean visibility) {
        super(viewModel);
        if (entity != null) {
            this.entity.set(entity);
        }
        titleVisibility.set(visibility);
    }

    public static class Suggestions {
        public static class Suggestion {
            public int drawable;
            public String name;
            public String category;
            public String text;
        }
    }
}