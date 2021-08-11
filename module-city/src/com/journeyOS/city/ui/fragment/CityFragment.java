package com.journeyOS.city.ui.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.journeyOS.base.router.RouterPath;
import com.journeyOS.city.BR;
import com.journeyOS.city.R;
import com.journeyOS.city.databinding.FragmentCityBinding;
import com.journeyOS.city.ui.viewmodel.CityViewModel;
import com.journeyOS.core.app.AppViewModelFactory;
import com.journeyOS.liteframework.base.BaseFragment;
import com.journeyOS.liteframework.utils.KLog;
import com.journeyOS.liteframework.utils.ToastUtils;
import com.journeyOS.widget.SideLetterBar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

@Route(path = RouterPath.Fragment.City.PAGER_CITY)
public class CityFragment extends BaseFragment<FragmentCityBinding, CityViewModel> {
    private static final String TAG = CityFragment.class.getSimpleName();

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_city;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public CityViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        //因为AppViewModelFactory所在的组件不依赖city组件，所以只能反射构造CityViewModel
        //为了不用每一个XXXViewModel都有到AppViewModelFactory中写死其className
        //所以setMode的目的是为了得到className以方便反射
        //如果不调用setModel，则默认会调用CityViewModel(@NonNull Application application)构造方法
        factory.setModel(CityViewModel.class);
        return ViewModelProviders.of(this, factory).get(CityViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        viewModel.initData();
        binding.side.setOverlay(binding.tvLetterOverlay);
        binding.side.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                KLog.d(TAG, "letter = [" + letter + "]");
                binding.cityList.scrollToPosition(viewModel.getLetterPosition(letter));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationPermissions();
    }

    @Override
    public void initViewObservable() {
    }

    private void requestLocationPermissions() {
        //请求打开定位权限
        RxPermissions rxPermissions = new RxPermissions(this);
        boolean isGranted = rxPermissions.isGranted(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!isGranted) {
            Disposable disposable = rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (!aBoolean) {
                                ToastUtils.showShort(getString(R.string.location_permission));
                            }
                        }
                    });
        }
    }

}
