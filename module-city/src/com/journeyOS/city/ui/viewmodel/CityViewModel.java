package com.journeyOS.city.ui.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;

import com.journeyOS.city.BR;
import com.journeyOS.city.R;
import com.journeyOS.core.data.DataRepository;
import com.journeyOS.core.data.source.local.city.City;
import com.journeyOS.liteframework.base.BaseViewModel;
import com.journeyOS.liteframework.base.MultiItemViewModel;
import com.journeyOS.liteframework.binding.command.BindingAction;
import com.journeyOS.liteframework.binding.command.BindingCommand;
import com.journeyOS.liteframework.binding.command.BindingConsumer;
import com.journeyOS.liteframework.bus.event.SingleLiveEvent;
import com.journeyOS.liteframework.utils.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class CityViewModel extends BaseViewModel<DataRepository> {
    private static final String TAG = CityViewModel.class.getSimpleName();

    private static final String HEAD = "head";
    private static final String HOT = "hot";
    private static final String BODY = "body";

    private List<City> cityList = new ArrayList<>();
    private List<City> hotCityList = new ArrayList<>();

    //封装一个界面发生改变的观察者
    public UIChangeObservable uiChange = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent<Boolean> onFocusChange = new SingleLiveEvent<>();
        public SingleLiveEvent<Boolean> onTextChange = new SingleLiveEvent<>();
    }

    public CityViewModel(@NonNull Application application, DataRepository model) {
        super(application, model);
    }

    public BindingCommand finishClickCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            finish();
        }
    });

    //输入框焦点改变的回调事件
    public BindingCommand<Boolean> onFocusChangeCommand = new BindingCommand<>(new BindingConsumer<Boolean>() {
        @Override
        public void call(Boolean hasFocus) {
            uiChange.onFocusChange.setValue(hasFocus);
        }
    });

    public BindingCommand<String> textChangedCommand = new BindingCommand<>(new BindingConsumer<String>() {
        @Override
        public void call(String text) {
            if (TextUtils.isEmpty(text)) {
                initData();
            } else {
                observableList.clear();
                for (City city : model.matchCity(text)) {
                    CityItemViewModel cityItemViewModel = new CityItemViewModel(CityViewModel.this, CityViewModel.this.model, city, "");
                    cityItemViewModel.setCityLetterVisibility(false);
                    cityItemViewModel.multiItemType(BODY);
                    observableList.add(cityItemViewModel);
                }
            }
        }
    });


    public void initData() {
        if (cityList.size() == 0) {
            cityList = model.getAllCity();
            Collections.sort(cityList, new CityComparator());

            long startTime = System.currentTimeMillis();
            for (City city : cityList) {
                if (CityHotViewModel.getHotCityId().contains(city.locationId)) {
                    hotCityList.add(city);
                }
            }
            long endTime = System.currentTimeMillis();
            KLog.d(TAG, "init hot city time = [" + (endTime - startTime) / 1000 + "]");
        }

        observableList.clear();

        CityHeadViewModel cityHeadViewModel = new CityHeadViewModel(this);
        cityHeadViewModel.multiItemType(HEAD);
        observableList.add(cityHeadViewModel);

        int firstHot = 0;
        for (City city : hotCityList) {
            CityHotViewModel cityHotViewModel = new CityHotViewModel(this, model, city, firstHot == 0);
            firstHot++;
            cityHotViewModel.multiItemType(HOT);
            observableList.add(cityHotViewModel);
        }

        String lastInitial = "";
        for (City city : cityList) {
            boolean cityLetter = false;
            String currentInitial = city.adm1En.substring(0, 1);
            CityItemViewModel cityItemViewModel = new CityItemViewModel(this, model, city, currentInitial);
            if (!lastInitial.equals(currentInitial)) {
                cityLetter = true;
                lastInitial = currentInitial;
            }
            cityItemViewModel.setCityLetterVisibility(cityLetter);
            cityItemViewModel.multiItemType(BODY);
            observableList.add(cityItemViewModel);
        }
    }

    //给RecyclerView添加ObservableList
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();

    //RecyclerView多布局添加ItemBinding
    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局
            String itemType = (String) item.getItemType();
            if (HEAD.equals(itemType)) {
                //定位城市
                itemBinding.set(BR.viewModel, R.layout.item_city_head);
            } else if (HOT.equals(itemType)) {
                //热门城市
                itemBinding.set(BR.viewModel, R.layout.item_city_hot);
            } else if (BODY.equals(itemType)) {
                //所有城市
                itemBinding.set(BR.viewModel, R.layout.item_city_body);
            }
        }
    });


    public int getLetterPosition(String letter) {
        int position = 0;
        for (City city : cityList) {
            if (letter.equalsIgnoreCase(city.adm1En.substring(0, 1))) {
                position = cityList.indexOf(city);
            }
        }
        return position;
    }

    /**
     * 根据省份排序
     */
    private class CityComparator implements Comparator<City> {

        @Override
        public int compare(City cityLeft, City cityRight) {

            char a = cityLeft.adm1En.charAt(0);
            char b = cityRight.adm1En.charAt(0);

            return a - b;
        }
    }
}
