/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.plugins.city.ui.search;


import androidx.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.city.ui.adapter.CityHolder;
import com.journeyOS.plugins.city.ui.adapter.CityInfoData;
import com.journeyOS.plugins.city.ui.adapter.HeaderData;
import com.journeyOS.plugins.city.ui.adapter.HeaderHolder;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.base.view.SideLetterBar;
import com.journeyOS.core.viewmodel.ModelProvider;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {

    @BindView(R2.id.hot_city_list)
    RecyclerView mAllCitiesRecyclerView;
    @BindView(R2.id.tv_letter_overlay)
    TextView mTvLetterOverlay;
    @BindView(R2.id.side)
    SideLetterBar mSide;
    @BindView(R2.id.searchTextView)
    EditText mSearchTextView;
    @BindView(R2.id.action_empty_btn)
    ImageButton mActionEmptyBtn;
    @BindView(R2.id.search_result_view)
    RecyclerView mSearchResultView;
    @BindView(R2.id.empty_view)
    LinearLayout mEmptyView;
    private SearchModel mSearchModel;

    private BaseRecyclerAdapter mSearchResultAdapter;

    public static void navigationActivity(Context from) {
        Intent intent = new Intent(from, SearchActivity.class);
        from.startActivity(intent);
    }

    public static void navigationFromApplication(Context from) {
        Intent intent = new Intent(from, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    @Override
    public int getContentViewId() {
        return R.layout.city_activity_search;
    }


    @Override
    public void initViews() {
        initSearchView();
    }

    @Override
    protected void initDataObserver() {
        super.initDataObserver();
        mSearchModel = ModelProvider.getModel(this,SearchModel.class);
        mSearchModel.getAllCities();
        mSearchModel.getAllCityData().observe(this, new Observer<List<CityInfoData>>() {
            @Override
            public void onChanged(@Nullable List<CityInfoData> cityInfoData) {
                onAllCities(cityInfoData);
            }
        });
        mSearchModel.getMatchedCityData().observe(this, new Observer<List<CityInfoData>>() {
            @Override
            public void onChanged(@Nullable List<CityInfoData> cityInfoData) {
                onMatched(cityInfoData);
            }
        });

    }

    private int getLetterPosition(String letter, List<CityInfoData> allInfoDatas) {
        int position = 0;
        for (CityInfoData cityInfoData : allInfoDatas) {
            if (letter.equalsIgnoreCase(cityInfoData.getInitial())) {
                position = allInfoDatas.indexOf(cityInfoData);
            }
        }
        return position;
    }


    public void onMatched(List<CityInfoData> result) {
        if (BaseUtils.isEmpty(result)) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mSearchResultAdapter.setData(result);

        }
    }


    public void onAllCities(final List<CityInfoData> allInfoDatas) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAllCitiesRecyclerView.setLayoutManager(linearLayoutManager);
        BaseRecyclerAdapter citiesAdapter = new BaseRecyclerAdapter(this);
        citiesAdapter.registerHolder(HeaderHolder.class, new HeaderData());
        citiesAdapter.registerHolder(CityHolder.class, allInfoDatas);
        mAllCitiesRecyclerView.setAdapter(citiesAdapter);

        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(this);
        mSearchResultView.setLayoutManager(resultLayoutManager);
        mSearchResultAdapter = new BaseRecyclerAdapter(this);
        mSearchResultAdapter.registerHolder(CityHolder.class, R.layout.city_item_city);
        mSearchResultView.setAdapter(mSearchResultAdapter);

        mSide.setOverlay(mTvLetterOverlay);
        mSide.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                linearLayoutManager.scrollToPositionWithOffset(getLetterPosition(letter, allInfoDatas), 0);
            }
        });
    }

    private void initSearchView() {
        setCursorDrawable(R.drawable.city_color_cursor_white);

        mSearchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                return true;
            }
        });

        mSearchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CharSequence text = mSearchTextView.getText();
                boolean hasText = !TextUtils.isEmpty(text);
                if (hasText) {
                    mActionEmptyBtn.setVisibility(View.VISIBLE);
                } else {
                    mActionEmptyBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    mActionEmptyBtn.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    mSearchResultView.setVisibility(View.GONE);
                } else {
                    mActionEmptyBtn.setVisibility(View.VISIBLE);
                    mSearchResultView.setVisibility(View.VISIBLE);
                    mSearchModel.matchCities(keyword);
                }
            }
        });

    }


    public void setCursorDrawable(int drawable) {
        try {
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(mSearchTextView, drawable);
        } catch (Exception ignored) {
            //LogUtils.d(TAG, "");
        }
    }

    @OnClick({R2.id.action_back, R2.id.action_empty_btn})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.action_back) {
            finish();
        } else if (i == R.id.action_empty_btn) {
            mSearchTextView.setText("");
        }
    }
}
