package com.journeyOS.liteweather.ui.weather.fragment;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.journeyOS.liteframework.base.ItemViewModel;

public class IndicesViewModel extends ItemViewModel<WeatherViewModel> {
    public ObservableField<Suggestions.Suggestion> entity = new ObservableField<>();

    public IndicesViewModel(@NonNull WeatherViewModel viewModel, Suggestions.Suggestion entity) {
        super(viewModel);
        this.entity.set(entity);
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