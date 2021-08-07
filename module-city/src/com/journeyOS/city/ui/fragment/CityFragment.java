package com.journeyOS.city.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
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
import com.journeyOS.widget.SideLetterBar;

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
    public void initViewObservable() {
        viewModel.uiChange.onFocusChange.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
//                if (viewModel.uiChange.onFocusChange.getValue()) {
//                    binding.actionEmptyBtn.setVisibility(View.VISIBLE);
//                } else {
//                    binding.actionEmptyBtn.setVisibility(View.GONE);
//                }
            }
        });
    }

}
