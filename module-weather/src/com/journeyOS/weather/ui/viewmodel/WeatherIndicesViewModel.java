package com.journeyOS.weather.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.journeyOS.core.entity.Indices;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.weather.BR;
import com.journeyOS.weather.R;

import java.util.Collections;
import java.util.Comparator;

import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class WeatherIndicesViewModel extends MultiItemViewModel<WeatherViewModel> {
    private static final String TAG = WeatherIndicesViewModel.class.getSimpleName();

    //给RecyclerView添加ObservableList
    public ObservableList<IndicesViewModel> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<IndicesViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_indices);


    public WeatherIndicesViewModel(@NonNull WeatherViewModel viewModel, Indices indices) {
        super(viewModel);
        parseIndices(indices);
    }

    private void parseIndices(Indices indices) {
        KLog.d(TAG, "indices = " + indices);
        if (indices == null) {
            return;
        }
        if (indices.daily == null) {
            return;
        }

        if (indices.daily.size() == 0) {
            return;
        }

        try {
            observableList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(indices.daily, new NumberComparator());

        //请求成功
        int item = 0;
//        int index = Math.min(INDICES_INDEX, observableList.size());
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
            IndicesViewModel itemViewModel = new IndicesViewModel(viewModel, suggestion, item == 0);
            try {
                observableList.add(itemViewModel);
                item++;
            } catch (Exception e) {
                KLog.d(TAG, "add observable list error = " + e);
            }
        }
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